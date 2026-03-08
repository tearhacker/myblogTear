package com.star.queryvo;

import java.util.Date;

/**
 * @Description: 编辑修改文章实体类
 * @Date: Created in 15:55 2020/6/6
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class ShowBlog {

    private Long id;
    private String flag;
    private String title;
    private String content;
    private Long typeId;
    private String firstPicture;
    private Long firstPictureUploadId;  // 首图上传文件ID
    private String firstPictureType;     // 首图类型：link-链接，upload-上传
    private String description;
    private Integer blogStatus;        // 博文状态：1-普通公开，2-机密，3-绝密
    private String accessPassword;     // 访问密码(MD5加密)
    private boolean recommend;
    private boolean published;
    private boolean shareStatement;
    private boolean appreciation;
    private boolean commentabled;
    private Date updateTime;

    public ShowBlog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public Long getFirstPictureUploadId() {
        return firstPictureUploadId;
    }

    public void setFirstPictureUploadId(Long firstPictureUploadId) {
        this.firstPictureUploadId = firstPictureUploadId;
    }

    public String getFirstPictureType() {
        return firstPictureType;
    }

    public void setFirstPictureType(String firstPictureType) {
        this.firstPictureType = firstPictureType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBlogStatus() {
        return blogStatus;
    }

    public void setBlogStatus(Integer blogStatus) {
        this.blogStatus = blogStatus;
    }

    public String getAccessPassword() {
        return accessPassword;
    }

    public void setAccessPassword(String accessPassword) {
        this.accessPassword = accessPassword;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isShareStatement() {
        return shareStatement;
    }

    public void setShareStatement(boolean shareStatement) {
        this.shareStatement = shareStatement;
    }

    public boolean isAppreciation() {
        return appreciation;
    }

    public void setAppreciation(boolean appreciation) {
        this.appreciation = appreciation;
    }

    public boolean isCommentabled() {
        return commentabled;
    }

    public void setCommentabled(boolean commentabled) {
        this.commentabled = commentabled;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ShowBlog{" +
                "id=" + id +
                ", flag='" + flag + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", typeId=" + typeId +
                ", firstPicture='" + firstPicture + '\'' +
                ", firstPictureUploadId=" + firstPictureUploadId +
                ", firstPictureType='" + firstPictureType + '\'' +
                ", description='" + description + '\'' +
                ", blogStatus=" + blogStatus +
                ", accessPassword='" + accessPassword + '\'' +
                ", recommend=" + recommend +
                ", published=" + published +
                ", shareStatement=" + shareStatement +
                ", appreciation=" + appreciation +
                ", commentabled=" + commentabled +
                ", updateTime=" + updateTime +
                '}';
    }
}