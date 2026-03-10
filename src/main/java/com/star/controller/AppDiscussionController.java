package com.star.controller;

import com.star.entity.AppDiscussion;
import com.star.entity.AppDiscussionComment;
import com.star.service.AppDiscussionCommentService;
import com.star.service.AppDiscussionService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/discussion")
public class AppDiscussionController {

    @Autowired
    private AppDiscussionService appDiscussionService;

    @Autowired
    private AppDiscussionCommentService appDiscussionCommentService;

    @PostMapping("/create")
    public Result createDiscussion(@RequestParam("userId") Long userId,
                                @RequestParam("qqNumber") String qqNumber,
                                @RequestParam("nickname") String nickname,
                                @RequestParam(value = "avatar", required = false) String avatar,
                                @RequestParam("title") String title,
                                @RequestParam("content") String content) {
        AppDiscussion discussion = appDiscussionService.createDiscussion(userId, qqNumber, nickname, avatar, title, content);
        return Result.success(discussion);
    }

    @GetMapping("/list")
    public Result getDiscussionList(@RequestParam(value = "page", defaultValue = "1") int page,
                                  @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        List<AppDiscussion> list = appDiscussionService.getApprovedDiscussions(page, pageSize);
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result searchDiscussions(@RequestParam("keyword") String keyword,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        List<AppDiscussion> list = appDiscussionService.searchDiscussions(keyword, page, pageSize);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result getDiscussionDetail(@PathVariable Long id) {
        AppDiscussion discussion = appDiscussionService.getDiscussionById(id);
        if (discussion != null) {
            appDiscussionService.viewDiscussion(id);
            List<AppDiscussionComment> comments = appDiscussionCommentService.getCommentsByDiscussionId(id);
            Map<String, Object> result = new HashMap<>();
            result.put("discussion", discussion);
            result.put("comments", comments);
            return Result.success(result);
        }
        return Result.error("话题不存在");
    }

    @GetMapping("/my/{userId}")
    public Result getMyDiscussions(@PathVariable Long userId) {
        List<AppDiscussion> list = appDiscussionService.getUserDiscussions(userId);
        return Result.success(list);
    }

    @PostMapping("/like/{id}")
    public Result likeDiscussion(@PathVariable Long id) {
        boolean success = appDiscussionService.likeDiscussion(id);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    @PostMapping("/comment/create")
    public Result createComment(@RequestParam("discussionId") Long discussionId,
                              @RequestParam("userId") Long userId,
                              @RequestParam("qqNumber") String qqNumber,
                              @RequestParam("nickname") String nickname,
                              @RequestParam(value = "avatar", required = false) String avatar,
                              @RequestParam("content") String content,
                              @RequestParam(value = "parentCommentId", required = false) Long parentCommentId) {
        AppDiscussionComment comment = appDiscussionCommentService.createComment(
            discussionId, userId, qqNumber, nickname, avatar, content, parentCommentId
        );
        return Result.success(comment);
    }

    @GetMapping("/comment/{discussionId}")
    public Result getComments(@PathVariable Long discussionId) {
        List<AppDiscussionComment> comments = appDiscussionCommentService.getCommentsByDiscussionId(discussionId);
        return Result.success(comments);
    }

    @GetMapping("/comment/replies/{parentCommentId}")
    public Result getReplies(@PathVariable Long parentCommentId) {
        List<AppDiscussionComment> replies = appDiscussionCommentService.getRepliesByParentCommentId(parentCommentId);
        return Result.success(replies);
    }

    @PostMapping("/comment/like/{id}")
    public Result likeComment(@PathVariable Long id) {
        boolean success = appDiscussionCommentService.likeComment(id);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }
}