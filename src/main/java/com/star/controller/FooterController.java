package com.star.controller;

import com.star.queryvo.FirstPageBlog;
import com.star.response.Response;
import com.star.service.BlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description: 页脚控制器
 * @Date: Created in 2025/09/07
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
@RequestMapping("/footer")
public class FooterController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BlogService blogService;

    /**
     * 获取最新博客列表（用于页脚显示）
     */
    @GetMapping("/newblog")
    @ResponseBody
    public Response<List<FirstPageBlog>> getNewBlogs() {
        try {
            // 获取最新的博客列表
            List<FirstPageBlog> newBlogs = blogService.getAllFirstPageBlog();
            // 只返回前5篇
            if (newBlogs.size() > 5) {
                newBlogs = newBlogs.subList(0, 5);
            }
            return Response.success("获取最新博客成功", newBlogs);
        } catch (Exception e) {
            logger.error("获取最新博客失败: {}", e.getMessage(), e);
            return Response.error("获取最新博客失败: " + e.getMessage());
        }
    }

}
