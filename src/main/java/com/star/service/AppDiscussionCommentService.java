package com.star.service;

import com.star.entity.AppDiscussionComment;

import java.util.List;

public interface AppDiscussionCommentService {

    AppDiscussionComment createComment(Long discussionId, Long userId, String qqNumber, String nickname, String avatar, String content, Long parentCommentId);

    AppDiscussionComment getCommentById(Long id);

    List<AppDiscussionComment> getCommentsByDiscussionId(Long discussionId);

    List<AppDiscussionComment> getRepliesByParentCommentId(Long parentCommentId);

    List<AppDiscussionComment> getUserComments(Long userId);

    boolean likeComment(Long id);

    boolean deleteComment(Long id);

    int countByDiscussionId(Long discussionId);
}