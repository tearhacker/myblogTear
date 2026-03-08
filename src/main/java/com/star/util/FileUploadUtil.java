package com.star.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 文件上传工具类
 * @Date: Created in 2025/09/07
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class FileUploadUtil {

    // 基础上传路径 - 使用默认值，支持跨平台
    private static String baseUploadPath = getDefaultUploadPath();
    
    // 文件访问URL前缀
    private static String fileUrlPrefix = "/api/files";
    
    /**
     * 获取默认上传路径，支持跨平台部署
     */
    private static String getDefaultUploadPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String userDir = System.getProperty("user.dir");
        
        // 检测部署环境
        if (isRunningInDocker()) {
            // Docker容器环境
            return "/app/uploads/";
        } else if (isRunningInTomcat()) {
            // Tomcat部署环境
            String tomcatHome = System.getProperty("catalina.home");
            if (tomcatHome != null && !tomcatHome.isEmpty()) {
                return tomcatHome + File.separator + "webapps" + File.separator + "uploads" + File.separator;
            }
        } else if (isRunningInJar()) {
            // JAR包运行环境
            return userDir + File.separator + "uploads" + File.separator;
        }
        
        // 默认情况：项目根目录下的uploads文件夹
        return userDir + File.separator + "uploads" + File.separator;
    }
    
    /**
     * 检测是否运行在Docker容器中
     */
    private static boolean isRunningInDocker() {
        try {
            File dockerFile = new File("/.dockerenv");
            return dockerFile.exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检测是否运行在Tomcat中
     */
    private static boolean isRunningInTomcat() {
        return System.getProperty("catalina.home") != null;
    }
    
    /**
     * 检测是否运行在JAR包中
     */
    private static boolean isRunningInJar() {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains(".jar");
    }
    
    static {
        // 静态初始化
        init();
    }
    
    private static void init() {
        // 创建上传目录
        createUploadDirectory();
    }
    
    /**
     * 创建上传目录，支持跨平台部署
     */
    private static void createUploadDirectory() {
        try {
            // 标准化路径分隔符
            String normalizedPath = baseUploadPath.replace("\\", File.separator).replace("/", File.separator);
            Path path = Paths.get(normalizedPath);
            
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("创建上传目录: " + normalizedPath);
            }
            
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
                    System.out.println("设置目录权限成功: " + normalizedPath);
                } catch (Exception e) {
                    System.err.println("设置目录权限失败: " + e.getMessage());
                }
            }
            
            // 检查目录是否可写
            if (!Files.isWritable(path)) {
                System.err.println("警告: 上传目录不可写: " + normalizedPath);
                // 尝试使用备选路径
                useFallbackPath();
            }
            
        } catch (Exception e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
            useFallbackPath();
        }
    }
    
    /**
     * 使用备选路径
     */
    private static void useFallbackPath() {
        try {
            // 尝试多个备选路径
            String[] fallbackPaths = {
                System.getProperty("java.io.tmpdir") + File.separator + "uploads" + File.separator,
                System.getProperty("user.home") + File.separator + "uploads" + File.separator,
                "/tmp/uploads/",  // Linux/Unix
                "C:\\temp\\uploads\\"  // Windows
            };
            
            for (String fallbackPath : fallbackPaths) {
                try {
                    Path path = Paths.get(fallbackPath);
                    Files.createDirectories(path);
                    if (Files.isWritable(path)) {
                        baseUploadPath = fallbackPath;
                        System.out.println("使用备选上传目录: " + fallbackPath);
                        return;
                    }
                } catch (Exception ex) {
                    // 继续尝试下一个路径
                }
            }
            
            System.err.println("所有备选路径都失败，文件上传功能可能不可用");
        } catch (Exception e) {
            System.err.println("使用备选路径失败: " + e.getMessage());
        }
    }
    
    // 允许的图片文件类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    );
    
    // 允许的文档文件类型
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain", "text/csv"
    );
    
    // 允许的音频文件类型
    private static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
        "audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", "audio/aac"
    );
    
    // 允许的视频文件类型
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/avi", "video/mkv", "video/wmv", "video/mov"
    );
    
    // 允许的压缩文件类型
    private static final List<String> ALLOWED_ARCHIVE_TYPES = Arrays.asList(
        "application/zip", "application/x-rar-compressed", "application/x-7z-compressed"
    );

    /**
     * 验证文件是否有效
     * @param file 上传的文件
     * @return 是否有效
     */
    public static boolean isValidFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return false;
        }
        
        // 检查文件名是否包含危险字符
        if (originalFilename.contains("../") || originalFilename.contains("..\\")) {
            return false;
        }
        
        return true;
    }

    /**
     * 验证文件类型是否被允许
     * @param file 上传的文件
     * @return 是否被允许
     */
    public static boolean isAllowedFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            // 如果没有MIME类型，暂时允许通过（用于调试）
            return true;
        }
        
        return ALLOWED_IMAGE_TYPES.contains(contentType) ||
               ALLOWED_DOCUMENT_TYPES.contains(contentType) ||
               ALLOWED_AUDIO_TYPES.contains(contentType) ||
               ALLOWED_VIDEO_TYPES.contains(contentType) ||
               ALLOWED_ARCHIVE_TYPES.contains(contentType) ||
               contentType.startsWith("text/") || // 允许所有文本文件
               contentType.startsWith("application/"); // 允许所有应用程序文件
    }

    /**
     * 验证文件大小
     * @param file 上传的文件
     * @param maxSizeInMB 最大大小（MB）
     * @return 是否在允许范围内
     */
    public static boolean isValidFileSize(MultipartFile file, long maxSizeInMB) {
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return file.getSize() <= maxSizeInBytes;
    }

    /**
     * 生成唯一的存储文件名
     * @param originalFilename 原始文件名
     * @return 唯一的存储文件名
     */
    public static String generateStoredFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        if (extension.isEmpty()) {
            return uuid + "_" + timestamp;
        } else {
            return uuid + "_" + timestamp + "." + extension;
        }
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名（不包含点）
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 根据日期创建存储目录，支持跨平台
     * @param baseDir 基础目录
     * @return 存储目录路径
     */
    public static String createDateBasedDirectory(String baseDir) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + File.separator + "MM" + File.separator + "dd");
        String datePath = sdf.format(new Date());
        
        // 标准化路径分隔符
        String normalizedBaseDir = baseDir.replace("\\", File.separator).replace("/", File.separator);
        if (!normalizedBaseDir.endsWith(File.separator)) {
            normalizedBaseDir += File.separator;
        }
        
        String fullPath = normalizedBaseDir + datePath;
        
        try {
            Path directory = Paths.get(fullPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                
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
                        Files.setPosixFilePermissions(directory, permissions);
                    } catch (Exception e) {
                        System.err.println("设置日期目录权限失败: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("无法创建上传目录: " + fullPath + ", 错误: " + e.getMessage());
        }
        
        return fullPath;
    }

    /**
     * 获取文件存储的完整路径
     * @param storedFilename 存储文件名
     * @return 完整路径
     */
    public static String getFullStoragePath(String storedFilename) {
        String dateBasedDir = createDateBasedDirectory(baseUploadPath);
        return dateBasedDir + File.separator + storedFilename;
    }

    /**
     * 保存文件到磁盘
     * @param file 上传的文件
     * @param filePath 保存路径
     * @throws IOException IO异常
     */
    public static void saveFileToDisk(MultipartFile file, String filePath) throws IOException {
        File destFile = new File(filePath);
        File parentDir = destFile.getParentFile();
        
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        file.transferTo(destFile);
    }

    /**
     * 删除磁盘文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFileFromDisk(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 检查磁盘文件是否存在
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean isFileExistsOnDisk(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    /**
     * 获取客户端IP地址
     * @param request HTTP请求对象
     * @return IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        String xForwardedForProxy = request.getHeader("Proxy-Client-IP");
        if (xForwardedForProxy != null && !xForwardedForProxy.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedForProxy)) {
            return xForwardedForProxy;
        }
        
        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 生成文件访问URL（基于文件ID）
     * @param request HTTP请求对象
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    public static String generateFileUrl(HttpServletRequest request, Long fileId) {
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.equals("/")) {
            contextPath = "";
        }
        return contextPath + fileUrlPrefix + "/download/" + fileId;
    }

    /**
     * 生成文件访问URL（基于文件名） - 保留兼容性
     * @param request HTTP请求对象
     * @param storedFilename 存储文件名
     * @return 文件访问URL
     */
    public static String generateFileUrlByName(HttpServletRequest request, String storedFilename) {
        String contextPath = request.getContextPath();
        if (contextPath == null || contextPath.equals("/")) {
            contextPath = "";
        }
        return contextPath + fileUrlPrefix + "/download/file/" + storedFilename;
    }
    
    /**
     * 获取基础上传路径
     * @return 基础上传路径
     */
    public static String getBaseUploadPath() {
        return baseUploadPath;
    }
    
    /**
     * 获取文件URL前缀
     * @return 文件URL前缀
     */
    public static String getFileUrlPrefix() {
        return fileUrlPrefix;
    }

    /**
     * 根据文件类型获取分类
     * @param contentType MIME类型
     * @return 文件分类
     */
    public static String getFileCategory(String contentType) {
        if (contentType == null) {
            return "other";
        }
        
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "image";
        } else if (ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            return "document";
        } else if (ALLOWED_AUDIO_TYPES.contains(contentType)) {
            return "audio";
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            return "video";
        } else if (ALLOWED_ARCHIVE_TYPES.contains(contentType)) {
            return "archive";
        } else {
            return "other";
        }
    }

    /**
     * 验证文件名的安全性
     * @param filename 文件名
     * @return 是否安全
     */
    public static boolean isSafeFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        // 检查是否包含危险字符
        String[] dangerousPatterns = {"..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|"};
        for (String pattern : dangerousPatterns) {
            if (filename.contains(pattern)) {
                return false;
            }
        }
        
        return true;
    }
}