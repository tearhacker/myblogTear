package com.star.service;

import com.star.entity.AppVersion;

/**
 * @Description: APP版本服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AppVersionService {

    /**
     * 获取最新版本信息
     */
    AppVersion getLatestVersion();

    /**
     * 检查版本更新
     * @param currentVersionCode 当前版本号
     * @return 版本信息，如果已是最新则返回null
     */
    AppVersion checkUpdate(Integer currentVersionCode);
}
