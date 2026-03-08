package com.star.entity;

import java.util.Date;

/**
 * @Description: 用户签到统计实体类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class AppUserStats {

    private Long id;
    private Long userId;
    private String qqNumber;
    private Integer totalSigninDays;
    private Integer continuousSigninDays;
    private Integer maxContinuousDays;
    private Date lastSigninDate;
    private Integer loveSigninDays;
    private Integer loveContinuousDays;
    private Date updateTime;

    public AppUserStats() {
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

    public Integer getTotalSigninDays() {
        return totalSigninDays;
    }

    public void setTotalSigninDays(Integer totalSigninDays) {
        this.totalSigninDays = totalSigninDays;
    }

    public Integer getContinuousSigninDays() {
        return continuousSigninDays;
    }

    public void setContinuousSigninDays(Integer continuousSigninDays) {
        this.continuousSigninDays = continuousSigninDays;
    }

    public Integer getMaxContinuousDays() {
        return maxContinuousDays;
    }

    public void setMaxContinuousDays(Integer maxContinuousDays) {
        this.maxContinuousDays = maxContinuousDays;
    }

    public Date getLastSigninDate() {
        return lastSigninDate;
    }

    public void setLastSigninDate(Date lastSigninDate) {
        this.lastSigninDate = lastSigninDate;
    }

    public Integer getLoveSigninDays() {
        return loveSigninDays;
    }

    public void setLoveSigninDays(Integer loveSigninDays) {
        this.loveSigninDays = loveSigninDays;
    }

    public Integer getLoveContinuousDays() {
        return loveContinuousDays;
    }

    public void setLoveContinuousDays(Integer loveContinuousDays) {
        this.loveContinuousDays = loveContinuousDays;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AppUserStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", qqNumber='" + qqNumber + '\'' +
                ", totalSigninDays=" + totalSigninDays +
                ", continuousSigninDays=" + continuousSigninDays +
                ", maxContinuousDays=" + maxContinuousDays +
                ", lastSigninDate=" + lastSigninDate +
                ", loveSigninDays=" + loveSigninDays +
                ", loveContinuousDays=" + loveContinuousDays +
                ", updateTime=" + updateTime +
                '}';
    }
}
