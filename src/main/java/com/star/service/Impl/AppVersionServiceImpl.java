package com.star.service.Impl;

import com.star.dao.AppVersionDao;
import com.star.entity.AppVersion;
import com.star.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: APP版本服务实现类
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@Service
public class AppVersionServiceImpl implements AppVersionService {

    @Autowired
    private AppVersionDao appVersionDao;

    @Override
    public AppVersion getLatestVersion() {
        return appVersionDao.getLatestVersion();
    }

    @Override
    public AppVersion checkUpdate(Integer currentVersionCode) {
        AppVersion latestVersion = appVersionDao.getLatestVersion();
        if (latestVersion == null) {
            return null;
        }
        
        if (latestVersion.getVersionCode() > currentVersionCode) {
            return latestVersion;
        }
        
        return null;
    }
}
