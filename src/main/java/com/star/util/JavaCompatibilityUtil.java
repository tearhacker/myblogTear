package com.star.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: Java兼容性工具类
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
public class JavaCompatibilityUtil {

    /**
     * 创建Set集合（Java 8兼容）
     * @param elements 元素数组
     * @param <T> 元素类型
     * @return Set集合
     */
    @SafeVarargs
    public static <T> Set<T> createSet(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    /**
     * 检查Java版本
     * @return Java版本信息
     */
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * 检查是否为Java 8
     * @return 是否为Java 8
     */
    public static boolean isJava8() {
        String version = getJavaVersion();
        return version.startsWith("1.8") || version.startsWith("8");
    }

    /**
     * 检查是否为Java 9或更高版本
     * @return 是否为Java 9+
     */
    public static boolean isJava9OrHigher() {
        String version = getJavaVersion();
        try {
            int majorVersion = Integer.parseInt(version.split("\\.")[0]);
            return majorVersion >= 9;
        } catch (Exception e) {
            // 如果解析失败，尝试其他方式
            return !version.startsWith("1.8") && !version.startsWith("8");
        }
    }

    /**
     * 获取系统信息
     * @return 系统信息字符串
     */
    public static String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Java版本: ").append(getJavaVersion()).append("\n");
        info.append("操作系统: ").append(System.getProperty("os.name")).append("\n");
        info.append("操作系统版本: ").append(System.getProperty("os.version")).append("\n");
        info.append("用户目录: ").append(System.getProperty("user.home")).append("\n");
        info.append("工作目录: ").append(System.getProperty("user.dir")).append("\n");
        info.append("临时目录: ").append(System.getProperty("java.io.tmpdir")).append("\n");
        info.append("文件分隔符: ").append(System.getProperty("file.separator")).append("\n");
        info.append("路径分隔符: ").append(System.getProperty("path.separator")).append("\n");
        
        return info.toString();
    }

    /**
     * 检查文件上传功能兼容性
     * @return 兼容性检查结果
     */
    public static String checkFileUploadCompatibility() {
        StringBuilder result = new StringBuilder();
        result.append("=== 文件上传功能兼容性检查 ===\n");
        
        // Java版本检查
        if (isJava8()) {
            result.append("✓ Java 8 兼容性: 支持\n");
        } else if (isJava9OrHigher()) {
            result.append("✓ Java 9+ 兼容性: 支持\n");
        } else {
            result.append("⚠ Java版本: ").append(getJavaVersion()).append(" (未知版本)\n");
        }
        
        // 操作系统检查
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            result.append("✓ Windows系统: 支持\n");
        } else if (osName.contains("linux")) {
            result.append("✓ Linux系统: 支持\n");
        } else if (osName.contains("mac")) {
            result.append("✓ macOS系统: 支持\n");
        } else {
            result.append("⚠ 操作系统: ").append(System.getProperty("os.name")).append(" (可能支持)\n");
        }
        
        // 文件系统检查
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            java.io.File tempFile = new java.io.File(tempDir, "compatibility_test.tmp");
            boolean canWrite = tempFile.createNewFile();
            if (canWrite) {
                result.append("✓ 文件系统写入: 正常\n");
                tempFile.delete();
            } else {
                result.append("✗ 文件系统写入: 失败\n");
            }
        } catch (Exception e) {
            result.append("✗ 文件系统写入: 异常 - ").append(e.getMessage()).append("\n");
        }
        
        // 权限检查
        try {
            String userDir = System.getProperty("user.dir");
            java.io.File userDirFile = new java.io.File(userDir);
            if (userDirFile.canWrite()) {
                result.append("✓ 工作目录权限: 可写\n");
            } else {
                result.append("⚠ 工作目录权限: 只读\n");
            }
        } catch (Exception e) {
            result.append("✗ 工作目录权限: 检查失败 - ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
}
