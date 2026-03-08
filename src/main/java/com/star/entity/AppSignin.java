package com.star.entity;

import java.util.Date;

/**
 * @Description: 一念签到实体类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class AppSignin {

    private Long id;
    private Long userId;
    private String qqNumber;
    private Date signinDate;
    private Date signinTime;
    private Integer continuousDays;
    private Integer totalDays;
    private String signinMessage;
    private Integer mood;
    private Integer aiGenerated;

    public AppSignin() {
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

    public Date getSigninDate() {
        return signinDate;
    }

    public void setSigninDate(Date signinDate) {
        this.signinDate = signinDate;
    }

    public Date getSigninTime() {
        return signinTime;
    }

    public void setSigninTime(Date signinTime) {
        this.signinTime = signinTime;
    }

    public Integer getContinuousDays() {
        return continuousDays;
    }

    public void setContinuousDays(Integer continuousDays) {
        this.continuousDays = continuousDays;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public String getSigninMessage() {
        return signinMessage;
    }

    public void setSigninMessage(String signinMessage) {
        this.signinMessage = signinMessage;
    }

    public Integer getMood() {
        return mood;
    }

    public void setMood(Integer mood) {
        this.mood = mood;
    }

    public Integer getAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(Integer aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    @Override
    public String toString() {
        return "AppSignin{" +
                "id=" + id +
                ", userId=" + userId +
                ", qqNumber='" + qqNumber + '\'' +
                ", signinDate=" + signinDate +
                ", signinTime=" + signinTime +
                ", continuousDays=" + continuousDays +
                ", totalDays=" + totalDays +
                ", signinMessage='" + signinMessage + '\'' +
                ", mood=" + mood +
                ", aiGenerated=" + aiGenerated +
                '}';
    }
}
