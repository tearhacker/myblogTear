package com.star.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 文件上传实体类
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
public class FileUpload implements Serializable {

    private Long id;
    private String originalFilename;    // 原始文件名
    private String storedFilename;      // 存储文件名
    private String filePath;            // 文件存储路径
    private String fileUrl;             // 文件访问URL
    private Long fileSize;              // 文件大小（字节）
    private String fileType;            // 文件类型/MIME类型
    private String fileExtension;       // 文件扩展名
    private Date uploadTime;            // 上传时间
    private String uploaderIp;          // 上传者IP
    private String description;         // 文件描述
    private Integer downloadCount;      // 下载次数
    private Boolean isDeleted;          // 是否删除（0-未删除，1-已删除）

    public FileUpload() {
        this.downloadCount = 0;
        this.isDeleted = false;
        this.uploadTime = new Date();
    }

    public FileUpload(String originalFilename, String storedFilename, String filePath, String fileUrl, 
                     Long fileSize, String fileType, String fileExtension, String uploaderIp, String description) {
        this();
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileExtension = fileExtension;
        this.uploaderIp = uploaderIp;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploaderIp() {
        return uploaderIp;
    }

    public void setUploaderIp(String uploaderIp) {
        this.uploaderIp = uploaderIp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 获取格式化的文件大小
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double size = fileSize.doubleValue();
        int unitIndex = 0;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 检查是否为图片文件
     */
    public boolean isImageFile() {
        if (fileType == null) {
            return false;
        }
        return fileType.startsWith("image/");
    }

    /**
     * 检查是否为可预览的文件类型
     */
    public boolean isPreviewable() {
        if (fileType == null) {
            return false;
        }
        return fileType.startsWith("image/") || 
               fileType.equals("application/pdf") ||
               fileType.startsWith("text/");
    }

    @Override
    public String toString() {
        return "FileUpload{" +
                "id=" + id +
                ", originalFilename='" + originalFilename + '\'' +
                ", storedFilename='" + storedFilename + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", fileExtension='" + fileExtension + '\'' +
                ", uploadTime=" + uploadTime +
                ", uploaderIp='" + uploaderIp + '\'' +
                ", description='" + description + '\'' +
                ", downloadCount=" + downloadCount +
                ", isDeleted=" + isDeleted +
                '}';
    }
}