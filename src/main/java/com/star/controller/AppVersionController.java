package com.star.controller;

import com.star.entity.AppVersion;
import com.star.service.AppVersionService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: APP版本控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/version")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    /**
     * 检查版本更新
     * @param currentVersionCode 当前版本号
     */
    @GetMapping("/check")
    public Result checkUpdate(@RequestParam("versionCode") Integer currentVersionCode) {
        AppVersion version = appVersionService.checkUpdate(currentVersionCode);
        if (version == null) {
            return Result.success(null);
        }
        return Result.success(version);
    }

    /**
     * 获取最新版本信息
     */
    @GetMapping("/latest")
    public Result getLatestVersion() {
        AppVersion version = appVersionService.getLatestVersion();
        return Result.success(version);
    }
}
