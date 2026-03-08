package com.star.dao;

import com.star.entity.AppSignin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 一念签到DAO接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppSigninDao {

    AppSignin findByUserIdAndDate(@Param("userId") Long userId, @Param("signinDate") Date signinDate);

    AppSignin findLatestByUserId(@Param("userId") Long userId);

    List<AppSignin> findByUserId(@Param("userId") Long userId);

    List<AppSignin> findByQqNumber(@Param("qqNumber") String qqNumber);

    int insert(AppSignin appSignin);

    int update(AppSignin appSignin);

    int countByUserId(@Param("userId") Long userId);

    int countContinuousDays(@Param("userId") Long userId);
}
