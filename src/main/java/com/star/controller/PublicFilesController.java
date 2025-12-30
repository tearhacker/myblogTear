package com.star.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 前台文件管理页面控制器
 * @Date: Created in 2025/09/23
 * @Author: ONESTAR
 * @QQ群: 530311074
 * @URL: https://onestar.newstar.net.cn/
 */
@Controller
public class PublicFilesController {

    @GetMapping("/public_files")
    public String publicFiles() {
        return "public_files";
    }

}