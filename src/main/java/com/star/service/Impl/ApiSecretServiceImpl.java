package com.star.service.Impl;

import com.star.dao.ApiSecretDao;
import com.star.entity.ApiSecret;
import com.star.service.ApiSecretService;
import com.star.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: API密钥服务实现
 * @Author: 泪心
 * @Date: 2025/12/30
 */
@Service
public class ApiSecretServiceImpl implements ApiSecretService {

    @Autowired
    private ApiSecretDao apiSecretDao;

    // 内存级频率限制：key=密钥, value=上次请求时间戳
    private static final ConcurrentHashMap<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    // 最小请求间隔(毫秒)
    private static final long MIN_INTERVAL = 100;
    // 时间戳有效范围(秒)，防重放
    private static final long TIMESTAMP_VALID_RANGE = 300;

    @Override
    public Map<String, Object> verifyRequest(String tearSecret, long timestamp, String sign) {
        Map<String, Object> result = new HashMap<>();

        // 1. 参数校验
        if (tearSecret == null || sign == null) {
            result.put("code", -1);
            result.put("msg", "参数缺失");
            return result;
        }

        // 2. 时间戳校验(防重放攻击)
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - timestamp) > TIMESTAMP_VALID_RANGE) {
            result.put("code", -2);
            result.put("msg", "请求已过期");
            return result;
        }

        // 3. 频率限制
        Long lastTime = rateLimitMap.get(tearSecret);
        long currentTime = System.currentTimeMillis();
        if (lastTime != null && (currentTime - lastTime) < MIN_INTERVAL) {
            result.put("code", -3);
            result.put("msg", "请求过于频繁");
            return result;
        }
        rateLimitMap.put(tearSecret, currentTime);

        // 4. 签名校验: sign = MD5(tearSecret + timestamp)
        String expectedSign = MD5Utils.code(tearSecret + timestamp);
        if (!expectedSign.equalsIgnoreCase(sign)) {
            result.put("code", -4);
            result.put("msg", "签名错误");
            return result;
        }

        // 5. 查询密钥
        ApiSecret apiSecret = apiSecretDao.findByTearSecret(tearSecret);
        if (apiSecret == null) {
            result.put("code", -5);
            result.put("msg", "密钥不存在");
            return result;
        }

        // 6. 状态校验
        if (apiSecret.getStatus() != 0) {
            result.put("code", -6);
            result.put("msg", "密钥已禁用");
            return result;
        }

        // 7. 过期校验
        if (apiSecret.getExpireTime() != null && apiSecret.getExpireTime().before(new Date())) {
            result.put("code", -7);
            result.put("msg", "密钥已过期");
            return result;
        }

        // 8. 更新访问信息
        apiSecretDao.updateAccessInfo(apiSecret.getId());

        // 9. 返回成功
        result.put("code", 0);
        result.put("msg", "验证成功");
        result.put("version", apiSecret.getVersion());
        result.put("contact", apiSecret.getContact());
        return result;
    }
}
