package com.star.entity;

import java.util.Date;

public class AppDiscussionComment {

    private Long id;
    private Long discussionId;
    private Long userId;
    private String qqNumber;
    private String nickname;
    private String avatar;
    private String content;
    private Long parentCommentId;
    private Integer likeCount;
    private Date createTime;

    public AppDiscussionComment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Long discussionId) {
        this.discussionId = discussionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AppDiscussionComment{" +
                "id=" + id +
                ", discussionId=" + discussionId +
                ", userId=" + userId +
                ", qqNumber='" + qqNumber + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                ", parentCommentId=" + parentCommentId +
                ", likeCount=" + likeCount +
                ", createTime=" + createTime +
                '}';
    }
}