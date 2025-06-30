package com.star.controller.admin;

import com.star.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 图片上传测试控制器
 * @Date: Created in 2025/01/01
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/admin/test")
public class ImageUploadTestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 简单的图片上传测试接口
     */
    @PostMapping("/upload-image")
    @ResponseBody
    public Response<Map<String, Object>> testUploadImage(@RequestParam("file") MultipartFile file,
                                                       HttpServletRequest request) {
        try {
            logger.info("收到图片上传请求: {}", file.getOriginalFilename());
            
            // 基本验证
            if (file.isEmpty()) {
                return Response.error("文件为空");
            }
            
            // 验证文件类型
            if (!file.getContentType().startsWith("image/")) {
                return Response.error("只支持图片格式");
            }
            
            // 验证文件大小
            if (file.getSize() > 5 * 1024 * 1024) {
                return Response.error("文件大小不能超过5MB");
            }
            
            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("fileType", file.getContentType());
            result.put("uploadTime", System.currentTimeMillis());
            
            logger.info("图片上传测试成功: {}", file.getOriginalFilename());
            return Response.success("图片上传测试成功", result);
            
        } catch (Exception e) {
            logger.error("图片上传测试失败: {}", e.getMessage(), e);
            return Response.error("上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查系统状态
     */
    @GetMapping("/status")
    @ResponseBody
    public Response<Map<String, Object>> checkSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timestamp", System.currentTimeMillis());
        status.put("javaVersion", System.getProperty("java.version"));
        status.put("osName", System.getProperty("os.name"));
        status.put("userDir", System.getProperty("user.dir"));
        status.put("tempDir", System.getProperty("java.io.tmpdir"));
        
        return Response.success("系统状态正常", status);
    }
}
