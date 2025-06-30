package com.star.controller;

import com.star.queryvo.DetailedBlog;
import com.star.service.BlogService;
import com.star.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 博文访问控制控制器
 * @Date: Created in 2025/01/01
 * @Author: TEAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
public class BlogAccessController {

    @Autowired
    private BlogService blogService;
    
    // IP封禁记录：IP -> 错误次数
    private static final ConcurrentHashMap<String, Integer> ipErrorCount = new ConcurrentHashMap<>();
    // IP封禁记录：IP -> 封禁时间
    private static final ConcurrentHashMap<String, Long> ipBlockTime = new ConcurrentHashMap<>();
    
    // 最大错误次数
    private static final int MAX_ERROR_COUNT = 3;
    // 封禁时间（毫秒）- 3分钟
    private static final long BLOCK_DURATION = 3 * 60 * 1000;

    /**
     * 验证博文访问密码
     * @param blogId 博文ID
     * @param password 用户输入的密码
     * @param session HTTP会话
     * @return 验证结果
     */
    @PostMapping("/blog/verify-password")
    @ResponseBody
    public String verifyPassword(@RequestParam Long blogId, 
                                @RequestParam String password, 
                                HttpServletRequest request,
                                HttpSession session) {
        try {
            // 获取客户端IP
            String clientIp = getClientIpAddress(request);
            
            // 检查IP是否被封禁
            if (isIpBlocked(clientIp)) {
                return "error:IP已被封禁，请稍后再试";
            }
            
            // 获取博文详情
            DetailedBlog blog = blogService.getDetailedBlog(blogId);
            
            if (blog == null) {
                return "error:博文不存在";
            }
            
            // 检查博文状态
            Integer blogStatus = blog.getBlogStatus();
            if (blogStatus == null || blogStatus == 1) {
                return "error:此博文为公开状态，无需密码";
            }
            
            // 验证密码
            String encryptedPassword = MD5Utils.code(password);
            String storedPassword = blog.getAccessPassword();
            
            if (encryptedPassword.equals(storedPassword)) {
                // 密码正确，清除错误记录，在session中记录访问权限
                clearIpErrorCount(clientIp);
                session.setAttribute("blog_access_" + blogId, true);
                return "success:密码验证成功";
            } else {
                // 密码错误，记录错误次数
                recordPasswordError(clientIp);
                return "error:验证密码错误";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error:验证失败，请重试";
        }
    }

    /**
     * 检查博文访问权限
     * @param blogId 博文ID
     * @param session HTTP会话
     * @return 是否有访问权限
     */
    @GetMapping("/blog/check-access/{blogId}")
    @ResponseBody
    public boolean checkAccess(@PathVariable Long blogId, HttpSession session) {
        try {
            // 检查session中是否有访问权限
            Boolean hasAccess = (Boolean) session.getAttribute("blog_access_" + blogId);
            if (hasAccess != null && hasAccess) {
                return true;
            }
            
            // 获取博文详情检查状态
            DetailedBlog blog = blogService.getDetailedBlog(blogId);
            if (blog == null) {
                return false;
            }
            
            Integer blogStatus = blog.getBlogStatus();
            // 普通公开状态无需密码
            if (blogStatus == null || blogStatus == 1) {
                return true;
            }
            
            // 机密或绝密状态需要密码验证
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 显示密码输入页面
     * @param blogId 博文ID
     * @param model 模型
     * @return 密码输入页面
     */
    @GetMapping("/blog/password/{blogId}")
    public String showPasswordPage(@PathVariable Long blogId, Model model) {
        try {
            DetailedBlog blog = blogService.getDetailedBlog(blogId);
            if (blog == null) {
                return "error/404";
            }
            
            model.addAttribute("blogId", blogId);
            model.addAttribute("blogTitle", blog.getTitle());
            model.addAttribute("blogStatus", blog.getBlogStatus());
            
            return "blog/password-input";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/500";
        }
    }
    
    /**
     * 安全警告页面
     * @return 安全警告页面
     */
    @GetMapping("/security-warning")
    public String securityWarning() {
        return "security-warning";
    }
    
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 检查IP是否被封禁
     * @param ip IP地址
     * @return 是否被封禁
     */
    private boolean isIpBlocked(String ip) {
        Long blockTime = ipBlockTime.get(ip);
        if (blockTime != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - blockTime < BLOCK_DURATION) {
                return true; // 仍在封禁期内
            } else {
                // 封禁期已过，清除记录
                ipBlockTime.remove(ip);
                ipErrorCount.remove(ip);
            }
        }
        return false;
    }
    
    /**
     * 记录密码错误
     * @param ip IP地址
     */
    private void recordPasswordError(String ip) {
        int errorCount = ipErrorCount.getOrDefault(ip, 0) + 1;
        ipErrorCount.put(ip, errorCount);
        
        if (errorCount >= MAX_ERROR_COUNT) {
            // 达到最大错误次数，封禁IP
            ipBlockTime.put(ip, System.currentTimeMillis());
            System.out.println("IP " + ip + " 因多次密码错误被封禁3分钟");
        }
    }
    
    /**
     * 清除IP错误记录
     * @param ip IP地址
     */
    private void clearIpErrorCount(String ip) {
        ipErrorCount.remove(ip);
        ipBlockTime.remove(ip);
    }
}
