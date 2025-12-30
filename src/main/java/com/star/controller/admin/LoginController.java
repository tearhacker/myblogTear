package com.star.controller.admin;

import com.star.entity.User;
import com.star.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description: 用户登录控制器
 * @Date: Created in 21:40 2020/5/27
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * @Description: 跳转登录页面
     * @Auther: ONESTAR
     * @Date: 21:57 2020/5/27
     * @Param:
     * @Return: 返回登录页面
     */
    @GetMapping
    public String loginPage(){
        return "admin/login";
    }

    /**
     * @Description: 登录校验
     * @Auther: ONESTAR
     * @Date: 10:04 2020/5/27
     * @Param: username:用户名
     * @Param: password:密码
     * @Param: rememberMe:是否记住密码
     * @Param: session:session域
     * @Param: request:请求对象
     * @Param: response:响应对象
     * @Param: attributes:返回页面消息
     * @Return: 登录成功跳转登录成功页面，登录失败返回登录页面
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpSession session,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        RedirectAttributes attributes)
    {
        User user = userService.checkUser(username, password);
        if (user != null) {
            user.setPassword(null);
            session.setAttribute("user", user);
            
            // 如果勾选了记住密码，设置Cookie
            if ("on".equals(rememberMe) || "true".equals(rememberMe)) {
                // 设置用户名Cookie，保存30天
                Cookie usernameCookie = new Cookie("remembered_username", username);
                usernameCookie.setMaxAge(30 * 24 * 60 * 60); // 30天
                usernameCookie.setPath("/");
                response.addCookie(usernameCookie);
                
                // 设置密码Cookie，保存30天
                Cookie passwordCookie = new Cookie("remembered_password", password);
                passwordCookie.setMaxAge(30 * 24 * 60 * 60); // 30天
                passwordCookie.setPath("/");
                response.addCookie(passwordCookie);
            } else {
                // 如果没有勾选记住密码，清除相关Cookie
                clearRememberMeCookies(request, response);
            }
            
            // 设置session永不过期
            session.setMaxInactiveInterval(-1);
            return "admin/index";
        } else {
            attributes.addFlashAttribute("message", "用户名和密码错误");
            return "redirect:/admin";
        }
    }


    /**
     * @Description: 注销
     * @Auther: ONESTAR
     * @Date: 10:15 2020/5/27
     * @Param: session:session域
     * @Param: request:请求对象
     * @Param: response:响应对象
     * @Return: 返回登录页面
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        session.removeAttribute("user");
        // 清除记住密码的Cookie
        clearRememberMeCookies(request, response);
        return "redirect:/admin";
    }
    
    /**
     * @Description: 清除记住密码相关的Cookie
     * @Auther: ONESTAR
     * @Date: 2025/09/20
     * @Param: request:请求对象
     * @Param: response:响应对象
     */
    private void clearRememberMeCookies(HttpServletRequest request, HttpServletResponse response) {
        // 清除用户名Cookie
        Cookie usernameCookie = new Cookie("remembered_username", "");
        usernameCookie.setMaxAge(0);
        usernameCookie.setPath("/");
        response.addCookie(usernameCookie);
        
        // 清除密码Cookie
        Cookie passwordCookie = new Cookie("remembered_password", "");
        passwordCookie.setMaxAge(0);
        passwordCookie.setPath("/");
        response.addCookie(passwordCookie);
    }
}