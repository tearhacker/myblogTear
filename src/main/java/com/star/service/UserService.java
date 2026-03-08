package com.star.service;

import com.star.entity.User;

/**
 * @Description: 用户业务层接口
 * @Date: Created in 22:56 2020/5/26
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface UserService {
    //核对用户名和密码
    User checkUser(String username, String password);
}