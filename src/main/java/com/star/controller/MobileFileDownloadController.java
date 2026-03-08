package com.star.controller;

import com.star.annotation.AccessLimit;
import com.star.entity.FileUpload;
import com.star.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @Description: 移动端文件下载控制器
 * @Date: Created in 2025/01/01
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Controller
@RequestMapping("/api/mobile")
public class MobileFileDownloadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 移动端优化的文件下载接口
     */
    @AccessLimit(seconds = 10, maxCount = 10)
    @GetMapping("/download/{id}")
    public void mobileDownloadFile(@PathVariable Long id, 
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        try {
            logger.info("移动端文件下载请求: ID={}, User-Agent={}", id, request.getHeader("User-Agent"));
            
            FileUpload fileUpload = fileUploadService.getFileById(id);
            if (fileUpload == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>文件不存在</h1></body></html>");
                return;
            }

            File file = new File(fileUpload.getFilePath());
            if (!file.exists() || !file.isFile()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>文件不存在</h1></body></html>");
                return;
            }

            // 移动端优化的下载处理
            downloadFileForMobile(fileUpload, file, request, response);
            
            // 增加下载次数
            // 注意：这里应该调用incrementDownloadCount方法，但当前接口中没有
            // 可以考虑在FileUploadService中添加这个方法
            
        } catch (Exception e) {
            logger.error("移动端文件下载失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>下载失败</h1></body></html>");
            } catch (Exception ex) {
                logger.error("响应写入失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 移动端文件下载处理
     */
    private void downloadFileForMobile(FileUpload fileUpload, File file, 
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // 检测用户代理
        String userAgent = request.getHeader("User-Agent");
        boolean isMobile = isMobileDevice(userAgent);
        
        logger.info("检测到设备类型: {}", isMobile ? "移动端" : "桌面端");

        // 清除所有响应头
        response.reset();
        
        // 设置基础响应头
        response.setContentType(fileUpload.getFileType());
        response.setContentLengthLong(fileUpload.getFileSize());
        
        // 移动端特殊响应头
        if (isMobile) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            response.setHeader("Content-Transfer-Encoding", "binary");
        }
        
        // 文件名处理（移动端兼容）
        String originalFilename = fileUpload.getOriginalFilename();
        String encodedFilename = encodeFilenameForMobile(originalFilename, userAgent);
        
        // 设置Content-Disposition
        if (isMobile) {
            // 移动端强制下载
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + encodedFilename + "\"");
        } else {
            // 桌面端
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename);
        }
        
        // 根据文件类型设置Content-Type
        setMobileCompatibleContentType(response, fileUpload.getFileType(), originalFilename);

        // 输出文件
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
        
        logger.info("移动端文件下载成功: {} ({} bytes)", originalFilename, fileUpload.getFileSize());
    }

    /**
     * 检测是否为移动设备
     */
    private boolean isMobileDevice(String userAgent) {
        if (userAgent == null) return false;
        
        String ua = userAgent.toLowerCase();
        return ua.contains("mobile") || 
               ua.contains("android") || 
               ua.contains("iphone") || 
               ua.contains("ipad") || 
               ua.contains("ipod") || 
               ua.contains("blackberry") || 
               ua.contains("windows phone");
    }

    /**
     * 移动端文件名编码
     */
    private String encodeFilenameForMobile(String filename, String userAgent) {
        try {
            if (userAgent != null && userAgent.toLowerCase().contains("android")) {
                // Android设备使用UTF-8编码
                return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            } else if (userAgent != null && userAgent.toLowerCase().contains("iphone")) {
                // iOS设备使用ISO-8859-1编码
                return URLEncoder.encode(filename, "ISO-8859-1");
            } else {
                // 其他设备尝试UTF-8
                return URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            // 编码失败时返回原始文件名
            return filename;
        }
    }

    /**
     * 设置移动端兼容的Content-Type
     */
    private void setMobileCompatibleContentType(HttpServletResponse response, String fileType, String filename) {
        if (fileType == null) {
            response.setContentType("application/octet-stream");
            return;
        }
        
        // 移动端特殊处理
        if (fileType.startsWith("image/")) {
            response.setContentType(fileType);
        } else if (fileType.equals("application/pdf")) {
            response.setContentType("application/pdf");
        } else if (fileType.startsWith("text/")) {
            response.setContentType(fileType + "; charset=UTF-8");
        } else if (fileType.contains("zip") || fileType.contains("rar") || fileType.contains("7z")) {
            response.setContentType("application/octet-stream");
        } else if (fileType.startsWith("video/")) {
            response.setContentType(fileType);
        } else if (fileType.startsWith("audio/")) {
            response.setContentType(fileType);
        } else {
            // 默认设置为二进制流
            response.setContentType("application/octet-stream");
        }
    }
}
