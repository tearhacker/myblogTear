package com.star.dao;

import com.star.entity.FileUpload;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文件上传数据访问接口
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Mapper
public interface FileUploadDao {

    /**
     * 保存文件上传信息
     * @param fileUpload 文件上传实体
     * @return 影响行数
     */
    int saveFileUpload(FileUpload fileUpload);

    /**
     * 物理删除文件记录
     * @param id 文件ID
     * @return 影响行数
     */
    int deleteFileUpload(Long id);

    /**
     * 软删除文件记录
     * @param id 文件ID
     * @return 影响行数
     */
    int softDeleteFileUpload(Long id);

    /**
     * 更新文件信息
     * @param fileUpload 文件上传实体
     * @return 影响行数
     */
    int updateFileUpload(FileUpload fileUpload);

    /**
     * 根据ID获取文件信息
     * @param id 文件ID
     * @return 文件上传实体
     */
    FileUpload getFileUploadById(Long id);

    /**
     * 根据存储文件名获取文件信息
     * @param storedFilename 存储文件名
     * @return 文件上传实体
     */
    FileUpload getFileUploadByStoredFilename(String storedFilename);

    /**
     * 获取所有文件列表（不包括已删除的）
     * @return 文件列表
     */
    List<FileUpload> listFileUploads();

    /**
     * 根据文件类型获取文件列表
     * @param fileType 文件类型
     * @return 文件列表
     */
    List<FileUpload> listFileUploadsByType(String fileType);

    /**
     * 分页获取文件列表
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文件列表
     */
    List<FileUpload> listFileUploadsWithPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 增加文件下载次数
     * @param id 文件ID
     * @return 影响行数
     */
    int incrementDownloadCount(Long id);

    /**
     * 获取文件总数（不包括已删除的）
     * @return 文件总数
     */
    int getFileCount();

    /**
     * 获取文件总大小（不包括已删除的）
     * @return 文件总大小（字节）
     */
    Long getTotalFileSize();

    /**
     * 根据上传时间范围获取文件列表
     * @param startTime 开始时间（格式：yyyy-MM-dd）
     * @param endTime 结束时间（格式：yyyy-MM-dd）
     * @return 文件列表
     */
    List<FileUpload> listFileUploadsByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 搜索文件（根据原始文件名或描述）
     * @param keyword 搜索关键词
     * @return 文件列表
     */
    List<FileUpload> searchFileUploads(String keyword);

    /**
     * 获取最近上传的文件列表
     * @param limit 限制数量
     * @return 文件列表
     */
    List<FileUpload> getRecentFileUploads(int limit);
}