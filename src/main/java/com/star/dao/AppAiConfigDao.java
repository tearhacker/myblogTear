package com.star.dao;

import com.star.entity.AppAiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: APP AI配置DAO接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Mapper
public interface AppAiConfigDao {

    @Select("SELECT * FROM app_ai_config WHERE id = #{id}")
    AppAiConfig findById(Long id);

    @Select("SELECT * FROM app_ai_config WHERE is_enabled = 1")
    AppAiConfig findEnabled();

    @Select("SELECT * FROM app_ai_config")
    java.util.List<AppAiConfig> findAll();
}
