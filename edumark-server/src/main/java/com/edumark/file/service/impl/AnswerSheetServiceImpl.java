package com.edumark.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.exam.entity.Exam;
import com.edumark.exam.mapper.ExamMapper;
import com.edumark.file.dto.AnswerSheetDTO;
import com.edumark.file.dto.AnswerSheetQueryDTO;
import com.edumark.file.dto.AnswerSheetUploadDTO;
import com.edumark.file.dto.FileUploadResult;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetImage;
import com.edumark.file.mapper.AnswerSheetImageMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.recognition.BarcodeRecognitionResult;
import com.edumark.file.recognition.BarcodeRecognitionService;
import com.edumark.file.recognition.RecognitionImageInput;
import com.edumark.file.service.AnswerSheetDetailService;
import com.edumark.file.service.AnswerSheetRecognitionAsyncService;
import com.edumark.file.service.AnswerSheetService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.AnswerSheetImageVO;
import com.edumark.file.vo.AnswerSheetVO;
import com.edumark.school.entity.Student;
import com.edumark.school.mapper.StudentMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 答题卡服务实现类
 *
 * @author EduMark
 */
@Service
public class AnswerSheetServiceImpl extends ServiceImpl<AnswerSheetMapper, AnswerSheet> implements AnswerSheetService {

    private static final int STATUS_RECOGNIZING = 0;
    private static final int STATUS_RECOGNIZED = 1;
    private static final int STATUS_READY_FOR_MARKING = 2;
    private static final int STATUS_RECOGNITION_EXCEPTION = 5;
    private static final Pattern DIGIT_STUDENT_NUMBER_PATTERN = Pattern.compile("(?<!\\d)(\\d{6,20})(?!\\d)");
    private static final Pattern ALNUM_STUDENT_NUMBER_PATTERN = Pattern.compile("(?<![A-Za-z0-9])([A-Za-z][A-Za-z0-9]{5,19})(?![A-Za-z0-9])");

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetImageMapper answerSheetImageMapper;

    @Resource
    private FileService fileService;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private ExamMapper examMapper;

    @Resource
    private BarcodeRecognitionService barcodeRecognitionService;

    @Resource
    private AnswerSheetDetailService answerSheetDetailService;

    @Resource
    private AnswerSheetRecognitionAsyncService recognitionAsyncService;

