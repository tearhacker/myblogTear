package com.star.tool;

import com.star.util.MD5Utils;

import java.util.Scanner;

/**
 * 本地小工具：把用户输入的明文密码生成项目一致的 MD5（用于改库/测试登录）。
 *
 * 用法：
 * - 方式1（推荐）：直接传参
 *   java com.star.tool.PasswordHashTool yourPassword
 * 2a402dd1f1d3ac729528eabdfd2650d0
 * - 方式2：不传参会进入交互输入
 */
public class PasswordHashTool {

    public static void main(String[] args) {
        String password;

        if (args != null && args.length > 0) {
            password = args[0];
        } else {
            System.out.print("请输入明文密码：");
            Scanner scanner = new Scanner(System.in);
            password = scanner.nextLine();
        }

        String md5 = MD5Utils.code(password);
        System.out.println("MD5(32位小写) = " + md5);
    }
}


