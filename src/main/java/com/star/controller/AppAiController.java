package com.star.controller;

import com.star.service.AiChatService;
import com.star.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: APP AI对话接口控制器
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
@RestController
@RequestMapping("/app/ai")
public class AppAiController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public Result chat(@RequestParam("message") String message) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }
            
            String response = aiChatService.chat(message.trim());
            
            Map<String, Object> result = new HashMap<>();
            result.put("response", response);
            
            return Result.success("成功", result);
        } catch (Exception e) {
            return Result.error("AI对话失败: " + e.getMessage());
        }
    }

    @GetMapping("/signin/message")
    public Result generateSigninMessage(@RequestParam("days") Integer days) {
        try {
            if (days == null || days < 1) {
                days = 1;
            }
            
            String message = aiChatService.generateSigninMessage(days);
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", message);
            result.put("days", days);
            
            return Result.success("成功", result);
        } catch (Exception e) {
            return Result.error("生成签到寄语失败: " + e.getMessage());
        }
    }

    @PostMapping("/love-letter")
    public Result generateLoveLetter(
            @RequestParam("targetName") String targetName,
            @RequestParam("days") Integer days,
            @RequestParam(value = "userWords", required = false) String userWords) {
        try {
            if (targetName == null || targetName.trim().isEmpty()) {
                targetName = "TA";
            }
            
            if (days == null || days < 1) {
                days = 1;
            }
            
            String prompt = buildLoveLetterPrompt(targetName, days, userWords);
            String letter = aiChatService.chat(prompt);
            
            Map<String, Object> result = new HashMap<>();
            result.put("letter", letter);
            result.put("targetName", targetName);
            result.put("days", days);
            
            return Result.success("成功", result);
        } catch (Exception e) {
            return Result.error("生成情书失败: " + e.getMessage());
        }
    }

    private String buildLoveLetterPrompt(String targetName, int days, String userWords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位浪漫的情书作家。请为用户写一封真挚动人的情书。\n\n");
        prompt.append("背景信息：\n");
        prompt.append("- 用户已经持续思念TA（").append(targetName).append("）").append(days).append("天\n");
        prompt.append("- 这是一段从初中开始的暗恋故事\n");
        if (userWords != null && !userWords.trim().isEmpty()) {
            prompt.append("- 用户想对TA说的话：").append(userWords).append("\n");
        }
        prompt.append("\n要求：\n");
        prompt.append("1. 情感真挚，语言优美\n");
        prompt.append("2. 字数200-300字\n");
        prompt.append("3. 不要太肉麻，要真诚\n");
        prompt.append("4. 结尾要有期待和祝福\n");
        
        return prompt.toString();
    }
}
