package com.star.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Description: 文件上传配置类
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Configuration
public class FileUploadConfig {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/api/files}")
    private String urlPrefix;

    @Value("${file.upload.max-size:800MB}")
    private String maxSize;

    @PostConstruct
    public void init() {
        // 确保上传目录存在
        createUploadDirectory();
    }

    /**
     * 创建上传目录
     */
    private void createUploadDirectory() {
        try {
            String actualPath;
            
            // 判断是否为绝对路径
            if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
                // 绝对路径
                actualPath = uploadPath;
            } else {
                // 相对路径，基于项目根目录
                actualPath = System.getProperty("user.dir") + File.separator + uploadPath;
            }
            
            Path path = Paths.get(actualPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("创建上传目录: " + actualPath);
                
                // 设置目录权限（Linux/Unix系统）
                if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                    try {
                        java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions = 
                            new java.util.HashSet<>();
                        permissions.add(java.nio.file.attribute.PosixFilePermission.OWNER_READ);
                        permissions.add(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE);
                        permissions.add(java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE);
                        permissions.add(java.nio.file.attribute.PosixFilePermission.GROUP_READ);
                        permissions.add(java.nio.file.attribute.PosixFilePermission.GROUP_WRITE);
                        permissions.add(java.nio.file.attribute.PosixFilePermission.OTHERS_READ);
                        Files.setPosixFilePermissions(path, permissions);
                        System.out.println("设置目录权限成功");
                    } catch (Exception e) {
                        System.err.println("设置目录权限失败: " + e.getMessage());
                    }
                }
            }
            
            // 检查目录是否可写
            if (!Files.isWritable(path)) {
                System.err.println("警告: 上传目录不可写: " + actualPath);
            }
            
        } catch (Exception e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
            // 如果创建失败，使用临时目录作为备选
            try {
                String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "uploads";
                Files.createDirectories(Paths.get(tempPath));
                System.out.println("使用临时目录作为上传目录: " + tempPath);
            } catch (Exception ex) {
                System.err.println("使用临时目录也失败: " + ex.getMessage());
            }
        }
    }

    /**
     * 配置MultipartResolver
     */
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /**
     * 获取上传路径
     */
    public String getUploadPath() {
        return uploadPath;
    }

    /**
     * 获取URL前缀
     */
    public String getUrlPrefix() {
        return urlPrefix;
    }

    /**
     * 获取最大文件大小
     */
    public String getMaxSize() {
        return maxSize;
    }
}
