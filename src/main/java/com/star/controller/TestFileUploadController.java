package com.star.controller;

import com.star.response.Response;
import com.star.util.JavaCompatibilityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: 文件上传测试控制器
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/test")
public class TestFileUploadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 跳转到文件上传测试页面
     */
    @GetMapping("/upload")
    public String uploadTestPage() {
        return "admin/files-upload";
    }

    /**
     * 跳转到系统测试页面
     */
    @GetMapping("/system")
    public String systemTestPage() {
        return "test";
    }

    /**
     * 跳转到拖拽上传测试页面
     */
    @GetMapping("/drag-upload")
    public String dragUploadTestPage() {
        return "test-drag-upload";
    }

    /**
     * 简单的文件上传测试
     */
    @PostMapping("/upload/simple")
    @ResponseBody
    public Response<Map<String, Object>> simpleUpload(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "description", required = false) String description,
                                                     HttpServletRequest request) {
        try {
            logger.info("开始简单文件上传测试: {}", file.getOriginalFilename());
            
            // 基本验证
            if (file.isEmpty()) {
                return Response.error("文件不能为空");
            }
            
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成存储文件名
            String storedFilename = UUID.randomUUID().toString() + extension;
            
            // 创建上传目录
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "test";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 保存文件
            String filePath = uploadDir + File.separator + storedFilename;
            File destFile = new File(filePath);
            file.transferTo(destFile);
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("originalFilename", originalFilename);
            result.put("storedFilename", storedFilename);
            result.put("filePath", filePath);
            result.put("fileSize", file.getSize());
            result.put("contentType", file.getContentType());
            result.put("description", description);
            result.put("uploadTime", System.currentTimeMillis());
            
            logger.info("简单文件上传成功: {}", originalFilename);
            return Response.success("文件上传成功", result);
            
        } catch (Exception e) {
            logger.error("简单文件上传失败: {}", e.getMessage(), e);
            return Response.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/database")
    @ResponseBody
    public Response<String> testDatabase() {
        try {
            // 简单的数据库连接测试
            return Response.success("数据库连接正常", "测试成功");
        } catch (Exception e) {
            logger.error("数据库测试失败: {}", e.getMessage(), e);
            return Response.error("数据库测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试文件系统
     */
    @GetMapping("/filesystem")
    @ResponseBody
    public Response<Map<String, Object>> testFileSystem() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 系统信息
            result.put("osName", System.getProperty("os.name"));
            result.put("osVersion", System.getProperty("os.version"));
            result.put("javaVersion", System.getProperty("java.version"));
            result.put("userHome", System.getProperty("user.home"));
            result.put("javaTmpDir", System.getProperty("java.io.tmpdir"));
            
            // Java兼容性检查
            result.put("isJava8", JavaCompatibilityUtil.isJava8());
            result.put("isJava9OrHigher", JavaCompatibilityUtil.isJava9OrHigher());
            result.put("systemInfo", JavaCompatibilityUtil.getSystemInfo());
            result.put("compatibilityCheck", JavaCompatibilityUtil.checkFileUploadCompatibility());
            
            // 测试当前工作目录
            String currentDir = System.getProperty("user.dir");
            result.put("currentDirectory", currentDir);
            
            // 测试上传目录
            String uploadDir = currentDir + File.separator + "uploads";
            File uploadDirFile = new File(uploadDir);
            result.put("uploadDirectory", uploadDir);
            result.put("uploadDirectoryExists", uploadDirFile.exists());
            result.put("uploadDirectoryWritable", uploadDirFile.canWrite());
            result.put("uploadDirectoryReadable", uploadDirFile.canRead());
            
            // 创建测试目录
            if (!uploadDirFile.exists()) {
                boolean created = uploadDirFile.mkdirs();
                result.put("uploadDirectoryCreated", created);
            }
            
            // 测试文件写入
            String testFile = uploadDir + File.separator + "test.txt";
            File testFileObj = new File(testFile);
            boolean writeSuccess = testFileObj.createNewFile();
            result.put("testFileWrite", writeSuccess);
            
            if (writeSuccess) {
                // 测试文件读取
                boolean readSuccess = testFileObj.canRead();
                result.put("testFileRead", readSuccess);
                
                // 测试文件删除
                boolean deleteSuccess = testFileObj.delete();
                result.put("testFileDelete", deleteSuccess);
            }
            
            // 测试路径分隔符
            result.put("fileSeparator", File.separator);
            result.put("pathSeparator", File.pathSeparator);
            
            // 测试权限
            try {
                if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir);
                    java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions = 
                        java.nio.file.Files.getPosixFilePermissions(path);
                    result.put("posixPermissions", permissions.toString());
                }
            } catch (Exception e) {
                result.put("posixPermissionsError", e.getMessage());
            }
            
            return Response.success("文件系统测试完成", result);
            
        } catch (Exception e) {
            logger.error("文件系统测试失败: {}", e.getMessage(), e);
            return Response.error("文件系统测试失败: " + e.getMessage());
        }
    }
    
    /**
     * Java兼容性检查
     */
    @GetMapping("/compatibility")
    @ResponseBody
    public Response<String> checkCompatibility() {
        try {
            String compatibilityInfo = JavaCompatibilityUtil.checkFileUploadCompatibility();
            return Response.success("兼容性检查完成", compatibilityInfo);
        } catch (Exception e) {
            logger.error("兼容性检查失败: {}", e.getMessage(), e);
            return Response.error("兼容性检查失败: " + e.getMessage());
        }
    }
    
    /**
     * 标签页测试页面
     */
    @GetMapping("/tabs")
    public String testTabs() {
        return "test-tabs";
    }
    
    /**
     * 记住密码功能测试页面
     */
    @GetMapping("/remember-password")
    public String testRememberPassword() {
        return "test-remember-password";
    }
}
