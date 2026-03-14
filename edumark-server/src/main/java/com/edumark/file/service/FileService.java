package com.edumark.file.service;

import com.edumark.file.dto.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件服务接口
 *
 * @author EduMark
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param directory 目录
     * @return 上传结果
     */
    FileUploadResult upload(MultipartFile file, String directory);

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @param directory 目录
     * @return 上传结果列表
     */
    List<FileUploadResult> uploadBatch(List<MultipartFile> files, String directory);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 批量删除文件
     *
     * @param objectNames 对象名称列表
     */
    void deleteBatch(List<String> objectNames);

    /**
     * 获取文件访问URL
     *
     * @param objectName 对象名称
     * @return 访问URL
     */
    String getUrl(String objectName);

    /**
     * 获取文件预签名URL（有效期）
     *
     * @param objectName 对象名称
     * @param expiry 有效期（秒）
     * @return 预签名URL
     */
    String getPresignedUrl(String objectName, int expiry);

    /**
     * 获取文件流
     *
     * @param objectName 对象名称
     * @return 输入流
     */
    InputStream getFileStream(String objectName);

    /**
     * 检查文件是否存在
     *
     * @param objectName 对象名称
     * @return 是否存在
     */
    boolean exists(String objectName);

    /**
     * 上传字节数组
     *
     * @param bytes 字节数组
     * @param objectName 对象名称
     * @param contentType 内容类型
     */
    void uploadBytes(byte[] bytes, String objectName, String contentType);
}
