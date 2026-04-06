package com.edumark.score.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.entity.ExamSubject;
import com.edumark.exam.mapper.ExamClassMapper;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.exam.mapper.ExamSubjectMapper;
import com.edumark.marking.entity.MarkingArbitration;
import com.edumark.marking.entity.MarkingTask;
import com.edumark.marking.mapper.MarkingArbitrationMapper;
import com.edumark.marking.mapper.MarkingTaskMapper;
import com.edumark.exam.vo.ExamVO;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.school.entity.Student;
import com.edumark.school.mapper.StudentMapper;
import com.edumark.score.dto.ScoreQueryDTO;
import com.edumark.score.entity.*;
import com.edumark.score.mapper.*;
import com.edumark.score.service.ScoreService;
import com.edumark.score.vo.ExamScoreVO;
import com.edumark.score.vo.ScorePublishCheckVO;
import com.edumark.score.vo.ScoreStatisticsVO;
import com.edumark.score.vo.SubjectScoreVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成绩服务实现
 *
 * @author EduMark
 */
@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource
    private ExamScoreMapper examScoreMapper;

    @Resource
    private SubjectScoreMapper subjectScoreMapper;

    @Resource
    private ScoreStatisticsMapper statisticsMapper;

    @Resource
    private ScorePublishRecordMapper publishRecordMapper;

    @Resource
    private ExamMapper examMapper;

    @Resource
    private ExamSubjectMapper examSubjectMapper;

    @Resource
    private ExamClassMapper examClassMapper;

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private MarkingTaskMapper markingTaskMapper;

    @Resource
    private MarkingArbitrationMapper markingArbitrationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aggregateScores(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        ensureExamReadyForScoreProcessing(exam);

        // 清除旧数据
        examScoreMapper.deleteByExamId(examId);
        subjectScoreMapper.deleteByExamId(examId);

        // 查询考试科目
        List<ExamSubject> subjects = examSubjectMapper.selectList(
                new LambdaQueryWrapper<ExamSubject>()
                        .eq(ExamSubject::getExamId, examId)
                        .eq(ExamSubject::getStatus, 1)
        );

        // 查询所有答题卡(已完成阅卷的)
        List<AnswerSheet> answerSheets = answerSheetMapper.selectList(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .eq(AnswerSheet::getStatus, 4) // 已完成
        );

        // 按学生分组
        Map<Long, List<AnswerSheet>> studentSheets = answerSheets.stream()
                .collect(Collectors.groupingBy(AnswerSheet::getStudentId));

        // 查询考试的所有班级学生
        List<Long> classIds = examClassMapper.selectList(
                new LambdaQueryWrapper<com.edumark.exam.entity.ExamClass>()
                        .eq(com.edumark.exam.entity.ExamClass::getExamId, examId)
        ).stream().map(com.edumark.exam.entity.ExamClass::getClassId).toList();

        // 查询所有应参加考试的学生
        Set<Long> allStudentIds = new HashSet<>();
        if (!classIds.isEmpty()) {
            List<Student> allStudents = studentMapper.selectList(
                    new LambdaQueryWrapper<Student>()
                            .in(Student::getClassId, classIds)
                            .eq(Student::getDeleted, 0)
            );
            allStudentIds = allStudents.stream().map(Student::getId).collect(Collectors.toSet());
        }

        // 查询学生信息
        Map<Long, Student> studentMap = new HashMap<>();
        if (!allStudentIds.isEmpty()) {
            List<Student> students = studentMapper.selectBatchIds(allStudentIds);
            studentMap = students.stream().collect(Collectors.toMap(Student::getId, s -> s));
        }

        // 汇总各科成绩和总分（包括缺考学生）
        for (Long studentId : allStudentIds) {
            Student student = studentMap.get(studentId);
            if (student == null) continue;

            List<AnswerSheet> sheets = studentSheets.getOrDefault(studentId, new ArrayList<>());

            BigDecimal totalScore = BigDecimal.ZERO;
            int subjectCount = 0;

            // 为每个科目保存成绩（有答题卡的用实际成绩，没有的记0分缺考）
            for (ExamSubject subject : subjects) {
                AnswerSheet sheet = sheets.stream()
                        .filter(s -> subject.getId().equals(s.getExamSubjectId()))
                        .findFirst()
                        .orElse(null);

                SubjectScore subjectScore = new SubjectScore();
                subjectScore.setExamId(examId);
                subjectScore.setExamSubjectId(subject.getId());
                subjectScore.setStudentId(studentId);
                subjectScore.setClassId(student.getClassId());

                if (sheet != null) {
                    // 有答题卡，使用实际成绩
                    subjectScore.setAnswerSheetId(sheet.getId());
                    subjectScore.setScore(BigDecimal.valueOf(sheet.getTotalScore() != null ? sheet.getTotalScore() : 0));
                    subjectScore.setObjectiveScore(BigDecimal.valueOf(sheet.getObjectiveScore() != null ? sheet.getObjectiveScore() : 0));
                    subjectScore.setSubjectiveScore(BigDecimal.valueOf(sheet.getSubjectiveScore() != null ? sheet.getSubjectiveScore() : 0));
                } else {
                    // 缺考，记0分
                    subjectScore.setAnswerSheetId(null);
                    subjectScore.setScore(BigDecimal.ZERO);
                    subjectScore.setObjectiveScore(BigDecimal.ZERO);
                    subjectScore.setSubjectiveScore(BigDecimal.ZERO);
                }
                subjectScore.setCreateTime(LocalDateTime.now());
                subjectScoreMapper.insert(subjectScore);

                totalScore = totalScore.add(subjectScore.getScore());
                subjectCount++;
            }

            // 保存总成绩
            ExamScore examScore = new ExamScore();
            examScore.setExamId(examId);
            examScore.setStudentId(studentId);
            examScore.setClassId(student.getClassId());
            examScore.setTotalScore(totalScore);
            examScore.setSubjectCount(subjectCount);
            examScore.setCreateTime(LocalDateTime.now());
            examScoreMapper.insert(examScore);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateRanking(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        ensureExamReadyForScoreProcessing(exam);

        // 计算年级排名(总分)
        List<ExamScore> examScores = examScoreMapper.selectListByExamId(examId);
        examScores.sort((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()));

        int gradeRank = 0;
        BigDecimal lastScore = null;
        for (int i = 0; i < examScores.size(); i++) {
            ExamScore score = examScores.get(i);
            if (lastScore == null || score.getTotalScore().compareTo(lastScore) != 0) {
                gradeRank = i + 1;
            }
            score.setGradeRank(gradeRank);
            lastScore = score.getTotalScore();
        }

        // 计算班级排名(总分)
        Map<Long, List<ExamScore>> classScores = examScores.stream()
                .collect(Collectors.groupingBy(ExamScore::getClassId));

        for (List<ExamScore> scores : classScores.values()) {
            scores.sort((a, b) -> b.getTotalScore().compareTo(a.getTotalScore()));
            int classRank = 0;
            lastScore = null;
            for (int i = 0; i < scores.size(); i++) {
                ExamScore score = scores.get(i);
                if (lastScore == null || score.getTotalScore().compareTo(lastScore) != 0) {
                    classRank = i + 1;
                }
                score.setClassRank(classRank);
                lastScore = score.getTotalScore();
            }
        }

        // 更新排名
        for (ExamScore score : examScores) {
            score.setUpdateTime(LocalDateTime.now());
            examScoreMapper.updateById(score);
        }

        // 计算各科目排名
        List<ExamSubject> subjects = examSubjectMapper.selectList(
                new LambdaQueryWrapper<ExamSubject>().eq(ExamSubject::getExamId, examId)
        );

        for (ExamSubject subject : subjects) {
            List<SubjectScore> subjectScores = subjectScoreMapper.selectListByExamSubjectId(subject.getId());

            // 年级排名
            subjectScores.sort((a, b) -> b.getScore().compareTo(a.getScore()));
            gradeRank = 0;
            lastScore = null;
            for (int i = 0; i < subjectScores.size(); i++) {
                SubjectScore score = subjectScores.get(i);
                if (lastScore == null || score.getScore().compareTo(lastScore) != 0) {
                    gradeRank = i + 1;
                }
                score.setGradeRank(gradeRank);
                lastScore = score.getScore();
            }

            // 班级排名
            Map<Long, List<SubjectScore>> classSubjectScores = subjectScores.stream()
                    .collect(Collectors.groupingBy(SubjectScore::getClassId));

            for (List<SubjectScore> scores : classSubjectScores.values()) {
                scores.sort((a, b) -> b.getScore().compareTo(a.getScore()));
                int classRank = 0;
                lastScore = null;
                for (int i = 0; i < scores.size(); i++) {
                    SubjectScore score = scores.get(i);
                    if (lastScore == null || score.getScore().compareTo(lastScore) != 0) {
                        classRank = i + 1;
                    }
                    score.setClassRank(classRank);
                    lastScore = score.getScore();
                }
            }

            // 更新排名
            for (SubjectScore score : subjectScores) {
                score.setUpdateTime(LocalDateTime.now());
                subjectScoreMapper.updateById(score);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateStatistics(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        ensureExamReadyForScoreProcessing(exam);

        // 清除旧统计数据
        statisticsMapper.deleteByExamId(examId);

        List<ExamSubject> subjects = examSubjectMapper.selectList(
                new LambdaQueryWrapper<ExamSubject>().eq(ExamSubject::getExamId, examId)
        );
        List<ExamVO.ExamClassVO> examClasses = examClassMapper.selectListByExamId(examId);

        // 1. 班级科目统计
        for (ExamVO.ExamClassVO examClass : examClasses) {
            for (ExamSubject subject : subjects) {
                calculateClassSubjectStat(examId, subject, examClass.getClassId());
            }
            // 班级总分统计
            calculateClassTotalStat(examId, examClass.getClassId(), subjects);
        }

        // 2. 年级科目统计
        for (ExamSubject subject : subjects) {
            calculateGradeSubjectStat(examId, subject);
        }

        // 3. 年级总分统计
        calculateGradeTotalStat(examId, subjects);
    }

    private void calculateClassSubjectStat(Long examId, ExamSubject subject, Long classId) {
        List<SubjectScore> scores = subjectScoreMapper.selectList(
                new LambdaQueryWrapper<SubjectScore>()
                        .eq(SubjectScore::getExamSubjectId, subject.getId())
                        .eq(SubjectScore::getClassId, classId)
        );

        if (scores.isEmpty()) return;

        ScoreStatistics stat = createStatistics(scores.stream().map(SubjectScore::getScore).toList(),
                subject.getFullScore(), subject.getPassScore(), subject.getExcellentScore());
        stat.setExamId(examId);
        stat.setExamSubjectId(subject.getId());
        stat.setClassId(classId);
        stat.setStatType(1); // 班级科目
        stat.setFullScore(BigDecimal.valueOf(subject.getFullScore()));
        statisticsMapper.insert(stat);
    }

    private void calculateClassTotalStat(Long examId, Long classId, List<ExamSubject> subjects) {
        List<ExamScore> scores = examScoreMapper.selectList(
                new LambdaQueryWrapper<ExamScore>()
                        .eq(ExamScore::getExamId, examId)
                        .eq(ExamScore::getClassId, classId)
        );

        if (scores.isEmpty()) return;

        int fullScore = subjects.stream().mapToInt(ExamSubject::getFullScore).sum();
        int passScore = (int) (fullScore * 0.6);
        int excellentScore = (int) (fullScore * 0.85);

        ScoreStatistics stat = createStatistics(scores.stream().map(ExamScore::getTotalScore).toList(),
                fullScore, passScore, excellentScore);
        stat.setExamId(examId);
        stat.setClassId(classId);
        stat.setStatType(2); // 班级总分
        stat.setFullScore(BigDecimal.valueOf(fullScore));
        statisticsMapper.insert(stat);
    }

    private void calculateGradeSubjectStat(Long examId, ExamSubject subject) {
        List<SubjectScore> scores = subjectScoreMapper.selectList(
                new LambdaQueryWrapper<SubjectScore>()
                        .eq(SubjectScore::getExamSubjectId, subject.getId())
        );

        if (scores.isEmpty()) return;

        ScoreStatistics stat = createStatistics(scores.stream().map(SubjectScore::getScore).toList(),
                subject.getFullScore(), subject.getPassScore(), subject.getExcellentScore());
        stat.setExamId(examId);
        stat.setExamSubjectId(subject.getId());
        stat.setStatType(3); // 年级科目
        stat.setFullScore(BigDecimal.valueOf(subject.getFullScore()));
        statisticsMapper.insert(stat);
    }

    private void calculateGradeTotalStat(Long examId, List<ExamSubject> subjects) {
        List<ExamScore> scores = examScoreMapper.selectListByExamId(examId);

        if (scores.isEmpty()) return;

        int fullScore = subjects.stream().mapToInt(ExamSubject::getFullScore).sum();
        int passScore = (int) (fullScore * 0.6);
        int excellentScore = (int) (fullScore * 0.85);

        ScoreStatistics stat = createStatistics(scores.stream().map(ExamScore::getTotalScore).toList(),
                fullScore, passScore, excellentScore);
        stat.setExamId(examId);
        stat.setStatType(4); // 年级总分
        stat.setFullScore(BigDecimal.valueOf(fullScore));
        statisticsMapper.insert(stat);
    }

    private ScoreStatistics createStatistics(List<BigDecimal> scores, int fullScore, Integer passScore, Integer excellentScore) {
        ScoreStatistics stat = new ScoreStatistics();
        stat.setStudentCount(scores.size());

        BigDecimal max = scores.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal min = scores.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal sum = scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = sum.divide(BigDecimal.valueOf(scores.size()), 1, RoundingMode.HALF_UP);

        stat.setMaxScore(max);
        stat.setMinScore(min);
        stat.setAvgScore(avg);

        // 及格率
        if (passScore != null) {
            long passCount = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(passScore)) >= 0).count();
            stat.setPassCount((int) passCount);
            stat.setPassRate(BigDecimal.valueOf(passCount * 100.0 / scores.size()).setScale(2, RoundingMode.HALF_UP));
        }

        // 优秀率
        if (excellentScore != null) {
            long excellentCount = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(excellentScore)) >= 0).count();
            stat.setExcellentCount((int) excellentCount);
            stat.setExcellentRate(BigDecimal.valueOf(excellentCount * 100.0 / scores.size()).setScale(2, RoundingMode.HALF_UP));
        }

        // 分数段统计
        Map<String, Integer> segments = new LinkedHashMap<>();
        int[] bounds = {0, 60, 70, 80, 90, 100};
        for (int i = 0; i < bounds.length - 1; i++) {
            int low = (int) (fullScore * bounds[i] / 100.0);
            int high = (int) (fullScore * bounds[i + 1] / 100.0);
            String key = bounds[i] + "-" + bounds[i + 1];
            int finalI = i;
            long count = scores.stream().filter(s -> {
                double score = s.doubleValue();
                if (finalI == bounds.length - 2) {
                    return score >= low && score <= high;
                }
                return score >= low && score < high;
            }).count();
            segments.put(key, (int) count);
        }
        stat.setScoreSegments(segments);

        stat.setCreateTime(LocalDateTime.now());
        return stat;
    }

    @Override
    public PageResult<ExamScoreVO> pageExamScores(ScoreQueryDTO query) {
        Page<ExamScoreVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        examScoreMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<SubjectScoreVO> pageSubjectScores(ScoreQueryDTO query) {
        Page<SubjectScoreVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        subjectScoreMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public ExamScoreVO getStudentExamScore(Long examId, Long studentId) {
        ExamScoreVO vo = examScoreMapper.selectVOByExamAndStudent(examId, studentId);
        if (vo != null) {
            List<SubjectScoreVO> subjectScores = subjectScoreMapper.selectListByExamAndStudent(examId, studentId);
            vo.setSubjectScores(subjectScores);
        }
        return vo;
    }

    @Override
    public List<ScoreStatisticsVO> getStatistics(Long examId, Long examSubjectId, Long classId) {
        List<ScoreStatisticsVO> result = new ArrayList<>();

        if (examSubjectId != null && classId != null) {
            // 班级科目统计
            result = statisticsMapper.selectList(
                    new LambdaQueryWrapper<ScoreStatistics>()
                            .eq(ScoreStatistics::getExamId, examId)
                            .eq(ScoreStatistics::getExamSubjectId, examSubjectId)
                            .eq(ScoreStatistics::getClassId, classId)
            ).stream().map(this::toVO).toList();
        } else if (examSubjectId != null) {
            // 科目的班级对比
            result = statisticsMapper.selectClassStatBySubject(examSubjectId);
        } else if (classId != null) {
            // 班级各科统计
            result = statisticsMapper.selectList(
                    new LambdaQueryWrapper<ScoreStatistics>()
                            .eq(ScoreStatistics::getExamId, examId)
                            .eq(ScoreStatistics::getClassId, classId)
                            .in(ScoreStatistics::getStatType, 1, 2)
            ).stream().map(this::toVO).toList();
        } else {
            // 年级整体统计
            result = statisticsMapper.selectListByExamId(examId);
        }

        // 转换分数段为列表
        for (ScoreStatisticsVO vo : result) {
            if (vo.getScoreSegments() != null) {
                List<ScoreStatisticsVO.ScoreSegmentVO> segmentList = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : vo.getScoreSegments().entrySet()) {
                    segmentList.add(new ScoreStatisticsVO.ScoreSegmentVO(entry.getKey(), entry.getValue()));
                }
                vo.setSegmentList(segmentList);
            }
        }

        return result;
    }

    private ScoreStatisticsVO toVO(ScoreStatistics stat) {
        ScoreStatisticsVO vo = new ScoreStatisticsVO();
        vo.setId(stat.getId());
        vo.setExamId(stat.getExamId());
        vo.setExamSubjectId(stat.getExamSubjectId());
        vo.setClassId(stat.getClassId());
        vo.setStatType(stat.getStatType());
        vo.setStudentCount(stat.getStudentCount());
        vo.setFullScore(stat.getFullScore());
        vo.setMaxScore(stat.getMaxScore());
        vo.setMinScore(stat.getMinScore());
        vo.setAvgScore(stat.getAvgScore());
        vo.setPassCount(stat.getPassCount());
        vo.setPassRate(stat.getPassRate());
        vo.setExcellentCount(stat.getExcellentCount());
        vo.setExcellentRate(stat.getExcellentRate());
        vo.setScoreSegments(stat.getScoreSegments());
        return vo;
    }

    private void ensureExamReadyForScoreProcessing(Exam exam) {
        if (exam.getStatus() == null || exam.getStatus() < 4) {
            throw new BusinessException("考试尚未完成，不能生成正式成绩数据");
        }
    }

    @Override
    public ScorePublishCheckVO getPublishCheck(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        int subjectCount = Math.toIntExact(examSubjectMapper.selectCount(
                new LambdaQueryWrapper<ExamSubject>()
                        .eq(ExamSubject::getExamId, examId)
                        .eq(ExamSubject::getDeleted, 0)
        ));

        int answerSheetCount = countAnswerSheets(examId, null);
        int completedAnswerSheetCount = countAnswerSheets(examId, 4);
        int pendingRecognitionCount = countAnswerSheets(examId, 0);
        int recognitionExceptionCount = countAnswerSheets(examId, 5);
        int pendingMarkingAnswerSheetCount = Math.toIntExact(answerSheetMapper.selectCount(
                new LambdaQueryWrapper<AnswerSheet>()
                        .eq(AnswerSheet::getExamId, examId)
                        .in(AnswerSheet::getStatus, 1, 2, 3)
        ));

        List<MarkingTask> markingTasks = markingTaskMapper.selectList(
                new LambdaQueryWrapper<MarkingTask>()
                        .eq(MarkingTask::getExamId, examId)
                        .eq(MarkingTask::getDeleted, 0)
        );
        int markingTaskCount = markingTasks.size();
        int unfinishedTaskCount = (int) markingTasks.stream()
                .filter(task -> task.getStatus() == null || task.getStatus() != 2
                        || (task.getPendingCount() != null && task.getPendingCount() > 0))
                .count();

        List<Long> taskIds = markingTasks.stream()
                .map(MarkingTask::getId)
                .toList();
        int pendingArbitrationCount = taskIds.isEmpty() ? 0 : Math.toIntExact(markingArbitrationMapper.selectCount(
                new LambdaQueryWrapper<MarkingArbitration>()
                        .in(MarkingArbitration::getTaskId, taskIds)
                        .eq(MarkingArbitration::getStatus, 0)
        ));

        int examScoreCount = Math.toIntExact(examScoreMapper.selectCount(
                new LambdaQueryWrapper<ExamScore>()
                        .eq(ExamScore::getExamId, examId)
        ));
        int subjectScoreCount = Math.toIntExact(subjectScoreMapper.selectCount(
                new LambdaQueryWrapper<SubjectScore>()
                        .eq(SubjectScore::getExamId, examId)
        ));
        int statisticsCount = Math.toIntExact(statisticsMapper.selectCount(
                new LambdaQueryWrapper<ScoreStatistics>()
                        .eq(ScoreStatistics::getExamId, examId)
        ));

        List<String> blockingItems = new ArrayList<>();
        List<String> warningItems = new ArrayList<>();

        if (exam.getStatus() == null || exam.getStatus() < 4) {
            blockingItems.add("考试状态尚未到“已完成”，当前还不能正式出分");
        }
        if (pendingRecognitionCount > 0) {
            blockingItems.add("仍有 " + pendingRecognitionCount + " 份答题卡处于识别中");
        }
        if (recognitionExceptionCount > 0) {
            blockingItems.add("仍有 " + recognitionExceptionCount + " 份答题卡在异常池");
        }
        if (pendingMarkingAnswerSheetCount > 0) {
            blockingItems.add("仍有 " + pendingMarkingAnswerSheetCount + " 份答题卡未完成阅卷");
        }
        if (unfinishedTaskCount > 0) {
            blockingItems.add("仍有 " + unfinishedTaskCount + " 个阅卷任务未完成");
        }
        if (pendingArbitrationCount > 0) {
            blockingItems.add("仍有 " + pendingArbitrationCount + " 条仲裁待处理");
        }

        if (answerSheetCount == 0) {
            warningItems.add("当前考试还没有答题卡数据");
        }
        if (examScoreCount == 0) {
            warningItems.add("尚未生成总分汇总记录，发布时会自动执行汇总");
        }
        if (subjectScoreCount == 0) {
            warningItems.add("尚未生成科目成绩记录，发布时会自动计算");
        }
        if (statisticsCount == 0) {
            warningItems.add("尚未生成统计分析，发布时会自动计算");
        }

        ScorePublishCheckVO vo = new ScorePublishCheckVO();
        vo.setExamId(examId);
        vo.setExamName(exam.getName());
        vo.setExamStatus(exam.getStatus());
        vo.setCanPublish(blockingItems.isEmpty());
        vo.setSubjectCount(subjectCount);
        vo.setAnswerSheetCount(answerSheetCount);
        vo.setCompletedAnswerSheetCount(completedAnswerSheetCount);
        vo.setPendingRecognitionCount(pendingRecognitionCount);
        vo.setRecognitionExceptionCount(recognitionExceptionCount);
        vo.setPendingMarkingAnswerSheetCount(pendingMarkingAnswerSheetCount);
        vo.setMarkingTaskCount(markingTaskCount);
        vo.setUnfinishedTaskCount(unfinishedTaskCount);
        vo.setPendingArbitrationCount(pendingArbitrationCount);
        vo.setExamScoreCount(examScoreCount);
        vo.setSubjectScoreCount(subjectScoreCount);
        vo.setStatisticsCount(statisticsCount);
        vo.setBlockingItems(blockingItems);
        vo.setWarningItems(warningItems);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long examId, Long userId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (exam.getStatus() == 5) {
            throw new BusinessException("成绩已发布");
        }
        if (exam.getStatus() < 4) {
            throw new BusinessException("阅卷未完成，不能发布成绩");
        }

        // 汇总成绩
        aggregateScores(examId);

        // 计算排名
        calculateRanking(examId);

        // 计算统计
        calculateStatistics(examId);

        // 更新考试状态
        exam.setStatus(5);
        examMapper.updateById(exam);

        // 记录发布
        ScorePublishRecord record = new ScorePublishRecord();
        record.setExamId(examId);
        record.setPublishType(1);
        record.setPublishTime(LocalDateTime.now());
        record.setPublishBy(userId);
        record.setCreateTime(LocalDateTime.now());
        publishRecordMapper.insert(record);
    }

    private int countAnswerSheets(Long examId, Integer status) {
        LambdaQueryWrapper<AnswerSheet> wrapper = new LambdaQueryWrapper<AnswerSheet>()
                .eq(AnswerSheet::getExamId, examId);
        if (status != null) {
            wrapper.eq(AnswerSheet::getStatus, status);
        }
        return Math.toIntExact(answerSheetMapper.selectCount(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublish(Long examId, Long userId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        if (exam.getStatus() != 5) {
            throw new BusinessException("成绩未发布");
        }

        // 更新考试状态
        exam.setStatus(4);
        examMapper.updateById(exam);

        // 记录撤回
        ScorePublishRecord record = new ScorePublishRecord();
        record.setExamId(examId);
        record.setPublishType(2);
        record.setPublishTime(LocalDateTime.now());
        record.setPublishBy(userId);
        record.setCreateTime(LocalDateTime.now());
        publishRecordMapper.insert(record);
    }

    @Override
    public byte[] exportExcel(Long examId, Long classId) {
        ScoreQueryDTO query = new ScoreQueryDTO();
        query.setExamId(examId);
        query.setClassId(classId);
        query.setPageNum(1);
        query.setPageSize(10000);

        Page<ExamScoreVO> page = new Page<>(1, 10000);
        examScoreMapper.selectPageVO(page, query);
        List<ExamScoreVO> scores = page.getRecords();

        // 查询考试科目列表（用于确定导出列顺序）
        List<ExamSubject> subjects = examSubjectMapper.selectList(
                new LambdaQueryWrapper<ExamSubject>()
                        .eq(ExamSubject::getExamId, examId)
                        .eq(ExamSubject::getStatus, 1)
                        .orderByAsc(ExamSubject::getSort)
                        .orderByAsc(ExamSubject::getId)
        );

        // 查询各科成绩
        for (ExamScoreVO score : scores) {
            List<SubjectScoreVO> subjectScores = subjectScoreMapper.selectListByExamAndStudent(examId, score.getStudentId());
            score.setSubjectScores(subjectScores);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 转换为导出数据（包含各科明细）
            List<ScoreExportData> exportData = scores.stream().map(s -> {
                ScoreExportData data = new ScoreExportData();
                data.setStudentName(s.getStudentName());
                data.setStudentNumber(s.getStudentNumber());
                data.setClassName(s.getClassName());
                data.setTotalScore(s.getTotalScore() != null ? s.getTotalScore().toString() : "");
                data.setClassRank(s.getClassRank() != null ? s.getClassRank().toString() : "");
                data.setGradeRank(s.getGradeRank() != null ? s.getGradeRank().toString() : "");

                // 设置各科成绩
                if (s.getSubjectScores() != null && !subjects.isEmpty()) {
                    Map<Long, SubjectScoreVO> subjectScoreMap = s.getSubjectScores().stream()
                            .collect(Collectors.toMap(SubjectScoreVO::getExamSubjectId, sc -> sc, (a, b) -> a));

                    String[] subjectScores = new String[subjects.size()];
                    for (int i = 0; i < subjects.size(); i++) {
                        SubjectScoreVO ss = subjectScoreMap.get(subjects.get(i).getId());
                        subjectScores[i] = ss != null && ss.getScore() != null ? ss.getScore().toString() : "0";
                    }
                    data.setSubjectScores(subjectScores);
                }
                return data;
            }).toList();

            // 动态设置表头（包含各科目）
            List<List<String>> headers = new ArrayList<>();
            headers.add(Arrays.asList("基本信息", "姓名"));
            headers.add(Arrays.asList("基本信息", "学号"));
            headers.add(Arrays.asList("基本信息", "班级"));
            // 各科成绩列
            for (ExamSubject subject : subjects) {
                headers.add(Arrays.asList("各科成绩", subject.getSubjectName()));
            }
            headers.add(Arrays.asList("汇总", "总分"));
            headers.add(Arrays.asList("排名", "班级排名"));
            headers.add(Arrays.asList("排名", "年级排名"));

            // 写入Excel
            EasyExcel.write(baos)
                    .head(createDynamicHead(headers))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("成绩")
                    .doWrite(exportData.stream().map(data -> {
                List<Object> row = new ArrayList<>();
                row.add(data.getStudentName());
                row.add(data.getStudentNumber());
                row.add(data.getClassName());
                // 各科成绩
                if (data.getSubjectScores() != null) {
                    for (String score : data.getSubjectScores()) {
                        row.add(score);
                    }
                } else {
                    for (int i = 0; i < subjects.size(); i++) {
                        row.add("0");
                    }
                }
                row.add(data.getTotalScore());
                row.add(data.getClassRank());
                row.add(data.getGradeRank());
                return row;
            }).toList());

            return baos.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 创建动态表头
     */
    private List<List<String>> createDynamicHead(List<List<String>> headers) {
        return headers;
    }

    /**
     * 成绩导出数据类
     */
    public static class ScoreExportData {
        private String studentName;
        private String studentNumber;
        private String className;
        private String[] subjectScores;  // 各科成绩数组，顺序与科目列表一致
        private String totalScore;
        private String classRank;
        private String gradeRank;

        // Getters and Setters
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public String getStudentNumber() { return studentNumber; }
        public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public String[] getSubjectScores() { return subjectScores; }
        public void setSubjectScores(String[] subjectScores) { this.subjectScores = subjectScores; }

        public String getTotalScore() { return totalScore; }
        public void setTotalScore(String totalScore) { this.totalScore = totalScore; }

        public String getClassRank() { return classRank; }
        public void setClassRank(String classRank) { this.classRank = classRank; }

        public String getGradeRank() { return gradeRank; }
        public void setGradeRank(String gradeRank) { this.gradeRank = gradeRank; }
    }
}
