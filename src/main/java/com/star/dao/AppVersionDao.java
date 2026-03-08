package com.star.dao;

import com.star.entity.AppVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: APP版本Dao
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppVersionDao {

    @Select("SELECT * FROM app_version WHERE is_latest = 1 ORDER BY version_code DESC LIMIT 1")
    AppVersion getLatestVersion();

    @Select("SELECT * FROM app_version WHERE version_code = #{versionCode}")
    AppVersion getByVersionCode(Integer versionCode);
}
