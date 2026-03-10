package tear.conception.model;

import org.json.JSONObject;

public class Discussion {
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
    private String createTime;
    private String updateTime;
    private String auditTime;
    private Long auditUserId;
    private String auditRemark;

    public Discussion() {
    }

    public static Discussion fromJson(JSONObject json) {
        Discussion discussion = new Discussion();
        try {
            discussion.id = json.optLong("id", 0L);
            discussion.userId = json.optLong("userId", 0L);
            // 尝试两种可能的字段名
            discussion.qqNumber = json.optString("qqNumber", "");
            if (discussion.qqNumber.isEmpty()) {
                discussion.qqNumber = json.optString("qq_number", "");
            }
            discussion.nickname = json.optString("nickname", "");
            discussion.avatar = json.optString("avatar", "");
            discussion.title = json.optString("title", "");
            discussion.content = json.optString("content", "");
            discussion.status = json.optInt("status", 0);
            discussion.viewCount = json.optInt("viewCount", 0);
            discussion.commentCount = json.optInt("commentCount", 0);
            discussion.likeCount = json.optInt("likeCount", 0);
            discussion.createTime = json.optString("createTime", "");
            discussion.updateTime = json.optString("updateTime", "");
            discussion.auditTime = json.optString("auditTime", "");
            discussion.auditUserId = json.optLong("auditUserId", 0L);
            discussion.auditRemark = json.optString("auditRemark", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return discussion;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
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
}
