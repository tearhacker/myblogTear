package com.star.service;

import com.star.entity.AppLoveSignin;

import java.util.List;

/**
 * @Description: 恋爱签到服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AppLoveSigninService {

    AppLoveSignin signin(Long userId, String qqNumber, String targetName);

    AppLoveSignin getTodaySignin(Long userId);

    List<AppLoveSignin> getLoveSigninHistory(Long userId);

    boolean hasSignedInToday(Long userId);

    String generateLoveMessage(int continuousDays, boolean useAI);
}
