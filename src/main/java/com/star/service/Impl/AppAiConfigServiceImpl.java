package com.star.service.Impl;

import com.star.dao.AppAiConfigDao;
import com.star.entity.AppAiConfig;
import com.star.service.AppAiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: APP AI配置服务实现类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Service
public class AppAiConfigServiceImpl implements AppAiConfigService {

    @Autowired
    private AppAiConfigDao appAiConfigDao;

    @Override
    public AppAiConfig findById(Long id) {
        return appAiConfigDao.findById(id);
    }

    @Override
    public AppAiConfig getEnabledConfig() {
        return appAiConfigDao.findEnabled();
    }

    @Override
    public List<AppAiConfig> findAll() {
        return appAiConfigDao.findAll();
    }
}
