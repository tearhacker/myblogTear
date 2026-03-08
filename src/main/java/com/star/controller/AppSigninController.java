package com.star.controller;

import com.star.service.AppSigninService;
import com.star.vo.AppSigninVO;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: APP签到控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/signin")
public class AppSigninController {

    @Autowired
    private AppSigninService appSigninService;

    @PostMapping("/do")
    public Result doSignin(@RequestParam("userId") Long userId,
                           @RequestParam("qqNumber") String qqNumber) {
        AppSigninVO vo = appSigninService.signin(userId, qqNumber);
        return Result.success(vo);
    }

    @GetMapping("/today/{userId}")
    public Result getTodaySignin(@PathVariable Long userId) {
        AppSigninVO vo = appSigninService.getTodaySignin(userId);
        if (vo == null) {
            return Result.success(null);
        }
        return Result.success(vo);
    }

    @GetMapping("/stats/{userId}")
    public Result getSigninStats(@PathVariable Long userId) {
        AppSigninVO vo = appSigninService.getSigninStats(userId);
        return Result.success(vo);
    }

    @GetMapping("/check/{userId}")
    public Result checkTodaySignin(@PathVariable Long userId) {
        boolean signed = appSigninService.hasSignedInToday(userId);
        return Result.success(signed);
    }
}
