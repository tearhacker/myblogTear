package com.star.service;

import java.util.Map;

/**
 * @Description: API密钥服务接口
 * @Author: 泪心
 * @Date: 2025/12/30
 */
public interface ApiSecretService {

    /**
     * 验证API请求
     * @param tearSecret 密钥
     * @param timestamp 时间戳(秒)
     * @param sign 签名 = MD5(tearSecret + timestamp)
     * @return 验证结果
     */
    Map<String, Object> verifyRequest(String tearSecret, long timestamp, String sign);
}
