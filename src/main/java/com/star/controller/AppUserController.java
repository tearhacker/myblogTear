package com.star.controller;

import com.star.entity.AppUser;
import com.star.service.AppUserService;
import com.star.vo.AppUserVO;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: APP用户控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/user")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/login")
    public Result login(@RequestParam("qqNumber") String qqNumber,
                        @RequestParam(value = "nickname", required = false) String nickname) {
        AppUser user = appUserService.loginOrRegister(qqNumber, nickname);
        
        AppUserVO vo = new AppUserVO();
        vo.setId(user.getId());
        vo.setQqNumber(user.getQqNumber());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSignature(user.getSignature());
        vo.setGender(user.getGender());
        
        return Result.success(vo);
    }

    @GetMapping("/info/{id}")
    public Result getUserInfo(@PathVariable Long id) {
        AppUser user = appUserService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        AppUserVO vo = new AppUserVO();
        vo.setId(user.getId());
        vo.setQqNumber(user.getQqNumber());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setSignature(user.getSignature());
        vo.setGender(user.getGender());
        
        return Result.success(vo);
    }

    @PostMapping("/update/nickname")
    public Result updateNickname(@RequestParam("id") Long id,
                                 @RequestParam("nickname") String nickname) {
        boolean success = appUserService.updateNickname(id, nickname);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @PostMapping("/update/avatar")
    public Result updateAvatar(@RequestParam("id") Long id,
                               @RequestParam("avatar") String avatar) {
        boolean success = appUserService.updateAvatar(id, avatar);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    @PostMapping("/update/signature")
    public Result updateSignature(@RequestParam("id") Long id,
                                  @RequestParam("signature") String signature) {
        boolean success = appUserService.updateSignature(id, signature);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }
}
