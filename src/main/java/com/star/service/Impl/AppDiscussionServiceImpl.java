package com.star.service.Impl;

import com.star.dao.AppDiscussionDao;
import com.star.entity.AppDiscussion;
import com.star.service.AppDiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppDiscussionServiceImpl implements AppDiscussionService {

    @Autowired
    private AppDiscussionDao appDiscussionDao;

    @Override
    @Transactional
    public AppDiscussion createDiscussion(Long userId, String qqNumber, String nickname, String avatar, String title, String content) {
        AppDiscussion discussion = new AppDiscussion();
        discussion.setUserId(userId);
        discussion.setQqNumber(qqNumber);
        discussion.setNickname(nickname);
        discussion.setAvatar(avatar);
        discussion.setTitle(title);
        discussion.setContent(content);
        discussion.setStatus(0);
        discussion.setViewCount(0);
        discussion.setCommentCount(0);
        discussion.setLikeCount(0);
        
        appDiscussionDao.insert(discussion);
        return discussion;
    }

    @Override
    public AppDiscussion getDiscussionById(Long id) {
        return appDiscussionDao.findById(id);
    }

    @Override
    public List<AppDiscussion> getUserDiscussions(Long userId) {
        return appDiscussionDao.findByUserId(userId);
    }

    @Override
    public List<AppDiscussion> getApprovedDiscussions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return appDiscussionDao.findApprovedDiscussions(offset, pageSize);
    }

    @Override
    public List<AppDiscussion> searchDiscussions(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return appDiscussionDao.searchByKeyword(keyword, offset, pageSize);
    }

    @Override
    public List<AppDiscussion> getPendingDiscussions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return appDiscussionDao.findPendingDiscussions(offset, pageSize);
    }

    @Override
    public List<AppDiscussion> getRejectedDiscussions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return appDiscussionDao.findRejectedDiscussions(offset, pageSize);
    }

    @Override
    public List<AppDiscussion> getAllDiscussions(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return appDiscussionDao.findAllDiscussions(offset, pageSize);
    }

    @Override
    @Transactional
    public boolean auditDiscussion(Long id, Integer status, Long auditUserId, String auditRemark) {
        int result = appDiscussionDao.updateStatus(id, status, auditUserId, auditRemark);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateDiscussion(Long id, String title, String content) {
        AppDiscussion discussion = appDiscussionDao.findById(id);
        if (discussion == null) {
            return false;
        }
        discussion.setTitle(title);
        discussion.setContent(content);
        int result = appDiscussionDao.update(discussion);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteDiscussion(Long id) {
        int result = appDiscussionDao.delete(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean likeDiscussion(Long id) {
        int result = appDiscussionDao.incrementLikeCount(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean viewDiscussion(Long id) {
        int result = appDiscussionDao.incrementViewCount(id);
        return result > 0;
    }

    @Override
    public int countByStatus(Integer status) {
        return appDiscussionDao.countByStatus(status);
    }

    @Override
    public int countByUserId(Long userId) {
        return appDiscussionDao.countByUserId(userId);
    }
}