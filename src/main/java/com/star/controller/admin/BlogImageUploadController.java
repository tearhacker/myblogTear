package com.star.controller.admin;

import com.star.entity.FileUpload;
import com.star.response.Response;
import com.star.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 博客图片上传控制器
 * @Date: Created in 2025/01/01
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/admin/blog")
public class BlogImageUploadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传博客首图
     */
    @PostMapping("/upload-first-picture")
    @ResponseBody
    public Response<Map<String, Object>> uploadFirstPicture(@RequestParam("file") MultipartFile file,
                                                           HttpServletRequest request) {
        try {
            // 验证文件类型（只允许图片）
            if (!isImageFile(file)) {
                return Response.error("只支持图片格式：jpg、jpeg、png、gif、webp");
            }

            // 验证文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Response.error("图片大小不能超过5MB");
            }

            // 上传文件
            FileUpload uploadedFile = fileUploadService.uploadFile(file, "博客首图", request);
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("fileId", uploadedFile.getId());
            result.put("fileName", uploadedFile.getOriginalFilename());
            result.put("fileUrl", uploadedFile.getFileUrl());
            result.put("fileSize", uploadedFile.getFileSize());
            result.put("fileType", uploadedFile.getFileType());
            
            logger.info("博客首图上传成功: {} -> {}", uploadedFile.getOriginalFilename(), uploadedFile.getStoredFilename());
            return Response.success("图片上传成功", result);
            
        } catch (Exception e) {
            logger.error("博客首图上传失败: {}", e.getMessage(), e);
            return Response.error("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 预览上传的图片
     */
    @GetMapping("/preview-image/{fileId}")
    @ResponseBody
    public Response<Map<String, Object>> previewImage(@PathVariable Long fileId) {
        try {
            FileUpload file = fileUploadService.getFileById(fileId);
            if (file == null) {
                return Response.error("图片不存在");
            }

            // 检查是否为图片类型
            if (!file.getFileType().startsWith("image/")) {
                return Response.error("文件不是图片格式");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("fileId", file.getId());
            result.put("fileName", file.getOriginalFilename());
            result.put("fileUrl", file.getFileUrl());
            result.put("fileSize", file.getFileSize());
            result.put("fileType", file.getFileType());
            
            return Response.success("获取图片信息成功", result);
            
        } catch (Exception e) {
            logger.error("预览图片失败，ID: {}, 错误: {}", fileId, e.getMessage(), e);
            return Response.error("预览图片失败: " + e.getMessage());
        }
    }

    /**
     * 删除上传的图片
     */
    @DeleteMapping("/delete-image/{fileId}")
    @ResponseBody
    public Response<String> deleteImage(@PathVariable Long fileId) {
        try {
            boolean success = fileUploadService.deleteFile(fileId);
            if (success) {
                logger.info("博客图片删除成功，ID: {}", fileId);
                return Response.success("图片删除成功");
            } else {
                return Response.error("图片删除失败");
            }
        } catch (Exception e) {
            logger.error("删除博客图片失败，ID: {}, 错误: {}", fileId, e.getMessage(), e);
            return Response.error("图片删除失败: " + e.getMessage());
        }
    }

    /**
     * 验证文件是否为图片
     */
    private boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return contentType.startsWith("image/") && 
               (contentType.equals("image/jpeg") || 
                contentType.equals("image/jpg") || 
                contentType.equals("image/png") || 
                contentType.equals("image/gif") || 
                contentType.equals("image/webp"));
    }
}