    @Override
    public PageResult<AnswerSheetVO> pageQuery(AnswerSheetQueryDTO query) {
        Page<AnswerSheetVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        answerSheetMapper.selectPageVO(page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public AnswerSheetVO getDetail(Long id) {
        AnswerSheetVO vo = answerSheetMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("答题卡不存在");
        }
        // 加载图片列表
        List<AnswerSheetImageVO> images = answerSheetImageMapper.selectListByAnswerSheetId(id);
        vo.setImages(images);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(AnswerSheetDTO dto) {
        AnswerSheet entity = new AnswerSheet();
        BeanUtils.copyProperties(dto, entity);
        entity.setImageCount(0);
        entity.setStatus(STATUS_RECOGNIZING);
        save(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AnswerSheetDTO dto) {
        AnswerSheet entity = getById(dto.getId());
        if (entity == null) {
            throw new BusinessException("答题卡不存在");
        }
        BeanUtils.copyProperties(dto, entity, getNullPropertyNames(dto));
        applyStudentResolution(entity, dto.getStudentId(), dto.getStudentNumber());
        updateById(entity);
        syncNullableRecognitionFields(entity);
        initializeQuestionDetailsIfReady(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AnswerSheet entity = getById(id);
        if (entity == null) {
            return;
        }
        // 删除图片文件
        List<AnswerSheetImageVO> images = answerSheetImageMapper.selectListByAnswerSheetId(id);
        for (AnswerSheetImageVO image : images) {
            if (image.getImagePath() != null) {
                fileService.delete(image.getImagePath());
            }
        }
        // 删除图片记录
        answerSheetImageMapper.deleteByAnswerSheetId(id);
        // 删除答题卡
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnswerSheetVO uploadImages(Long answerSheetId, List<MultipartFile> files) {
        AnswerSheet answerSheet = getById(answerSheetId);
        if (answerSheet == null) {
            throw new BusinessException("答题卡不存在");
        }

        int currentCount = answerSheet.getImageCount() != null ? answerSheet.getImageCount() : 0;
        int pageNum = currentCount + 1;

        for (MultipartFile file : files) {
            FileUploadResult result = fileService.upload(file, "answer-sheet");

            AnswerSheetImage image = new AnswerSheetImage();
            image.setAnswerSheetId(answerSheetId);
            image.setPageNum(pageNum);
            image.setImagePath(result.getObjectName());
            image.setImageUrl(result.getUrl());
            image.setOriginalName(result.getOriginalName());
            image.setFileSize(result.getFileSize());
            image.setSort(pageNum);
            answerSheetImageMapper.insert(image);

            pageNum++;
        }

        // 更新图片数量
        answerSheet.setImageCount(pageNum - 1);
        if (answerSheet.getStudentId() == null || STATUS_RECOGNITION_EXCEPTION == answerSheet.getStatus()) {
            // 设置为识别中状态
            answerSheet.setStatus(STATUS_RECOGNIZING);
            answerSheet.setRemark("正在识别...");
            updateById(answerSheet);

            // 事务提交后再异步执行识别，确保图片数据已持久化
            final Long sheetId = answerSheet.getId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    recognitionAsyncService.recognizeAsync(sheetId);
                }
            });
        } else {
            updateById(answerSheet);
        }

        return getDetail(answerSheetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadAnswerSheet(AnswerSheetUploadDTO dto) {
        Exam exam = examMapper.selectById(dto.getExamId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setExamId(dto.getExamId());
        answerSheet.setExamSubjectId(dto.getExamSubjectId());
        answerSheet.setSeatNumber(dto.getSeatNumber());
        answerSheet.setImageCount(dto.getImageObjectNames() != null ? dto.getImageObjectNames().size() : 0);
        answerSheet.setStatus(STATUS_RECOGNIZING);
        answerSheet.setRemark("正在识别...");

        // 如果指定了学生ID或学号，先尝试同步识别
        if (dto.getStudentId() != null || (dto.getStudentNumber() != null && !dto.getStudentNumber().isBlank())) {
            applyRecognitionResult(answerSheet, dto, exam);
        }
        save(answerSheet);

        if (dto.getImageObjectNames() != null && !dto.getImageObjectNames().isEmpty()) {
            int pageNum = 1;
            for (String objectName : dto.getImageObjectNames()) {
                AnswerSheetImage image = new AnswerSheetImage();
                image.setAnswerSheetId(answerSheet.getId());
                image.setPageNum(pageNum);
                image.setImagePath(objectName);
                image.setImageUrl(fileService.getUrl(objectName));
                if (dto.getImageOriginalNames() != null && dto.getImageOriginalNames().size() >= pageNum) {
                    image.setOriginalName(dto.getImageOriginalNames().get(pageNum - 1));
                }
                image.setSort(pageNum);
                answerSheetImageMapper.insert(image);
                pageNum++;
            }
        }

        // 如果还没有识别成功，事务提交后异步触发识别
        if (answerSheet.getStatus() == STATUS_RECOGNIZING) {
            final Long sheetId = answerSheet.getId();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    recognitionAsyncService.recognizeAsync(sheetId);
                }
            });
        } else {
            // 已经识别成功，初始化题目明细
            initializeQuestionDetailsIfReady(answerSheet);
        }

        return answerSheet.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long imageId) {
        AnswerSheetImage image = answerSheetImageMapper.selectById(imageId);
        if (image == null) {
            return;
        }
        // 删除文件
        if (image.getImagePath() != null) {
            fileService.delete(image.getImagePath());
        }
        // 删除记录
        answerSheetImageMapper.deleteById(imageId);

        // 更新图片数量
        AnswerSheet answerSheet = getById(image.getAnswerSheetId());
        if (answerSheet != null) {
            int count = answerSheetImageMapper.selectCount(
                    new LambdaQueryWrapper<AnswerSheetImage>()
                            .eq(AnswerSheetImage::getAnswerSheetId, image.getAnswerSheetId())
            ).intValue();
            answerSheet.setImageCount(count);
            updateById(answerSheet);
        }
    }

    @Override
    public List<AnswerSheetVO> listByExamSubjectId(Long examSubjectId) {
        return answerSheetMapper.selectListByExamSubjectId(examSubjectId);
    }

    @Override
    public int countByExamSubjectId(Long examSubjectId) {
        return Math.toIntExact(count(new LambdaQueryWrapper<AnswerSheet>()
                .eq(AnswerSheet::getExamSubjectId, examSubjectId)));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        AnswerSheet entity = getById(id);
        if (entity == null) {
            throw new BusinessException("答题卡不存在");
        }
        entity.setStatus(status);
        updateById(entity);
    }

    @Override
    public void reRecognize(Long id) {
        AnswerSheet entity = getById(id);
        if (entity == null) {
            throw new BusinessException("答题卡不存在");
        }
        refreshRecognition(entity, null);
        updateById(entity);
        syncNullableRecognitionFields(entity);
        initializeQuestionDetailsIfReady(entity);
    }

    private void applyRecognitionResult(AnswerSheet answerSheet, AnswerSheetUploadDTO dto, Exam exam) {
        RecognitionOutcome outcome = resolveRecognitionOutcome(
                exam.getSchoolId(),
                dto.getExamSubjectId(),
                dto.getStudentId(),
                dto.getStudentNumber(),
                buildRecognitionInputs(dto.getImageObjectNames(), dto.getImageOriginalNames())
        );
        if (outcome.matchedStudent() == null) {
            answerSheet.setStudentId(null);
            answerSheet.setStudentNumber(outcome.candidateStudentNumber());
            answerSheet.setStatus(STATUS_RECOGNITION_EXCEPTION);
            answerSheet.setRemark(outcome.remark());
            return;
        }

        ensureNoDuplicate(outcome.matchedStudent().getId(), dto.getExamSubjectId(), null);
        answerSheet.setStudentId(outcome.matchedStudent().getId());
        answerSheet.setStudentNumber(outcome.candidateStudentNumber());
        answerSheet.setStatus(STATUS_READY_FOR_MARKING);
        answerSheet.setRemark(outcome.remark());
    }

    private void applyStudentResolution(AnswerSheet entity, Long studentId, String studentNumber) {
        if (studentId == null && (studentNumber == null || studentNumber.isBlank())) {
            return;
        }
        Exam exam = examMapper.selectById(entity.getExamId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        Student matchedStudent = resolveStudent(exam.getSchoolId(), studentId, studentNumber, null);
        if (matchedStudent == null) {
            entity.setStudentId(null);
            entity.setStudentNumber(studentNumber);
            entity.setStatus(STATUS_RECOGNITION_EXCEPTION);
            entity.setRemark(studentNumber == null || studentNumber.isBlank()
                    ? "未识别到条码/学号，请在异常池处理中补录"
                    : "学号 " + studentNumber + " 未匹配到学生，请检查后重试");
            return;
        }

        ensureNoDuplicate(matchedStudent.getId(), entity.getExamSubjectId(), entity.getId());
        entity.setStudentId(matchedStudent.getId());
        entity.setStudentNumber(studentNumber == null || studentNumber.isBlank()
                ? matchedStudent.getStudentNumber()
                : studentNumber.trim());
        entity.setStatus(STATUS_READY_FOR_MARKING);
        entity.setRemark("人工确认完成，已进入待阅卷");
    }

    private void refreshRecognition(AnswerSheet answerSheet, String preferredStudentNumber) {
        Exam exam = examMapper.selectById(answerSheet.getExamId());
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        List<AnswerSheetImageVO> images = answerSheetImageMapper.selectListByAnswerSheetId(answerSheet.getId());
        RecognitionOutcome outcome = resolveRecognitionOutcome(
                exam.getSchoolId(),
                answerSheet.getExamSubjectId(),
                null,
                preferredStudentNumber,
                buildRecognitionInputs(images)
        );
        if (outcome.matchedStudent() == null) {
            answerSheet.setStudentId(null);
            answerSheet.setStudentNumber(outcome.candidateStudentNumber());
            answerSheet.setStatus(STATUS_RECOGNITION_EXCEPTION);
            answerSheet.setRemark(outcome.remark());
            return;
        }

        ensureNoDuplicate(outcome.matchedStudent().getId(), answerSheet.getExamSubjectId(), answerSheet.getId());
        answerSheet.setStudentId(outcome.matchedStudent().getId());
        answerSheet.setStudentNumber(outcome.candidateStudentNumber());
        answerSheet.setStatus(STATUS_READY_FOR_MARKING);
        answerSheet.setRemark(outcome.remark());
    }

    private Student resolveStudent(Long schoolId, Long studentId, String studentNumber, List<String> imageOriginalNames) {
        if (studentId != null) {
            return studentMapper.selectById(studentId);
        }

        String candidate = extractStudentNumber(studentNumber, imageOriginalNames);
        if (candidate == null || candidate.isBlank()) {
            return null;
        }
        return studentMapper.selectBySchoolAndStudentNumber(schoolId, candidate);
    }

    private RecognitionOutcome resolveRecognitionOutcome(Long schoolId,
                                                         Long examSubjectId,
                                                         Long studentId,
                                                         String studentNumber,
                                                         List<RecognitionImageInput> images) {
        if (studentId != null) {
            Student matchedStudent = studentMapper.selectById(studentId);
            if (matchedStudent == null) {
                return new RecognitionOutcome(null, null, "指定学生不存在，请重新选择");
            }
            return new RecognitionOutcome(matchedStudent, matchedStudent.getStudentNumber(), "人工指定学生成功，已进入待阅卷");
        }

        if (studentNumber != null && !studentNumber.isBlank()) {
            String trimmedStudentNumber = studentNumber.trim();
            Student matchedStudent = studentMapper.selectBySchoolAndStudentNumber(schoolId, trimmedStudentNumber);
            if (matchedStudent == null) {
                return new RecognitionOutcome(null, trimmedStudentNumber, "学号 " + trimmedStudentNumber + " 未匹配到学生，请检查后重试");
            }
            return new RecognitionOutcome(matchedStudent, trimmedStudentNumber, "人工录入学号成功，已进入待阅卷");
        }

        BarcodeRecognitionResult barcodeResult = barcodeRecognitionService.recognize(examSubjectId, images);
        if (barcodeResult.isSuccess() && barcodeResult.getText() != null && !barcodeResult.getText().isBlank()) {
            String candidate = barcodeResult.getText().trim();
            Student matchedStudent = studentMapper.selectBySchoolAndStudentNumber(schoolId, candidate);
            if (matchedStudent != null) {
                return new RecognitionOutcome(matchedStudent, candidate, barcodeResult.getMessage() + "，已自动匹配学生");
            }
            return new RecognitionOutcome(null, candidate, barcodeResult.getMessage() + "，但未匹配到学生，请人工确认");
        }

        List<String> imageOriginalNames = images.stream()
                .map(RecognitionImageInput::getOriginalName)
                .filter(name -> name != null && !name.isBlank())
                .toList();
        String candidate = extractStudentNumber(null, imageOriginalNames);
        if (candidate == null) {
            String message = barcodeResult.getMessage();
            if (message == null || message.isBlank()) {
                message = "未识别到条码/学号，请在异常池处理中补录";
            } else {
                message = message + "，且未从文件名提取到候选学号，请在异常池处理中补录";
            }
            return new RecognitionOutcome(null, null, message);
        }

        Student matchedStudent = studentMapper.selectBySchoolAndStudentNumber(schoolId, candidate);
        if (matchedStudent != null) {
            String prefix = barcodeResult.getMessage();
            String message = (prefix == null || prefix.isBlank() ? "" : prefix + "，")
                    + "已回退文件名候选学号 " + candidate + " 并自动匹配学生";
            return new RecognitionOutcome(matchedStudent, candidate, message);
        }

        String prefix = barcodeResult.getMessage();
        String message = (prefix == null || prefix.isBlank() ? "" : prefix + "，")
                + "已从文件名提取候选学号 " + candidate + "，但未匹配到学生，请人工确认";
        return new RecognitionOutcome(null, candidate, message);
    }

    private List<RecognitionImageInput> buildRecognitionInputs(List<String> imageObjectNames, List<String> imageOriginalNames) {
        List<RecognitionImageInput> inputs = new ArrayList<>();
        if (imageObjectNames == null || imageObjectNames.isEmpty()) {
            return inputs;
        }

        for (int i = 0; i < imageObjectNames.size(); i++) {
            RecognitionImageInput input = new RecognitionImageInput();
            input.setPageNum(i + 1);
            input.setObjectName(imageObjectNames.get(i));
            if (imageOriginalNames != null && imageOriginalNames.size() > i) {
                input.setOriginalName(imageOriginalNames.get(i));
            }
            inputs.add(input);
        }
        return inputs;
    }

    private List<RecognitionImageInput> buildRecognitionInputs(List<AnswerSheetImageVO> images) {
        List<RecognitionImageInput> inputs = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            return inputs;
        }

        for (AnswerSheetImageVO image : images) {
            RecognitionImageInput input = new RecognitionImageInput();
            input.setPageNum(image.getPageNum());
            input.setObjectName(image.getImagePath());
            input.setOriginalName(image.getOriginalName());
            inputs.add(input);
        }
        return inputs;
    }

    private String extractStudentNumber(String studentNumber, List<String> imageOriginalNames) {
        if (studentNumber != null && !studentNumber.isBlank()) {
            return studentNumber.trim();
        }

        if (imageOriginalNames == null) {
            return null;
        }
        for (String value : imageOriginalNames) {
            String extracted = parseStudentNumber(value);
            if (extracted != null) {
                return extracted;
            }
        }
        return null;
    }

    private String parseStudentNumber(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        Matcher matcher = DIGIT_STUDENT_NUMBER_PATTERN.matcher(source);
        while (matcher.find()) {
            return matcher.group(1);
        }

        matcher = ALNUM_STUDENT_NUMBER_PATTERN.matcher(source);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void ensureNoDuplicate(Long studentId, Long examSubjectId, Long currentId) {
        if (studentId == null) {
            return;
        }
        AnswerSheet existing = answerSheetMapper.selectByStudentAndSubject(studentId, examSubjectId);
        if (existing != null && (currentId == null || !existing.getId().equals(currentId))) {
            throw new BusinessException("该学生的答题卡已存在");
        }
    }

    private String[] getNullPropertyNames(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        return java.util.Arrays.stream(beanWrapper.getPropertyDescriptors())
                .map(pd -> pd.getName())
                .filter(name -> beanWrapper.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    private void initializeQuestionDetailsIfReady(AnswerSheet answerSheet) {
        if (answerSheet == null || answerSheet.getId() == null) {
            return;
        }
        if (answerSheet.getStatus() == null || answerSheet.getStatus() != STATUS_READY_FOR_MARKING) {
            return;
        }
        if (answerSheet.getStudentId() == null || answerSheet.getExamSubjectId() == null) {
            return;
        }
        answerSheetDetailService.initializeQuestionDetails(answerSheet.getId());
        answerSheetDetailService.recognizeObjectiveAnswers(answerSheet.getId());
    }

    private void syncNullableRecognitionFields(AnswerSheet answerSheet) {
        if (answerSheet == null || answerSheet.getId() == null) {
            return;
        }
        lambdaUpdate()
                .eq(AnswerSheet::getId, answerSheet.getId())
                .set(AnswerSheet::getStudentId, answerSheet.getStudentId())
                .set(AnswerSheet::getStudentNumber, answerSheet.getStudentNumber())
                .set(AnswerSheet::getStatus, answerSheet.getStatus())
                .set(AnswerSheet::getRemark, answerSheet.getRemark())
                .update();
    }

    private record RecognitionOutcome(Student matchedStudent, String candidateStudentNumber, String remark) {
    }
}
