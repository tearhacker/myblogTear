package com.star.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.star.entity.AppAiConfig;
import com.star.service.AiChatService;
import com.star.service.AppAiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AppAiConfigService appAiConfigService;

    @Override
    public String chat(String userMessage) {
        AppAiConfig config = appAiConfigService.getEnabledConfig();
        if (config == null) {
            return "AI服务未启用";
        }
        
        return callAiApi(config, userMessage);
    }

    @Override
    public String chatWithTemplate(String templateKey, String... params) {
        AppAiConfig config = appAiConfigService.getEnabledConfig();
        if (config == null) {
            return "AI服务未启用";
        }
        
        String prompt = config.getPromptTemplate();
        if (prompt != null && params != null) {
            for (int i = 0; i < params.length; i++) {
                prompt = prompt.replace("{" + i + "}", params[i]);
            }
        }
        
        return callAiApi(config, prompt != null ? prompt : "请回复用户");
    }

    @Override
    public String generateSigninMessage(int days) {
        return chatWithTemplate("signin", String.valueOf(days));
    }

    @Override
    public String generateDailySigninMessage(int continuousDays, int totalDays) {
        String prompt = buildDailySigninPrompt(continuousDays, totalDays);
        return callAiApi(appAiConfigService.getEnabledConfig(), prompt);
    }

    private String buildDailySigninPrompt(int continuousDays, int totalDays) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位充满智慧和温暖的人生导师。请为用户生成一段每日签到寄语。\n\n");
        prompt.append("背景信息：\n");
        prompt.append("- 用户已连续签到：").append(continuousDays).append("天\n");
        prompt.append("- 用户累计签到：").append(totalDays).append("天\n");
        prompt.append("- 这是一个名为「一念」的签到应用，寓意一念放下、万般自在\n\n");
        prompt.append("核心主题：\n");
        prompt.append("1. 激励普通人坚持自己的梦想，不忘初心\n");
        prompt.append("2. 鼓励做回真实的自己，不再随波逐流\n");
        prompt.append("3. 人生总有逆境，度过了终会柳暗花明又一村\n");
        prompt.append("4. 从泪心到本心，找回最好的自己\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 寄语要励志、温暖、有深度，能给人力量\n");
        prompt.append("2. 结合签到天数，给予鼓励和祝福\n");
        prompt.append("3. 字数控制在30-60字之间\n");
        prompt.append("4. 语言真诚自然，不要说教\n");
        prompt.append("5. 可以适当引用诗词或名言\n");
        prompt.append("6. 每次生成的内容要有变化，不要重复\n");
        prompt.append("7. 直接输出寄语内容，不要加引号或其他符号\n");
        
        return prompt.toString();
    }

    @Override
    public String generateLoveSigninMessage(String targetName, int continuousDays, int totalDays) {
        String prompt = buildLoveSigninPrompt(targetName, continuousDays, totalDays);
        return callAiApi(appAiConfigService.getEnabledConfig(), prompt);
    }

    private String buildLoveSigninPrompt(String targetName, int continuousDays, int totalDays) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位浪漫的恋爱心理专家和诗人。请为用户生成一段恋爱签到寄语。\n\n");
        prompt.append("背景信息：\n");
        prompt.append("- 用户暗恋的对象：").append(targetName).append("\n");
        prompt.append("- 已连续签到：").append(continuousDays).append("天\n");
        prompt.append("- 累计签到：").append(totalDays).append("天\n");
        prompt.append("- 这是一个从内心的暗恋表达，用户坚持每天签到记录思念\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 寄语要温馨、浪漫、有深度，能触动人心\n");
        prompt.append("2. 结合签到天数，给予鼓励和祝福\n");
        prompt.append("3. 字数控制在30-60字之间\n");
        prompt.append("4. 不要太肉麻，要真诚自然\n");
        prompt.append("5. 可以适当引用诗词或名言\n");
        prompt.append("6. 每次生成的内容要有变化，不要重复\n");
        prompt.append("7. 直接输出寄语内容，不要加引号或其他符号\n");
        
        return prompt.toString();
    }

    private String callAiApi(AppAiConfig config, String userMessage) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(config.getApiUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(120000);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", config.getModelName());
            
            JSONArray messages = new JSONArray();
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            requestBody.put("messages", messages);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.toJSONString().getBytes(StandardCharsets.UTF_8));
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = JSONObject.parseObject(response.toString());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    if (choice != null) {
                        JSONObject message = choice.getJSONObject("message");
                        if (message != null) {
                            String content = message.getString("content");
                            if (content != null && !content.trim().isEmpty()) {
                                return content;
                            }
                        }
                    }
                }
                return null;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
