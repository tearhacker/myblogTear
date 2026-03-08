package com.star.vo;

/**
 * @Description: APP用户登录返回VO
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public class AppUserVO {

    private Long id;
    private String qqNumber;
    private String nickname;
    private String avatar;
    private String signature;
    private Integer gender;
    private Integer totalSigninDays;
    private Integer continuousSigninDays;

    public AppUserVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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
}
