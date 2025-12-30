package com.star.dao;

import com.star.entity.ApiSecret;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: API密钥持久层接口
 * @Author: 泪心
 * @Date: 2025/12/30
 */
@Mapper
@Repository
public interface ApiSecretDao {

    // 根据密钥查询
    ApiSecret findByTearSecret(@Param("tearSecret") String tearSecret);

    // 查询所有
    List<ApiSecret> findAll();

    // 更新访问次数和时间
    int updateAccessInfo(@Param("id") Long id);

    // 新增密钥
    int insertSecret(ApiSecret apiSecret);

    // 更新密钥状态
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 删除密钥
    int deleteById(@Param("id") Long id);
}
