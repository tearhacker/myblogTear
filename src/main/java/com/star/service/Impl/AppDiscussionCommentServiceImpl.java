package com.star.service.Impl;

import com.star.dao.AppDiscussionCommentDao;
import com.star.dao.AppDiscussionDao;
import com.star.entity.AppDiscussionComment;
import com.star.service.AppDiscussionCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppDiscussionCommentServiceImpl implements AppDiscussionCommentService {

    @Autowired
    private AppDiscussionCommentDao appDiscussionCommentDao;

    @Autowired
    private AppDiscussionDao appDiscussionDao;

    @Override
    @Transactional
    public AppDiscussionComment createComment(Long discussionId, Long userId, String qqNumber, String nickname, String avatar, String content, Long parentCommentId) {
        AppDiscussionComment comment = new AppDiscussionComment();
        comment.setDiscussionId(discussionId);
        comment.setUserId(userId);
        comment.setQqNumber(qqNumber);
        comment.setNickname(nickname);
        comment.setAvatar(avatar);
        comment.setContent(content);
        comment.setParentCommentId(parentCommentId);
        comment.setLikeCount(0);
        
        appDiscussionCommentDao.insert(comment);
        appDiscussionDao.incrementCommentCount(discussionId);
        return comment;
    }

    @Override
    public AppDiscussionComment getCommentById(Long id) {
        return appDiscussionCommentDao.findById(id);
    }

    @Override
    public List<AppDiscussionComment> getCommentsByDiscussionId(Long discussionId) {
        return appDiscussionCommentDao.findByDiscussionId(discussionId);
    }

    @Override
    public List<AppDiscussionComment> getRepliesByParentCommentId(Long parentCommentId) {
        return appDiscussionCommentDao.findReplies(parentCommentId);
    }

    @Override
    public List<AppDiscussionComment> getUserComments(Long userId) {
        return appDiscussionCommentDao.findByUserId(userId);
    }

    @Override
    @Transactional
    public boolean likeComment(Long id) {
        int result = appDiscussionCommentDao.incrementLikeCount(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id) {
        AppDiscussionComment comment = appDiscussionCommentDao.findById(id);
        if (comment == null) {
            return false;
        }
        int result = appDiscussionCommentDao.delete(id);
        if (result > 0) {
            appDiscussionDao.incrementCommentCount(comment.getDiscussionId());
        }
        return result > 0;
    }

    @Override
    public int countByDiscussionId(Long discussionId) {
        return appDiscussionCommentDao.countByDiscussionId(discussionId);
    }
}