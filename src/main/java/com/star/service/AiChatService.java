package com.star.service;

/**
 * @Description: AI对话服务接口
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
public interface AiChatService {

    String chat(String userMessage);

    String chatWithTemplate(String templateKey, String... params);

    String generateSigninMessage(int days);
}
