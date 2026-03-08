package com.star.dao;

import com.star.entity.AppLoveSignin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 恋爱签到DAO接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppLoveSigninDao {

    AppLoveSignin findByUserIdAndDate(@Param("userId") Long userId, @Param("signinDate") Date signinDate);

    AppLoveSignin findLatestByUserId(@Param("userId") Long userId);

    List<AppLoveSignin> findByUserId(@Param("userId") Long userId);

    int insert(AppLoveSignin appLoveSignin);

    int update(AppLoveSignin appLoveSignin);

    int countByUserId(@Param("userId") Long userId);
}
