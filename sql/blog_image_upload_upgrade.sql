-- 博客图片上传功能升级SQL脚本
-- 为t_blog表添加图片上传相关字段

-- 添加图片上传ID字段，关联到t_file_upload表
ALTER TABLE `t_blog` ADD COLUMN `first_picture_upload_id` bigint NULL DEFAULT NULL COMMENT '首图上传文件ID' AFTER `first_picture`;

-- 添加图片类型字段，区分是链接还是上传的图片
ALTER TABLE `t_blog` ADD COLUMN `first_picture_type` varchar(20) NULL DEFAULT 'link' COMMENT '首图类型：link-链接，upload-上传' AFTER `first_picture_upload_id`;

-- 添加外键约束（可选，如果需要严格约束）
-- ALTER TABLE `t_blog` ADD CONSTRAINT `fk_blog_first_picture_upload` FOREIGN KEY (`first_picture_upload_id`) REFERENCES `t_file_upload` (`id`) ON DELETE SET NULL;

-- 添加索引提高查询性能
CREATE INDEX `idx_blog_first_picture_upload_id` ON `t_blog` (`first_picture_upload_id`);
CREATE INDEX `idx_blog_first_picture_type` ON `t_blog` (`first_picture_type`);

-- 更新现有数据，将现有的first_picture设置为link类型
UPDATE `t_blog` SET `first_picture_type` = 'link' WHERE `first_picture` IS NOT NULL AND `first_picture` != '';

-- 显示表结构确认修改
DESCRIBE `t_blog`;
