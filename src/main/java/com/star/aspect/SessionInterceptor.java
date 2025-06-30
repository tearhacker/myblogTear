package com.star.aspect;

import com.alibaba.fastjson.JSONObject;
import com.star.annotation.AccessLimit;
import com.star.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: SessionInterceptor
 * @Description: TODO
 * @Author ONESTAR
 * @Date: 2021/1/22 21:18
 * @微信：YXK-ONESTAR
 * @URL：https://onestar.newstar.net.cn/
 * @Version 1.0
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (null == accessLimit) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            if (needLogin) {
                //判断是否登录
            }
            String ip=request.getRemoteAddr();
            String key = request.getServletPath() + ":" + ip ;
            Integer count = (Integer) redisTemplate.opsForValue().get(key);

            if (null == count || -1 == count) {
                redisTemplate.opsForValue().set(key, 1,seconds, TimeUnit.SECONDS);
                return true;
            }

            if (count < maxCount) {
                count = count+1;
                redisTemplate.opsForValue().set(key, count,0);
                return true;
            }

            if (count >= maxCount) {
//                response 返回 json 请求过于频繁请稍后再试


                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                Response result = new Response<>();
                result.setCode("9999");
                result.setMsg("操作过于频繁,请稍后再提交");
                Object obj = JSONObject.toJSON(result);
                response.getWriter().write(JSONObject.toJSONString(obj));


                return false;
            }
        }

        return true;
    }
}


/*


# 接口限流拦截器技术解析

## 核心功能
这是一个基于Spring拦截器实现的**接口访问频率限制组件**，主要功能：
- 通过Redis实现分布式限流
- 基于注解的灵活配置
- 防止恶意高频请求

## 技术实现细节

### 1. 基础配置
```java
@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate redisTemplate;
    //...
}

技术亮点
分布式限流

基于Redis实现，适用于集群环境

通过IP+接口路径区分不同用户的访问

滑动窗口算法

每个时间窗口(seconds)内限制访问次数(maxCount)

计数器自动过期实现时间窗口滑动

优雅响应

返回标准JSON格式错误信息

统一错误码(9999)标识限流情况

低侵入性

通过注解方式配置，不影响业务代码

无注解接口自动放行

 */