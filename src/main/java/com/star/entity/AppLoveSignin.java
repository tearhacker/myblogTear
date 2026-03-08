package com.star.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @Description: 恋爱签到实体类(隐形功能 - 致欧阳颖)
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class AppLoveSignin {

    private Long id;
    private Long userId;
    private String qqNumber;
    private String targetName;
    private Date signinDate;
    private Timestamp signinTime;
    private Integer continuousDays;
    private Integer totalDays;
    private String loveMessage;
    private Integer loveLevel;
    private Integer aiGenerated;

    public AppLoveSignin() {
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

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Date getSigninDate() {
        return signinDate;
    }

    public void setSigninDate(Date signinDate) {
        this.signinDate = signinDate;
    }

    public Timestamp getSigninTime() {
        return signinTime;
    }

    public void setSigninTime(Timestamp signinTime) {
        this.signinTime = signinTime;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public String getLoveMessage() {
        return loveMessage;
    }

    public void setLoveMessage(String loveMessage) {
        this.loveMessage = loveMessage;
    }

    public Integer getLoveLevel() {
        return loveLevel;
    }

    public void setLoveLevel(Integer loveLevel) {
        this.loveLevel = loveLevel;
    }

    public Integer getAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(Integer aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    @Override
    public String toString() {
        return "AppLoveSignin{" +
                "id=" + id +
                ", userId=" + userId +
                ", qqNumber='" + qqNumber + '\'' +
                ", targetName='" + targetName + '\'' +
                ", signinDate=" + signinDate +
                ", signinTime=" + signinTime +
                ", continuousDays=" + continuousDays +
                ", totalDays=" + totalDays +
                ", loveMessage='" + loveMessage + '\'' +
                ", loveLevel=" + loveLevel +
                ", aiGenerated=" + aiGenerated +
                '}';
    }
}
