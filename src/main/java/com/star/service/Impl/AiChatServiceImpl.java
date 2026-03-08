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

/**
 * @Description: AI对话服务实现类 - 火山云豆包API
 * @Author: 泪心
 * @QQ群: 435539500
 * @URL: https://github.com/tearhacker/
 */
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

    private String callAiApi(AppAiConfig config, String userMessage) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(config.getApiUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(60000);

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
                            return message.getString("content");
                        }
                    }
                }
                return "AI响应格式错误";
            } else {
                return "AI服务调用失败: " + responseCode;
            }
        } catch (Exception e) {
            return "AI服务异常: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
