package com.star.dao;

import com.star.entity.AppUserStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 用户签到统计DAO接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppUserStatsDao {

    AppUserStats findByUserId(@Param("userId") Long userId);

    AppUserStats findByQqNumber(@Param("qqNumber") String qqNumber);

    int insert(AppUserStats appUserStats);

    int update(AppUserStats appUserStats);

    int incrementSigninDays(@Param("userId") Long userId);

    int incrementLoveSigninDays(@Param("userId") Long userId);
}
