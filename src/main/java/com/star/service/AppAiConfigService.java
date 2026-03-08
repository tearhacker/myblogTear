package com.star.service;

import com.star.entity.AppAiConfig;

/**
 * @Description: APP AI配置服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AppAiConfigService {

    AppAiConfig findById(Long id);

    AppAiConfig getEnabledConfig();

    java.util.List<AppAiConfig> findAll();
}
