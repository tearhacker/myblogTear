package com.star.vo;

/**
 * @Description: APP签到返回VO
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class AppSigninVO {

    private Boolean success;
    private String message;
    private Integer continuousDays;
    private Integer totalDays;
    private Integer maxContinuousDays;
    private String signinMessage;
    private Boolean isMilestone;
    private Boolean alreadySigned;

    public AppSigninVO() {
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Integer getMaxContinuousDays() {
        return maxContinuousDays;
    }

    public void setMaxContinuousDays(Integer maxContinuousDays) {
        this.maxContinuousDays = maxContinuousDays;
    }

    public String getSigninMessage() {
        return signinMessage;
    }

    public void setSigninMessage(String signinMessage) {
        this.signinMessage = signinMessage;
    }

    public Boolean getIsMilestone() {
        return isMilestone;
    }

    public void setIsMilestone(Boolean isMilestone) {
        this.isMilestone = isMilestone;
    }

    public Boolean getAlreadySigned() {
        return alreadySigned;
    }

    public void setAlreadySigned(Boolean alreadySigned) {
        this.alreadySigned = alreadySigned;
    }
}
