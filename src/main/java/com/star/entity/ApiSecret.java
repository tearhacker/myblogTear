package com.star.entity;

import java.util.Date;

/**
 * @Description: API密钥实体类
 * @Author: 泪心
 * @Date: 2025/12/30
 */
public class ApiSecret {

    private Long id;
    private String tearSecret;      // API密钥
    private Integer status;         // 状态：0-启用，1-禁用
    private String contact;         // 联系方式
    private String version;         // 版本号
    private Date expireTime;        // 过期时间
    private Long accessCount;       // 全网总启动次数
    private Long todayCount;        // 今日启动次数
    private Date todayDate;         // 今日日期
    private Date lastAccessTime;    // 最后访问时间
    private String remark;          // 备注
    private Date createTime;
    private Date updateTime;

    public ApiSecret() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTearSecret() { return tearSecret; }
    public void setTearSecret(String tearSecret) { this.tearSecret = tearSecret; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public Long getAccessCount() { return accessCount; }
    public void setAccessCount(Long accessCount) { this.accessCount = accessCount; }

    public Long getTodayCount() { return todayCount; }
    public void setTodayCount(Long todayCount) { this.todayCount = todayCount; }

    public Date getTodayDate() { return todayDate; }
    public void setTodayDate(Date todayDate) { this.todayDate = todayDate; }

    public Date getLastAccessTime() { return lastAccessTime; }
    public void setLastAccessTime(Date lastAccessTime) { this.lastAccessTime = lastAccessTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
