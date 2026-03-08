package com.star.service;

import com.star.entity.AppUser;

/**
 * @Description: APP用户服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AppUserService {

    AppUser loginOrRegister(String qqNumber, String nickname);

    AppUser getUserById(Long id);

    AppUser getUserByQqNumber(String qqNumber);

    boolean updateNickname(Long id, String nickname);

    boolean updateAvatar(Long id, String avatar);

    boolean updateSignature(Long id, String signature);

    AppUser updateUserInfo(AppUser appUser);
}
