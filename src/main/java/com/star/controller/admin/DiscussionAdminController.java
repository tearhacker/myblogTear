package com.star.controller.admin;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.star.entity.AppDiscussion;
import com.star.entity.AppDiscussionComment;
import com.star.entity.User;
import com.star.service.AppDiscussionCommentService;
import com.star.service.AppDiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/discussions")
public class DiscussionAdminController {

    @Autowired
    private AppDiscussionService appDiscussionService;

    @Autowired
    private AppDiscussionCommentService appDiscussionCommentService;

    @GetMapping
    public String discussions(@RequestParam(defaultValue = "9") Integer status,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           Model model) {
        PageInfo<AppDiscussion> pageInfo;
        
        if (status == 0) {
            List<AppDiscussion> list = appDiscussionService.getPendingDiscussions(pageNum, 10);
            pageInfo = new PageInfo<>(list);
        } else if (status == 1) {
            List<AppDiscussion> list = appDiscussionService.getApprovedDiscussions(pageNum, 10);
            pageInfo = new PageInfo<>(list);
        } else if (status == 2) {
            List<AppDiscussion> list = appDiscussionService.getRejectedDiscussions(pageNum, 10);
            pageInfo = new PageInfo<>(list);
        } else {
            List<AppDiscussion> list = appDiscussionService.getAllDiscussions(pageNum, 10);
            pageInfo = new PageInfo<>(list);
        }
        
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("status", status);
        return "admin/discussions";
    }

    @GetMapping("/{id}")
    public String discussion(@PathVariable Long id, Model model) {
        AppDiscussion discussion = appDiscussionService.getDiscussionById(id);
        List<AppDiscussionComment> comments = appDiscussionCommentService.getCommentsByDiscussionId(id);
        model.addAttribute("discussion", discussion);
        model.addAttribute("comments", comments);
        return "admin/discussion-detail";
    }

    @PostMapping("/audit/{id}")
    public String audit(@PathVariable Long id,
                      @RequestParam Integer status,
                      @RequestParam(required = false) String auditRemark,
                      HttpSession session,
                      RedirectAttributes attributes) {
        User user = (User) session.getAttribute("user");
        boolean success = appDiscussionService.auditDiscussion(id, status, user.getId(), auditRemark);
        if (success) {
            attributes.addFlashAttribute("message", "审核成功");
        } else {
            attributes.addFlashAttribute("message", "审核失败");
        }
        return "redirect:/admin/discussions?status=0";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, 
                       @RequestParam(required = false) Integer status,
                       RedirectAttributes attributes) {
        boolean success = appDiscussionService.deleteDiscussion(id);
        if (success) {
            attributes.addFlashAttribute("message", "删除成功");
        } else {
            attributes.addFlashAttribute("message", "删除失败");
        }
        return "redirect:/admin/discussions?status=" + (status != null ? status : 9);
    }

    @PostMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable Long id,
                               @RequestParam Long discussionId,
                               RedirectAttributes attributes) {
        boolean success = appDiscussionCommentService.deleteComment(id);
        if (success) {
            attributes.addFlashAttribute("message", "删除评论成功");
        } else {
            attributes.addFlashAttribute("message", "删除评论失败");
        }
        return "redirect:/admin/discussions/" + discussionId;
    }
}