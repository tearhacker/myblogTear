package com.star.controller.admin;

import com.star.dao.ApiSecretDao;
import com.star.entity.ApiSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description: API密钥后台管理控制器
 * @Author: 泪心
 * @Date: 2025/12/30
 */
@Controller
@RequestMapping("/admin/apikeys")
public class ApiSecretAdminController {

    private static final Logger log = LoggerFactory.getLogger(ApiSecretAdminController.class);

    @Autowired
    private ApiSecretDao apiSecretDao;

    /**
     * 密钥管理页面
     */
    @GetMapping
    public String index(Model model) {
        List<ApiSecret> list = apiSecretDao.findAll();
        model.addAttribute("secrets", list);
        return "admin/apikeys";
    }

    /**
     * 获取所有密钥(AJAX)
     */
    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> list() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ApiSecret> list = apiSecretDao.findAll();
            result.put("code", 0);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", -1);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 新增密钥
     */
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> add(
            @RequestParam("contact") String contact,
            @RequestParam(value = "version", defaultValue = "1.0.0") String version,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "days", defaultValue = "0") int days) {
        
        Map<String, Object> result = new HashMap<>();
        log.info("收到新增密钥请求: contact={}, version={}, days={}", contact, version, days);
        
        if (contact == null || contact.trim().isEmpty()) {
            result.put("code", -1);
            result.put("msg", "联系方式不能为空");
            return result;
        }

        String tearSecret = "TEAR-" + UUID.randomUUID().toString().toUpperCase().substring(0, 16);

        ApiSecret secret = new ApiSecret();
        secret.setTearSecret(tearSecret);
        secret.setStatus(0);
        secret.setContact(contact.trim());
        secret.setVersion(version);
        secret.setRemark(remark);
        secret.setAccessCount(0L);
        
        if (days > 0) {
            Date expireTime = new Date(System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000);
            secret.setExpireTime(expireTime);
        }

        try {
            int rows = apiSecretDao.insertSecret(secret);
            log.info("插入密钥结果: rows={}, id={}, tearSecret={}", rows, secret.getId(), tearSecret);
            result.put("code", 0);
            result.put("msg", "创建成功");
            result.put("tearSecret", tearSecret);
        } catch (Exception e) {
            log.error("创建密钥失败", e);
            result.put("code", -1);
            result.put("msg", "创建失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新密钥状态(启用/禁用)
     */
    @PostMapping("/status")
    @ResponseBody
    public Map<String, Object> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        Map<String, Object> result = new HashMap<>();
        try {
            apiSecretDao.updateStatus(id, status);
            result.put("code", 0);
            result.put("msg", status == 0 ? "已启用" : "已禁用");
        } catch (Exception e) {
            result.put("code", -1);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 删除密钥
     */
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            apiSecretDao.deleteById(id);
            result.put("code", 0);
            result.put("msg", "删除成功");
        } catch (Exception e) {
            result.put("code", -1);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
