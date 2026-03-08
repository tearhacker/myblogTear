package com.star.controller.admin;

import com.star.entity.FileUpload;
import com.star.response.Response;
import com.star.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文件上传控制器
 * @Date: Created in 2025/09/07
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Controller
@RequestMapping("/admin")
public class FileUploadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 跳转到文件管理页面
     */
    @GetMapping("/files")
    public String files(Model model) {
        try {
            List<FileUpload> files = fileUploadService.getAllFiles();
            Map<String, Object> statistics = fileUploadService.getFileStatistics();
            
            model.addAttribute("files", files);
            model.addAttribute("statistics", statistics);
            
            return "admin/files";
        } catch (Exception e) {
            logger.error("获取文件列表失败: {}", e.getMessage(), e);
            model.addAttribute("error", "获取文件列表失败: " + e.getMessage());
            return "admin/files";
        }
    }

    /**
     * 跳转到文件上传页面
     */
    @GetMapping("/files/upload")
    public String uploadPage() {
        return "admin/files-upload";
    }

    /**
     * 单文件上传
     */
    @PostMapping("/files/upload")
    @ResponseBody
    public Response<FileUpload> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "description", required = false) String description,
                                         HttpServletRequest request) {
        try {
            FileUpload uploadedFile = fileUploadService.uploadFile(file, description, request);
            logger.info("文件上传成功: {}", uploadedFile.getOriginalFilename());
            return Response.success("文件上传成功", uploadedFile);
        } catch (Exception e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }

    /**
     * 测试数据库连接和表结构
     */
    @GetMapping("/files/test")
    @ResponseBody
    public Response<String> testDatabase() {
        try {
            List<FileUpload> files = fileUploadService.getAllFiles();
            return Response.success("数据库连接正常，表结构正常，当前文件数量: " + files.size());
        } catch (Exception e) {
            logger.error("数据库测试失败: {}", e.getMessage(), e);
            return Response.error("数据库测试失败: " + e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/files/upload/batch")
    @ResponseBody
    public Response<List<FileUpload>> uploadFiles(@RequestParam("files") MultipartFile[] files,
                                                @RequestParam(value = "descriptions", required = false) String[] descriptions,
                                                HttpServletRequest request) {
        try {
            List<FileUpload> uploadedFiles = fileUploadService.uploadFiles(files, descriptions, request);
            logger.info("批量文件上传成功，共上传 {} 个文件", uploadedFiles.size());
            return Response.success("批量文件上传成功", uploadedFiles);
        } catch (Exception e) {
            logger.error("批量文件上传失败: {}", e.getMessage(), e);
            return Response.error(e.getMessage());
        }
    }

    /**
     * 获取文件列表（分页）
     */
    @GetMapping("/files/list")
    @ResponseBody
    public Response<Map<String, Object>> getFileList(@RequestParam(value = "page", defaultValue = "1") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "type", required = false) String type,
                                                    @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            List<FileUpload> files;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 搜索文件
                files = fileUploadService.searchFiles(keyword.trim());
            } else if (type != null && !type.trim().isEmpty()) {
                // 按类型过滤
                files = fileUploadService.getFilesByType(type);
            } else {
                // 分页获取所有文件
                files = fileUploadService.getFilesWithPage(page, size);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("files", files);
            result.put("page", page);
            result.put("size", size);
            result.put("total", files.size());

            return Response.success("获取文件列表成功", result);
        } catch (Exception e) {
            logger.error("获取文件列表失败: {}", e.getMessage(), e);
            return Response.error("获取文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/files/{id}")
    @ResponseBody
    public Response<FileUpload> getFileDetail(@PathVariable Long id) {
        try {
            FileUpload file = fileUploadService.getFileById(id);
            if (file == null) {
                return Response.error("文件不存在");
            }
            return Response.success("获取文件详情成功", file);
        } catch (Exception e) {
            logger.error("获取文件详情失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("获取文件详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新文件信息
     */
    @PutMapping("/files/{id}")
    @ResponseBody
    public Response<String> updateFile(@PathVariable Long id,
                                     @RequestParam(value = "description", required = false) String description) {
        try {
            FileUpload file = fileUploadService.getFileById(id);
            if (file == null) {
                return Response.error("文件不存在");
            }

            if (description != null) {
                file.setDescription(description);
            }

            boolean success = fileUploadService.updateFile(file);
            if (success) {
                return Response.success("文件信息更新成功");
            } else {
                return Response.error("文件信息更新失败");
            }
        } catch (Exception e) {
            logger.error("更新文件信息失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("更新文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 软删除文件
     */
    @DeleteMapping("/files/{id}")
    @ResponseBody
    public Response<String> deleteFile(@PathVariable Long id) {
        try {
            boolean success = fileUploadService.deleteFile(id);
            if (success) {
                logger.info("文件删除成功，ID: {}", id);
                return Response.success("文件删除成功");
            } else {
                return Response.error("文件删除失败");
            }
        } catch (Exception e) {
            logger.error("删除文件失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 永久删除文件
     */
    @DeleteMapping("/files/{id}/permanent")
    @ResponseBody
    public Response<String> permanentDeleteFile(@PathVariable Long id) {
        try {
            boolean success = fileUploadService.permanentDeleteFile(id);
            if (success) {
                logger.info("文件永久删除成功，ID: {}", id);
                return Response.success("文件永久删除成功");
            } else {
                return Response.error("文件永久删除失败");
            }
        } catch (Exception e) {
            logger.error("永久删除文件失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("永久删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件统计信息
     */
    @GetMapping("/files/statistics")
    @ResponseBody
    public Response<Map<String, Object>> getFileStatistics() {
        try {
            Map<String, Object> statistics = fileUploadService.getFileStatistics();
            return Response.success("获取统计信息成功", statistics);
        } catch (Exception e) {
            logger.error("获取文件统计信息失败: {}", e.getMessage(), e);
            return Response.error("获取文件统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近上传的文件
     */
    @GetMapping("/files/recent")
    @ResponseBody
    public Response<List<FileUpload>> getRecentFiles(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        try {
            List<FileUpload> recentFiles = fileUploadService.getRecentFiles(limit);
            return Response.success("获取最近文件成功", recentFiles);
        } catch (Exception e) {
            logger.error("获取最近文件失败: {}", e.getMessage(), e);
            return Response.error("获取最近文件失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     */
    @GetMapping("/files/{id}/exists")
    @ResponseBody
    public Response<Boolean> checkFileExists(@PathVariable Long id) {
        try {
            boolean exists = fileUploadService.isFileExists(id);
            return Response.success("检查文件存在性成功", exists);
        } catch (Exception e) {
            logger.error("检查文件存在性失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Response.error("检查文件存在性失败: " + e.getMessage());
        }
    }
}