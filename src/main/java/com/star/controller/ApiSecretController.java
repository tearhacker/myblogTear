package com.star.controller;

import com.star.dao.ApiSecretDao;
import com.star.entity.ApiSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: API密钥验证接口 - 供C++ CURL调用
 * @Author: 泪心
 * @Date: 2025/12/30
 * 
 * C++端只需传密钥即可，所有安全校验后端处理
 */
@RestController
@RequestMapping("/api/secret")
public class ApiSecretController {

    @Autowired
    private ApiSecretDao apiSecretDao;

    // 内存级频率限制
    private static final ConcurrentHashMap<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> failCountMap = new ConcurrentHashMap<>();
    private static final long MIN_INTERVAL = 500; // 最小请求间隔(毫秒)
    private static final int MAX_FAIL_COUNT = 10; // 最大失败次数，超过则临时封禁

    /**
     * 简单验证接口 - C++只需传密钥
     * POST /api/secret/check
     * 参数: key (密钥)
     */
    @PostMapping("/check")
    public Map<String, Object> check(@RequestParam("key") String key, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String ip = getClientIp(request);

        // 1. 参数校验
        if (key == null || key.trim().isEmpty()) {
            return fail(result, -1, "密钥不能为空");
        }

        // 2. IP失败次数检查(防爆破)
        Integer failCount = failCountMap.getOrDefault(ip, 0);
        if (failCount >= MAX_FAIL_COUNT) {
            return fail(result, -2, "请求过于频繁，请稍后再试");
        }

        // 3. 频率限制
        String limitKey = ip + "_" + key;
        Long lastTime = rateLimitMap.get(limitKey);
        long now = System.currentTimeMillis();
        if (lastTime != null && (now - lastTime) < MIN_INTERVAL) {
            return fail(result, -3, "请求过于频繁");
        }
        rateLimitMap.put(limitKey, now);

        // 4. 查询密钥
        ApiSecret secret = apiSecretDao.findByTearSecret(key.trim());
        if (secret == null) {
            failCountMap.put(ip, failCount + 1);
            return fail(result, -4, "密钥无效");
        }

        // 5. 状态校验
        if (secret.getStatus() != 0) {
            return fail(result, -5, "密钥已禁用");
        }

        // 6. 过期校验
        if (secret.getExpireTime() != null && secret.getExpireTime().before(new Date())) {
            return fail(result, -6, "密钥已过期");
        }

        // 7. 验证成功，清除失败计数，更新访问信息
        failCountMap.remove(ip);
        apiSecretDao.updateAccessInfo(secret.getId());

        // 8. 返回成功及授权信息
        result.put("code", 0);
        result.put("msg", "ok");
        result.put("data", buildData(secret));
        return result;
    }

    /**
     * GET方式验证 - 更简单
     * GET /api/secret/v?k=密钥
     */
    @GetMapping("/v")
    public Map<String, Object> verify(@RequestParam("k") String key, HttpServletRequest request) {
        return check(key, request);
    }

    /**
     * 健康检查
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "pong");
        result.put("ts", System.currentTimeMillis() / 1000);
        return result;
    }

    /**
     * 隐蔽的新增密钥接口
     * POST /TearGame/new_create
     * 参数: contact(联系方式), version(版本号,可选), remark(备注,可选), days(有效天数,可选,0或不传为永久)
     */
    @PostMapping("/TearGame/new_create")
    public Map<String, Object> createSecret(
            @RequestParam("contact") String contact,
            @RequestParam(value = "version", defaultValue = "1.0.0") String version,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "days", defaultValue = "0") int days,
            HttpServletRequest request) {
        
        Map<String, Object> result = new HashMap<>();

        // 1. 参数校验
        if (contact == null || contact.trim().isEmpty()) {
            return fail(result, -1, "联系方式不能为空");
        }

        // 2. 生成唯一密钥
        String tearSecret = "TEAR-" + UUID.randomUUID().toString().toUpperCase().substring(0, 16);

        // 4. 构建实体
        ApiSecret secret = new ApiSecret();
        secret.setTearSecret(tearSecret);
        secret.setStatus(0);
        secret.setContact(contact.trim());
        secret.setVersion(version);
        secret.setRemark(remark);
        secret.setAccessCount(0L);
        
        // 5. 设置过期时间
        if (days > 0) {
            Date expireTime = new Date(System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000);
            secret.setExpireTime(expireTime);
        }

        // 6. 入库
        try {
            apiSecretDao.insertSecret(secret);
        } catch (Exception e) {
            return fail(result, -3, "创建失败: " + e.getMessage());
        }

        // 7. 返回新密钥
        result.put("code", 0);
        result.put("msg", "创建成功");
        Map<String, Object> data = new HashMap<>();
        data.put("tearSecret", tearSecret);
        data.put("contact", contact);
        data.put("version", version);
        data.put("expireTime", secret.getExpireTime() != null ? secret.getExpireTime().getTime() / 1000 : 0);
        result.put("data", data);
        return result;
    }

    // ============ 私有方法 ============

    private Map<String, Object> fail(Map<String, Object> result, int code, String msg) {
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    private Map<String, Object> buildData(ApiSecret secret) {
        Map<String, Object> data = new HashMap<>();
        data.put("version", secret.getVersion());
        data.put("contact", secret.getContact());
        data.put("expireTime", secret.getExpireTime() != null ? secret.getExpireTime().getTime() / 1000 : 0);
        data.put("accessCount", secret.getAccessCount() != null ? secret.getAccessCount() : 0);
        data.put("todayCount", secret.getTodayCount() != null ? secret.getTodayCount() : 0);
        return data;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
