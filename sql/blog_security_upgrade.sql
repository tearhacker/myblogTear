-- 博客安全功能升级SQL脚本
-- 为t_blog表添加安全相关字段

-- 添加博文状态字段：1-普通公开，2-机密，3-绝密
ALTER TABLE `t_blog` ADD COLUMN `blog_status` int(1) NOT NULL DEFAULT 1 COMMENT '博文状态：1-普通公开，2-机密，3-绝密' AFTER `description`;

-- 添加访问密码字段，存储MD5加密后的密码
ALTER TABLE `t_blog` ADD COLUMN `access_password` varchar(32) NULL DEFAULT NULL COMMENT '访问密码(MD5加密)' AFTER `blog_status`;

-- 添加索引提高查询性能
CREATE INDEX `idx_blog_status` ON `t_blog` (`blog_status`);

-- 更新现有数据，将所有现有博文设置为普通公开状态
UPDATE `t_blog` SET `blog_status` = 1 WHERE `blog_status` IS NULL;

-- 显示表结构确认修改
DESCRIBE `t_blog`;
