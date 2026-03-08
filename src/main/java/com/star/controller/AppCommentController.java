package com.star.controller;

import com.star.entity.Comment;
import com.star.service.CommentService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: APP评论接口控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/comment")
public class AppCommentController {

    @Autowired
    private CommentService commentService;

    @Value("${comment.avatar}")
    private String defaultAvatar;

    @GetMapping("/list/{blogId}")
    public Result getCommentList(@PathVariable Long blogId) {
        try {
            List<Comment> comments = commentService.listCommentByBlogId(blogId);
            return Result.success("获取成功", comments);
        } catch (Exception e) {
            return Result.error("获取评论失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result addComment(
            @RequestParam("blogId") Long blogId,
            @RequestParam("nickname") String nickname,
            @RequestParam("content") String content,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "parentId", required = false) Long parentId) {
        try {
            if (content == null || content.trim().isEmpty()) {
                return Result.error("评论内容不能为空");
            }
            
            if (nickname == null || nickname.trim().isEmpty()) {
                return Result.error("昵称不能为空");
            }
            
            Comment comment = new Comment();
            comment.setBlogId(blogId);
            comment.setNickname(nickname.trim());
            comment.setContent(content.trim());
            comment.setAvatar(avatar != null && !avatar.isEmpty() ? avatar : defaultAvatar);
            comment.setAdminComment(false);
            
            if (parentId != null && parentId > 0) {
                comment.setParentCommentId(parentId);
                Comment parentComment = commentService.getEmailByParentId(parentId);
                commentService.saveComment(comment, parentComment);
            } else {
                commentService.saveComment(comment, null);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", comment.getId());
            result.put("nickname", comment.getNickname());
            result.put("content", comment.getContent());
            result.put("avatar", comment.getAvatar());
            result.put("createTime", comment.getCreateTime());
            result.put("parentId", comment.getParentCommentId());
            
            return Result.success("评论成功", result);
        } catch (Exception e) {
            return Result.error("评论失败: " + e.getMessage());
        }
    }

    @GetMapping("/delete/{blogId}/{id}")
    public Result deleteComment(
            @PathVariable Long blogId,
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        try {
            commentService.deleteComment(new Comment(), id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
