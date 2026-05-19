package com.edumark.file.service.impl;

import com.edumark.common.config.MinioConfig;
import com.edumark.common.exception.BusinessException;
import com.edumark.file.dto.FileUploadResult;
import com.edumark.file.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务实现类
 *
 * @author EduMark
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioConfig minioConfig;

    /**
     * 初始化时检查并创建存储桶
     */
    @PostConstruct
    public void init() {
        try {
            String bucketName = minioConfig.getBucketName();
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("创建存储桶: {}", bucketName);
            }
        } catch (Exception e) {
            log.warn("MinIO初始化检查失败: {}", e.getMessage());
        }
    }

    @Override
    public FileUploadResult upload(MultipartFile file, String directory) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String objectName = directory + "/" + datePath + "/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            String url = getUrl(objectName);

            return new FileUploadResult(
                    fileName,
                    originalName,
                    objectName,
                    file.getSize(),
                    file.getContentType(),
                    url
            );
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<FileUploadResult> uploadBatch(List<MultipartFile> files, String directory) {
        List<FileUploadResult> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(upload(file, directory));
        }
        return results;
    }

    @Override
    public void delete(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteBatch(List<String> objectNames) {
        for (String objectName : objectNames) {
            delete(objectName);
        }
    }

    @Override
    public String getUrl(String objectName) {
        return minioConfig.getPublicEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;
    }

    @Override
    public String getPresignedUrl(String objectName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .expiry(expiry, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            log.error("获取预签名URL失败", e);
            throw new BusinessException("获取预签名URL失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream getFileStream(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("获取文件流失败", e);
            throw new BusinessException("获取文件流失败: " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void uploadBytes(byte[] bytes, String objectName, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(new java.io.ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
