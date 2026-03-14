package com.edumark.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edumark.common.exception.BusinessException;
import com.edumark.common.result.PageResult;
import com.edumark.file.dto.AnswerSheetDTO;
import com.edumark.file.dto.AnswerSheetQueryDTO;
import com.edumark.file.dto.AnswerSheetUploadDTO;
import com.edumark.file.dto.FileUploadResult;
import com.edumark.file.entity.AnswerSheet;
import com.edumark.file.entity.AnswerSheetImage;
import com.edumark.file.mapper.AnswerSheetImageMapper;
import com.edumark.file.mapper.AnswerSheetMapper;
import com.edumark.file.service.AnswerSheetService;
import com.edumark.file.service.FileService;
import com.edumark.file.vo.AnswerSheetImageVO;
import com.edumark.file.vo.AnswerSheetVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 答题卡服务实现类
 *
 * @author EduMark
 */
@Service
public class AnswerSheetServiceImpl extends ServiceImpl<AnswerSheetMapper, AnswerSheet> implements AnswerSheetService {

    @Resource
    private AnswerSheetMapper answerSheetMapper;

    @Resource
    private AnswerSheetImageMapper answerSheetImageMapper;

    @Resource
    private FileService fileService;

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
        entity.setStatus(0); // 待识别
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
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
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
        updateById(answerSheet);

        return getDetail(answerSheetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadAnswerSheet(AnswerSheetUploadDTO dto) {
        // 检查是否已存在
        AnswerSheet existing = answerSheetMapper.selectByStudentAndSubject(dto.getStudentId(), dto.getExamSubjectId());
        if (existing != null) {
            throw new BusinessException("该学生的答题卡已存在");
        }

        // 创建答题卡
        AnswerSheet answerSheet = new AnswerSheet();
        answerSheet.setExamId(dto.getExamId());
        answerSheet.setExamSubjectId(dto.getExamSubjectId());
        answerSheet.setStudentId(dto.getStudentId());
        answerSheet.setStudentNumber(dto.getStudentNumber());
        answerSheet.setSeatNumber(dto.getSeatNumber());
        answerSheet.setImageCount(dto.getImageObjectNames() != null ? dto.getImageObjectNames().size() : 0);
        answerSheet.setStatus(0);
        save(answerSheet);

        // 保存图片记录
        if (dto.getImageObjectNames() != null && !dto.getImageObjectNames().isEmpty()) {
            int pageNum = 1;
            for (String objectName : dto.getImageObjectNames()) {
                AnswerSheetImage image = new AnswerSheetImage();
                image.setAnswerSheetId(answerSheet.getId());
                image.setPageNum(pageNum);
                image.setImagePath(objectName);
                image.setImageUrl(fileService.getUrl(objectName));
                image.setSort(pageNum);
                answerSheetImageMapper.insert(image);
                pageNum++;
            }
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
}
