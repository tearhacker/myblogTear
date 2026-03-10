package com.star.dao;

import com.star.entity.AppDiscussionComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppDiscussionCommentDao {

    AppDiscussionComment findById(@Param("id") Long id);

    List<AppDiscussionComment> findByDiscussionId(@Param("discussionId") Long discussionId);

    List<AppDiscussionComment> findByUserId(@Param("userId") Long userId);

    List<AppDiscussionComment> findReplies(@Param("parentCommentId") Long parentCommentId);

    int insert(AppDiscussionComment comment);

    int update(AppDiscussionComment comment);

    int incrementLikeCount(@Param("id") Long id);

    int delete(@Param("id") Long id);

    int countByDiscussionId(@Param("discussionId") Long discussionId);
}