package com.star.entity;

import java.util.Date;

public class AppDiscussion {

    private Long id;
    private Long userId;
    private String qqNumber;
    private String nickname;
    private String avatar;
    private String title;
    private String content;
    private Integer status;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Date createTime;
    private Date updateTime;
    private Date auditTime;
    private Long auditUserId;
    private String auditRemark;

    public AppDiscussion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    @Override
    public String toString() {
        return "AppDiscussion{" +
                "id=" + id +
                ", userId=" + userId +
                ", qqNumber='" + qqNumber + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", viewCount=" + viewCount +
                ", commentCount=" + commentCount +
                ", likeCount=" + likeCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", auditTime=" + auditTime +
                ", auditUserId=" + auditUserId +
                ", auditRemark='" + auditRemark + '\'' +
                '}';
    }
}