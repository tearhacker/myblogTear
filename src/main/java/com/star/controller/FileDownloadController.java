package com.star.controller;

import com.star.annotation.AccessLimit;
import com.star.entity.FileUpload;
import com.star.response.Response;
import com.star.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 文件下载控制器
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/api/files")
public class FileDownloadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 根据文件ID下载文件
     */
    @AccessLimit(seconds = 10, maxCount = 5)
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            boolean success = fileUploadService.downloadFile(id, response);
            if (!success) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":404,\"message\":\"文件不存在或已被删除\"}");
            }
        } catch (Exception e) {
            logger.error("文件下载失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"message\":\"文件下载失败\"}");
            } catch (Exception ex) {
                logger.error("响应写入失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 根据存储文件名下载文件
     */
    @AccessLimit(seconds = 10, maxCount = 5)
    @GetMapping("/download/file/{storedFilename}")
    public void downloadFileByName(@PathVariable String storedFilename, HttpServletResponse response) {
        try {
            boolean success = fileUploadService.downloadFileByStoredFilename(storedFilename, response);
            if (!success) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":404,\"message\":\"文件不存在或已被删除\"}");
            }
        } catch (Exception e) {
            logger.error("文件下载失败，文件名: {}, 错误: {}", storedFilename, e.getMessage(), e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"message\":\"文件下载失败\"}");
            } catch (Exception ex) {
                logger.error("响应写入失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 预览文件（在线查看）
     */
    @AccessLimit(seconds = 5, maxCount = 10)
    @GetMapping("/preview/{id}")
    public void previewFile(@PathVariable Long id, HttpServletResponse response) {
        try {
            boolean success = fileUploadService.previewFile(id, response);
            if (!success) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":404,\"message\":\"文件不存在、已被删除或不支持预览\"}");
            }
        } catch (Exception e) {
            logger.error("文件预览失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"message\":\"文件预览失败\"}");
            } catch (Exception ex) {
                logger.error("响应写入失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 获取公开文件列表（前台展示用）
     */
    @GetMapping("/public/list")
    @ResponseBody
    public Response<List<FileUpload>> getPublicFileList(@RequestParam(value = "type", required = false) String type,
                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            List<FileUpload> files;
            
            if (type != null && !type.trim().isEmpty()) {
                files = fileUploadService.getFilesByType(type);
            } else {
                files = fileUploadService.getFilesWithPage(page, size);
            }
            
            return Response.success("获取文件列表成功", files);
        } catch (Exception e) {
            logger.error("获取公开文件列表失败: {}", e.getMessage(), e);
            return Response.error("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件详情（不含敏感信息）
     */
    @GetMapping("/public/info/{id}")
    @ResponseBody
    public Response<FileUpload> getPublicFileInfo(@PathVariable Long id) {
        try {
            FileUpload file = fileUploadService.getFileById(id);
            if (file == null) {
                return Response.error("文件不存在");
            }
            
            // 清除敏感信息
            file.setFilePath(null);
            file.setUploaderIp(null);
            
            return Response.success("获取文件信息成功", file);
        } catch (Exception e) {
            logger.error("获取公开文件信息失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 搜索公开文件
     */
    @GetMapping("/public/search")
    @ResponseBody
    public Response<List<FileUpload>> searchPublicFiles(@RequestParam("keyword") String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Response.error("搜索关键词不能为空");
            }
            
            List<FileUpload> files = fileUploadService.searchFiles(keyword.trim());
            
            // 清除敏感信息
            files.forEach(file -> {
                file.setFilePath(null);
                file.setUploaderIp(null);
            });
            
            return Response.success("搜索文件成功", files);
        } catch (Exception e) {
            logger.error("搜索公开文件失败，关键词: {}, 错误: {}", keyword, e.getMessage(), e);
            return Response.error("搜索文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件统计信息（公开）
     */
    @GetMapping("/public/statistics")
    @ResponseBody
    public Response<Object> getPublicStatistics() {
        try {
            int totalFiles = fileUploadService.getAllFiles().size();
            return Response.success("获取统计信息成功", totalFiles);
        } catch (Exception e) {
            logger.error("获取公开统计信息失败: {}", e.getMessage(), e);
            return Response.error("获取统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近上传的文件（公开）
     */
    @GetMapping("/public/recent")
    @ResponseBody
    public Response<List<FileUpload>> getPublicRecentFiles(@RequestParam(value = "limit", defaultValue = "5") int limit) {
        try {
            List<FileUpload> recentFiles = fileUploadService.getRecentFiles(Math.min(limit, 20)); // 限制最大数量
            
            // 清除敏感信息
            recentFiles.forEach(file -> {
                file.setFilePath(null);
                file.setUploaderIp(null);
            });
            
            return Response.success("获取最近文件成功", recentFiles);
        } catch (Exception e) {
            logger.error("获取公开最近文件失败: {}", e.getMessage(), e);
            return Response.error("获取最近文件失败: " + e.getMessage());
        }
    }
}