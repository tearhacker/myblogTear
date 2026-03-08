package com.star.service;

import com.star.entity.AppSignin;
import com.star.vo.AppSigninVO;

import java.util.List;

/**
 * @Description: 一念签到服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AppSigninService {

    AppSigninVO signin(Long userId, String qqNumber);

    AppSigninVO getTodaySignin(Long userId);

    AppSigninVO getSigninStats(Long userId);

    List<AppSignin> getSigninHistory(Long userId);

    boolean hasSignedInToday(Long userId);

    String generateSigninMessage(int continuousDays, boolean useAI);
}
