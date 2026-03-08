package com.star.service;

import com.star.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文件上传服务接口
 * @Date: Created in 2025/09/07
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface FileUploadService {

    /**
     * 上传单个文件
     * @param file 上传的文件
     * @param description 文件描述
     * @param request HTTP请求对象（用于获取IP等信息）
     * @return 文件上传实体
     */
    FileUpload uploadFile(MultipartFile file, String description, HttpServletRequest request);

    /**
     * 批量上传文件
     * @param files 上传的文件数组
     * @param descriptions 文件描述数组（可选）
     * @param request HTTP请求对象
     * @return 上传成功的文件列表
     */
    List<FileUpload> uploadFiles(MultipartFile[] files, String[] descriptions, HttpServletRequest request);

    /**
     * 下载文件
     * @param id 文件ID
     * @param response HTTP响应对象
     * @return 是否下载成功
     */
    boolean downloadFile(Long id, HttpServletResponse response);

    /**
     * 根据存储文件名下载文件
     * @param storedFilename 存储文件名
     * @param response HTTP响应对象
     * @return 是否下载成功
     */
    boolean downloadFileByStoredFilename(String storedFilename, HttpServletResponse response);

    /**
     * 预览文件（在线查看）
     * @param id 文件ID
     * @param response HTTP响应对象
     * @return 是否预览成功
     */
    boolean previewFile(Long id, HttpServletResponse response);

    /**
     * 根据ID获取文件信息
     * @param id 文件ID
     * @return 文件上传实体
     */
    FileUpload getFileById(Long id);

    /**
     * 获取所有文件列表
     * @return 文件列表
     */
    List<FileUpload> getAllFiles();

    /**
     * 根据文件类型获取文件列表
     * @param fileType 文件类型（如：image、video、document等）
     * @return 文件列表
     */
    List<FileUpload> getFilesByType(String fileType);

    /**
     * 分页获取文件列表
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 文件列表
     */
    List<FileUpload> getFilesWithPage(int page, int size);

    /**
     * 搜索文件
     * @param keyword 搜索关键词
     * @return 文件列表
     */
    List<FileUpload> searchFiles(String keyword);

    /**
     * 软删除文件
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long id);

    /**
     * 物理删除文件（同时删除磁盘文件和数据库记录）
     * @param id 文件ID
     * @return 是否删除成功
     */
    boolean permanentDeleteFile(Long id);

    /**
     * 更新文件信息
     * @param fileUpload 文件实体
     * @return 是否更新成功
     */
    boolean updateFile(FileUpload fileUpload);

    /**
     * 获取文件统计信息
     * @return 统计信息Map（包含文件总数、总大小等）
     */
    Map<String, Object> getFileStatistics();

    /**
     * 获取最近上传的文件
     * @param limit 限制数量
     * @return 文件列表
     */
    List<FileUpload> getRecentFiles(int limit);

    /**
     * 检查文件是否存在
     * @param id 文件ID
     * @return 是否存在
     */
    boolean isFileExists(Long id);

    /**
     * 根据时间范围获取文件列表
     * @param startTime 开始时间（yyyy-MM-dd）
     * @param endTime 结束时间（yyyy-MM-dd）
     * @return 文件列表
     */
    List<FileUpload> getFilesByTimeRange(String startTime, String endTime);
}