package com.star.dao;

import com.star.entity.AppDiscussion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppDiscussionDao {

    AppDiscussion findById(@Param("id") Long id);

    List<AppDiscussion> findByUserId(@Param("userId") Long userId);

    List<AppDiscussion> findByStatus(@Param("status") Integer status);

    List<AppDiscussion> findApprovedDiscussions(@Param("offset") int offset, @Param("limit") int limit);

    List<AppDiscussion> findPendingDiscussions(@Param("offset") int offset, @Param("limit") int limit);

    List<AppDiscussion> findRejectedDiscussions(@Param("offset") int offset, @Param("limit") int limit);

    List<AppDiscussion> findAllDiscussions(@Param("offset") int offset, @Param("limit") int limit);

    List<AppDiscussion> searchByKeyword(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

    int insert(AppDiscussion appDiscussion);

    int update(AppDiscussion appDiscussion);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("auditUserId") Long auditUserId, @Param("auditRemark") String auditRemark);

    int incrementViewCount(@Param("id") Long id);

    int incrementLikeCount(@Param("id") Long id);

    int incrementCommentCount(@Param("id") Long id);

    int delete(@Param("id") Long id);

    int countByStatus(@Param("status") Integer status);

    int countByUserId(@Param("userId") Long userId);
}