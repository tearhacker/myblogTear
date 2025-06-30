package com.star.service.Impl;

import com.star.config.FileUploadConfig;
import com.star.dao.FileUploadDao;
import com.star.entity.FileUpload;
import com.star.service.FileUploadService;
import com.star.util.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文件上传服务实现类
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUploadDao fileUploadDao;
    
    @Autowired
    private FileUploadConfig fileUploadConfig;

    // 最大文件大小限制（800MB）
    private static final long MAX_FILE_SIZE_MB = 800;

    @Override
    @Transactional
    public FileUpload uploadFile(MultipartFile file, String description, HttpServletRequest request) {
        try {
            logger.info("开始上传文件: {}, 大小: {} 字节", file.getOriginalFilename(), file.getSize());
            
            // 1. 验证文件基本信息
            if (!FileUploadUtil.isValidFile(file)) {
                throw new RuntimeException("文件无效或为空");
            }
            logger.debug("文件基本验证通过");

            // 2. 验证文件类型
            if (!FileUploadUtil.isAllowedFileType(file)) {
                throw new RuntimeException("不支持的文件类型：" + file.getContentType());
            }
            logger.debug("文件类型验证通过: {}", file.getContentType());

            // 3. 验证文件大小
            if (!FileUploadUtil.isValidFileSize(file, MAX_FILE_SIZE_MB)) {
                throw new RuntimeException("文件大小超过限制（最大" + MAX_FILE_SIZE_MB + "MB）");
            }
            logger.debug("文件大小验证通过");

            // 4. 验证文件名安全性
            String originalFilename = file.getOriginalFilename();
            if (!FileUploadUtil.isSafeFilename(originalFilename)) {
                throw new RuntimeException("文件名包含非法字符");
            }
            logger.debug("文件名安全性验证通过");

            // 5. 生成存储文件名和路径
            String storedFilename = FileUploadUtil.generateStoredFilename(originalFilename);
            String filePath = FileUploadUtil.getFullStoragePath(storedFilename);
            logger.debug("生成存储路径: {}", filePath);

            // 6. 保存文件到磁盘
            FileUploadUtil.saveFileToDisk(file, filePath);
            logger.debug("文件保存到磁盘成功");

            // 7. 获取上传者IP
            String uploaderIp = FileUploadUtil.getClientIpAddress(request);
            logger.debug("上传者IP: {}", uploaderIp);

            // 8. 创建文件上传实体（暂时不设置URL）
            FileUpload fileUpload = new FileUpload(
                originalFilename,
                storedFilename,
                filePath,
                "", // 暂时为空，保存后生成
                file.getSize(),
                file.getContentType(),
                FileUploadUtil.getFileExtension(originalFilename),
                uploaderIp,
                description
            );
            logger.debug("创建文件实体成功");

            // 9. 保存到数据库
            int result = fileUploadDao.saveFileUpload(fileUpload);
            if (result <= 0) {
                // 如果数据库保存失败，删除已上传的文件
                FileUploadUtil.deleteFileFromDisk(filePath);
                throw new RuntimeException("文件信息保存失败");
            }
            logger.debug("数据库保存成功，ID: {}", fileUpload.getId());

            // 10. 生成文件访问URL（使用数据库生成的ID）
            String fileUrl = FileUploadUtil.generateFileUrl(request, fileUpload.getId());
            fileUpload.setFileUrl(fileUrl);
            
            // 11. 更新URL到数据库
            fileUploadDao.updateFileUpload(fileUpload);
            logger.debug("文件URL更新成功: {}", fileUrl);

            logger.info("文件上传成功: {} -> {}", originalFilename, storedFilename);
            return fileUpload;

        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<FileUpload> uploadFiles(MultipartFile[] files, String[] descriptions, HttpServletRequest request) {
        List<FileUpload> uploadedFiles = new ArrayList<>();
        StringBuilder errorMessages = new StringBuilder();
        
        if (files == null || files.length == 0) {
            throw new RuntimeException("没有选择要上传的文件");
        }

        for (int i = 0; i < files.length; i++) {
            try {
                String description = (descriptions != null && i < descriptions.length) ? descriptions[i] : null;
                FileUpload uploadedFile = uploadFile(files[i], description, request);
                uploadedFiles.add(uploadedFile);
                logger.info("文件上传成功: {}", files[i].getOriginalFilename());
            } catch (Exception e) {
                String fileName = files[i].getOriginalFilename();
                String errorMsg = String.format("文件 %s 上传失败: %s", fileName, e.getMessage());
                logger.error(errorMsg, e);
                errorMessages.append(errorMsg).append("; ");
            }
        }

        if (uploadedFiles.isEmpty()) {
            String finalError = "所有文件上传失败";
            if (errorMessages.length() > 0) {
                finalError += ": " + errorMessages.toString();
            }
            throw new RuntimeException(finalError);
        }

        // 如果部分成功，返回成功的文件列表
        if (errorMessages.length() > 0) {
            logger.warn("部分文件上传失败: {}", errorMessages.toString());
        }

        return uploadedFiles;
    }

    @Override
    public boolean downloadFile(Long id, HttpServletResponse response) {
        try {
            FileUpload fileUpload = fileUploadDao.getFileUploadById(id);
            return downloadFileInternal(fileUpload, response, true);
        } catch (Exception e) {
            logger.error("文件下载失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean downloadFileByStoredFilename(String storedFilename, HttpServletResponse response) {
        try {
            FileUpload fileUpload = fileUploadDao.getFileUploadByStoredFilename(storedFilename);
            return downloadFileInternal(fileUpload, response, true);
        } catch (Exception e) {
            logger.error("文件下载失败，文件名: {}, 错误: {}", storedFilename, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean previewFile(Long id, HttpServletResponse response) {
        try {
            FileUpload fileUpload = fileUploadDao.getFileUploadById(id);
            if (fileUpload != null && !isPreviewableType(fileUpload.getFileType())) {
                return false; // 不支持预览的文件类型
            }
            return downloadFileInternal(fileUpload, response, false);
        } catch (Exception e) {
            logger.error("文件预览失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 内部文件下载处理方法（优化移动端兼容性）
     * @param fileUpload 文件实体
     * @param response HTTP响应
     * @param isDownload 是否为下载模式（false为预览模式）
     * @return 是否成功
     */
    private boolean downloadFileInternal(FileUpload fileUpload, HttpServletResponse response, boolean isDownload) {
        if (fileUpload == null) {
            logger.warn("文件不存在或已被删除");
            return false;
        }

        File file = new File(fileUpload.getFilePath());
        if (!file.exists() || !file.isFile()) {
            logger.warn("物理文件不存在: {}", fileUpload.getFilePath());
            return false;
        }

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            // 清除所有响应头，避免缓存问题
            response.reset();
            
            // 设置基础响应头
            response.setContentType(fileUpload.getFileType());
            response.setContentLengthLong(fileUpload.getFileSize());
            
            // 设置移动端兼容的响应头
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Accept-Ranges", "bytes");
            
            // 设置文件名（移动端兼容）
            String originalFilename = fileUpload.getOriginalFilename();
            String encodedFilename;
            
            try {
                // 尝试UTF-8编码
                encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                // 如果UTF-8编码失败，使用ISO-8859-1
                encodedFilename = URLEncoder.encode(originalFilename, "ISO-8859-1");
            }
            
            if (isDownload) {
                // 下载模式 - 强制下载
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
                
                // 移动端特殊处理
                response.setHeader("Content-Transfer-Encoding", "binary");
                
                // 只有下载时才增加下载次数
                fileUploadDao.incrementDownloadCount(fileUpload.getId());
            } else {
                // 预览模式 - 在线查看
                response.setHeader("Content-Disposition", 
                    "inline; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
            }
            
            // 根据文件类型设置额外的响应头
            setFileTypeSpecificHeaders(response, fileUpload.getFileType(), originalFilename);

            // 输出文件流
            try (OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            logger.info("文件{}成功: {} ({} bytes)", isDownload ? "下载" : "预览", originalFilename, fileUpload.getFileSize());
            return true;

        } catch (Exception e) {
            logger.error("文件输出流处理失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 根据文件类型设置特定的响应头
     */
    private void setFileTypeSpecificHeaders(HttpServletResponse response, String fileType, String filename) {
        if (fileType == null) return;
        
        if (fileType.startsWith("image/")) {
            // 图片文件
            response.setHeader("Content-Type", fileType);
        } else if (fileType.equals("application/pdf")) {
            // PDF文件
            response.setHeader("Content-Type", "application/pdf");
        } else if (fileType.startsWith("text/")) {
            // 文本文件
            response.setHeader("Content-Type", fileType + "; charset=UTF-8");
        } else if (fileType.startsWith("video/")) {
            // 视频文件
            response.setHeader("Content-Type", fileType);
            response.setHeader("Accept-Ranges", "bytes");
        } else if (fileType.startsWith("audio/")) {
            // 音频文件
            response.setHeader("Content-Type", fileType);
        } else if (fileType.contains("zip") || fileType.contains("rar") || fileType.contains("7z")) {
            // 压缩文件
            response.setHeader("Content-Type", "application/octet-stream");
        } else {
            // 其他文件类型
            response.setHeader("Content-Type", "application/octet-stream");
        }
    }

    /**
     * 检查文件类型是否支持预览
     */
    private boolean isPreviewableType(String fileType) {
        if (fileType == null) return false;
        return fileType.startsWith("image/") || 
               fileType.equals("application/pdf") ||
               fileType.startsWith("text/");
    }

    @Override
    public FileUpload getFileById(Long id) {
        return fileUploadDao.getFileUploadById(id);
    }

    @Override
    public List<FileUpload> getAllFiles() {
        return fileUploadDao.listFileUploads();
    }

    @Override
    public List<FileUpload> getFilesByType(String fileType) {
        return fileUploadDao.listFileUploadsByType(fileType);
    }

    @Override
    public List<FileUpload> getFilesWithPage(int page, int size) {
        int offset = (page - 1) * size;
        return fileUploadDao.listFileUploadsWithPage(offset, size);
    }

    @Override
    public List<FileUpload> searchFiles(String keyword) {
        return fileUploadDao.searchFileUploads(keyword);
    }

    @Override
    @Transactional
    public boolean deleteFile(Long id) {
        try {
            int result = fileUploadDao.softDeleteFileUpload(id);
            if (result > 0) {
                logger.info("文件软删除成功，ID: {}", id);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("文件软删除失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean permanentDeleteFile(Long id) {
        try {
            // 先获取文件信息
            FileUpload fileUpload = fileUploadDao.getFileUploadById(id);
            if (fileUpload == null) {
                return false;
            }

            // 删除物理文件
            boolean fileDeleted = FileUploadUtil.deleteFileFromDisk(fileUpload.getFilePath());
            
            // 删除数据库记录
            int result = fileUploadDao.deleteFileUpload(id);
            
            if (result > 0) {
                logger.info("文件永久删除成功，ID: {}, 物理文件删除: {}", id, fileDeleted);
                return true;
            }
            return false;
            
        } catch (Exception e) {
            logger.error("文件永久删除失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateFile(FileUpload fileUpload) {
        try {
            int result = fileUploadDao.updateFileUpload(fileUpload);
            return result > 0;
        } catch (Exception e) {
            logger.error("文件信息更新失败，ID: {}, 错误: {}", fileUpload.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getFileStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        try {
            int totalFiles = fileUploadDao.getFileCount();
            Long totalSize = fileUploadDao.getTotalFileSize();
            
            statistics.put("totalFiles", totalFiles);
            statistics.put("totalSize", totalSize);
            statistics.put("totalSizeFormatted", formatFileSize(totalSize));
            
            // 可以添加更多统计信息，如按类型分组的文件数量等
            
        } catch (Exception e) {
            logger.error("获取文件统计信息失败: {}", e.getMessage(), e);
            statistics.put("error", "获取统计信息失败");
        }
        return statistics;
    }

    @Override
    public List<FileUpload> getRecentFiles(int limit) {
        return fileUploadDao.getRecentFileUploads(limit);
    }

    @Override
    public boolean isFileExists(Long id) {
        FileUpload fileUpload = fileUploadDao.getFileUploadById(id);
        if (fileUpload == null) {
            return false;
        }
        return FileUploadUtil.isFileExistsOnDisk(fileUpload.getFilePath());
    }

    @Override
    public List<FileUpload> getFilesByTimeRange(String startTime, String endTime) {
        return fileUploadDao.listFileUploadsByTimeRange(startTime, endTime);
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null || size == 0) {
            return "0 B";
        }
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        double fileSize = size.doubleValue();
        int unitIndex = 0;
        
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
}
