package com.star.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.star.queryvo.DetailedBlog;
import com.star.queryvo.FirstPageBlog;
import com.star.queryvo.RecommendBlog;
import com.star.service.BlogService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: APP博客接口控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/blog")
public class AppBlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("/list")
    public Result getBlogList(
            @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
            @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize) {
        try {
            String orderBy = "update_time desc";
            PageHelper.startPage(pageNum, pageSize, orderBy);
            List<FirstPageBlog> blogList = blogService.getAllFirstPageBlog();
            PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogList);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("pages", pageInfo.getPages());
            result.put("hasNextPage", pageInfo.isHasNextPage());
            
            return Result.success("获取成功", result);
        } catch (Exception e) {
            return Result.error("获取博客列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommend")
    public Result getRecommendBlog() {
        try {
            List<RecommendBlog> recommendList = blogService.getRecommendedBlog();
            return Result.success("获取成功", recommendList);
        } catch (Exception e) {
            return Result.error("获取推荐博客失败: " + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result getBlogDetail(@PathVariable Long id) {
        try {
            DetailedBlog blog = blogService.getDetailedBlog(id);
            if (blog == null) {
                return Result.error("博客不存在");
            }
            
            if (blog.getBlogStatus() != null && blog.getBlogStatus() > 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", blog.getId());
                result.put("title", blog.getTitle());
                result.put("firstPicture", blog.getFirstPicture());
                result.put("blogStatus", blog.getBlogStatus());
                result.put("needPassword", true);
                return Result.success("该文章需要密码访问", result);
            }
            
            return Result.success("获取成功", blog);
        } catch (Exception e) {
            return Result.error("获取博客详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/detail/{id}/verify")
    public Result verifyBlogPassword(
            @PathVariable Long id,
            @RequestParam("password") String password) {
        try {
            DetailedBlog blog = blogService.getDetailedBlog(id);
            if (blog == null) {
                return Result.error("博客不存在");
            }
            
            if (blog.getAccessPassword() != null && 
                blog.getAccessPassword().equals(password)) {
                return Result.success("密码验证成功", blog);
            } else {
                return Result.error("密码错误");
            }
        } catch (Exception e) {
            return Result.error("密码验证失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result searchBlog(
            @RequestParam("query") String query,
            @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
            @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return Result.error("搜索关键词不能为空");
            }
            
            PageHelper.startPage(pageNum, pageSize);
            List<FirstPageBlog> blogList = blogService.getSearchBlog(query.trim());
            PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogList);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("pages", pageInfo.getPages());
            result.put("hasNextPage", pageInfo.isHasNextPage());
            
            return Result.success("搜索成功", result);
        } catch (Exception e) {
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

    @GetMapping("/type/{typeId}")
    public Result getBlogByType(
            @PathVariable Long typeId,
            @RequestParam(defaultValue = "1", value = "pageNum") Integer pageNum,
            @RequestParam(defaultValue = "10", value = "pageSize") Integer pageSize) {
        try {
            String orderBy = "update_time desc";
            PageHelper.startPage(pageNum, pageSize, orderBy);
            List<FirstPageBlog> blogList = blogService.getByTypeId(typeId);
            PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogList);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageInfo.getList());
            result.put("total", pageInfo.getTotal());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("pages", pageInfo.getPages());
            result.put("hasNextPage", pageInfo.isHasNextPage());
            
            return Result.success("获取成功", result);
        } catch (Exception e) {
            return Result.error("获取分类博客失败: " + e.getMessage());
        }
    }
}
