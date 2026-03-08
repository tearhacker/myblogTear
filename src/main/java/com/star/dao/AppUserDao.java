package com.star.dao;

import com.star.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: APP用户DAO接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppUserDao {

    AppUser findByQqNumber(@Param("qqNumber") String qqNumber);

    AppUser findById(@Param("id") Long id);

    int insert(AppUser appUser);

    int update(AppUser appUser);

    int updateLastLoginTime(@Param("id") Long id);

    int updateNickname(@Param("id") Long id, @Param("nickname") String nickname);

    int updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);

    int updateSignature(@Param("id") Long id, @Param("signature") String signature);
}
