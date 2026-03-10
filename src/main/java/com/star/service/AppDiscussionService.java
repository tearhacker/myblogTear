package com.star.service;

import com.star.entity.AppDiscussion;

import java.util.List;

public interface AppDiscussionService {

    AppDiscussion createDiscussion(Long userId, String qqNumber, String nickname, String avatar, String title, String content);

    AppDiscussion getDiscussionById(Long id);

    List<AppDiscussion> getUserDiscussions(Long userId);

    List<AppDiscussion> getApprovedDiscussions(int page, int pageSize);

    List<AppDiscussion> searchDiscussions(String keyword, int page, int pageSize);

    List<AppDiscussion> getPendingDiscussions(int page, int pageSize);

    List<AppDiscussion> getRejectedDiscussions(int page, int pageSize);

    List<AppDiscussion> getAllDiscussions(int page, int pageSize);

    boolean auditDiscussion(Long id, Integer status, Long auditUserId, String auditRemark);

    boolean updateDiscussion(Long id, String title, String content);

    boolean deleteDiscussion(Long id);

    boolean likeDiscussion(Long id);

    boolean viewDiscussion(Long id);

    int countByStatus(Integer status);

    int countByUserId(Long userId);
}