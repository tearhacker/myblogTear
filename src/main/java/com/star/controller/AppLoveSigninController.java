package com.star.controller;

import com.star.entity.AppLoveSignin;
import com.star.service.AppLoveSigninService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: APP恋爱签到控制器(隐形功能)
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/love")
public class AppLoveSigninController {

    @Autowired
    private AppLoveSigninService appLoveSigninService;

    @PostMapping("/signin")
    public Result doLoveSignin(@RequestParam("userId") Long userId,
                               @RequestParam("qqNumber") String qqNumber,
                               @RequestParam(value = "targetName", required = false, defaultValue = "欧阳颖") String targetName) {
        AppLoveSignin signin = appLoveSigninService.signin(userId, qqNumber, targetName);
        return Result.success(signin);
    }

    @GetMapping("/today/{userId}")
    public Result getTodayLoveSignin(@PathVariable Long userId) {
        AppLoveSignin signin = appLoveSigninService.getTodaySignin(userId);
        return Result.success(signin);
    }

    @GetMapping("/history/{userId}")
    public Result getLoveSigninHistory(@PathVariable Long userId) {
        List<AppLoveSignin> list = appLoveSigninService.getLoveSigninHistory(userId);
        return Result.success(list);
    }

    @GetMapping("/check/{userId}")
    public Result checkTodayLoveSignin(@PathVariable Long userId) {
        boolean signed = appLoveSigninService.hasSignedInToday(userId);
        return Result.success(signed);
    }
}
