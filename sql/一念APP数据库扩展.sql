-- =====================================================
-- 一念APP 数据库表结构扩展
-- 作者：泪心
-- 说明：从泪心到本心的下山之作
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. APP用户表 (基于QQ号登录)
-- =====================================================
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `qq_number` varchar(20) NOT NULL COMMENT 'QQ号(唯一标识)',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `signature` varchar(255) DEFAULT NULL COMMENT '个性签名',
  `gender` tinyint DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint DEFAULT 1 COMMENT '状态 0禁用 1正常',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_qq_number` (`qq_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='APP用户表';

-- =====================================================
-- 2. 一念签到表 (核心签到 - 放过自己，成为自己)
-- =====================================================
DROP TABLE IF EXISTS `app_signin`;
CREATE TABLE `app_signin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) NOT NULL COMMENT 'QQ号',
  `signin_date` date NOT NULL COMMENT '签到日期',
  `signin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `continuous_days` int DEFAULT 1 COMMENT '连续签到天数',
  `total_days` int DEFAULT 1 COMMENT '累计签到天数',
  `signin_message` varchar(255) DEFAULT NULL COMMENT '签到寄语(大模型生成)',
  `mood` tinyint DEFAULT 0 COMMENT '心情 0平静 1开心 2难过 3焦虑',
  `ai_generated` tinyint DEFAULT 0 COMMENT '是否AI生成寄语 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `signin_date`),
  KEY `idx_qq_number` (`qq_number`),
  KEY `idx_signin_date` (`signin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='一念签到表';

-- =====================================================
-- 3. 恋爱签到表 (隐形功能 - 致欧阳颖)
-- =====================================================
DROP TABLE IF EXISTS `app_love_signin`;
CREATE TABLE `app_love_signin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) NOT NULL COMMENT 'QQ号',
  `target_name` varchar(50) DEFAULT '欧阳颖' COMMENT '暗恋对象名称',
  `signin_date` date NOT NULL COMMENT '签到日期',
  `signin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `continuous_days` int DEFAULT 1 COMMENT '连续签到天数',
  `total_days` int DEFAULT 1 COMMENT '累计签到天数',
  `love_message` varchar(500) DEFAULT NULL COMMENT '恋爱寄语(AI生成)',
  `love_level` int DEFAULT 1 COMMENT '恋爱等级 1-10',
  `ai_generated` tinyint DEFAULT 0 COMMENT '是否AI生成寄语',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `signin_date`),
  KEY `idx_qq_number` (`qq_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='恋爱签到表(隐形)';

-- =====================================================
-- 4. 大模型配置表 (签到寄语生成)
-- =====================================================
DROP TABLE IF EXISTS `app_ai_config`;
CREATE TABLE `app_ai_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(50) NOT NULL COMMENT '配置名称',
  `api_url` varchar(255) NOT NULL COMMENT 'API地址',
  `api_key` varchar(255) DEFAULT NULL COMMENT 'API密钥',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `prompt_template` text COMMENT '提示词模板',
  `is_enabled` tinyint DEFAULT 0 COMMENT '是否启用 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='大模型配置表';

-- =====================================================
-- 5. 签到寄语模板表 (备用模板)
-- =====================================================
DROP TABLE IF EXISTS `app_signin_message`;
CREATE TABLE `app_signin_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_type` tinyint DEFAULT 1 COMMENT '类型 1一念签到 2恋爱签到',
  `message_content` varchar(255) NOT NULL COMMENT '寄语内容',
  `mood_type` tinyint DEFAULT 0 COMMENT '适用心情',
  `is_active` tinyint DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到寄语模板表';

-- =====================================================
-- 6. 用户签到统计表 (缓存统计数据)
-- =====================================================
DROP TABLE IF EXISTS `app_user_stats`;
CREATE TABLE `app_user_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) NOT NULL COMMENT 'QQ号',
  `total_signin_days` int DEFAULT 0 COMMENT '累计签到天数',
  `continuous_signin_days` int DEFAULT 0 COMMENT '连续签到天数',
  `max_continuous_days` int DEFAULT 0 COMMENT '最大连续天数',
  `last_signin_date` date DEFAULT NULL COMMENT '最后签到日期',
  `love_signin_days` int DEFAULT 0 COMMENT '恋爱签到天数',
  `love_continuous_days` int DEFAULT 0 COMMENT '恋爱连续天数',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_qq_number` (`qq_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到统计表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 初始化大模型配置
INSERT INTO `app_ai_config` (`config_name`, `api_url`, `api_key`, `model_name`, `prompt_template`, `is_enabled`) 
VALUES ('默认大模型', 'https://api.example.com/v1/chat/completions', '', 'gpt-3.5-turbo', '请为一位坚持签到{days}天的用户生成一句温暖治愈的签到寄语，要求简短有深意，不超过50字。', 0);

-- 初始化一念签到寄语模板
INSERT INTO `app_signin_message` (`message_type`, `message_content`, `mood_type`) VALUES
(1, '一念放下，万般自在。今日的你，已比昨日更从容。', 0),
(1, '心若向阳，无惧悲伤。愿你今日温暖如初。', 1),
(1, '放下执念，回归本真。你正在找回最好的自己。', 0),
(1, '每一次签到，都是与自己的约定。坚持，就是力量。', 0),
(1, '人生路漫漫，愿你落子无悔，所愿皆所得。', 0),
(1, '不再沉沦苦海，今日的你，已踏上归途。', 2),
(1, '焦虑终将散去，平静正在归来。深呼吸，一切都会好。', 3);

-- 初始化恋爱签到寄语模板
INSERT INTO `app_signin_message` (`message_type`, `message_content`, `mood_type`) VALUES
(2, '暗恋是一场漫长的告白，每一天都在心里说爱你。', 0),
(2, '欧阳颖，这个名字，是我青春最美的秘密。', 1),
(2, '初中时的悸动，至今仍在心间。愿时光温柔以待。', 0),
(2, '有些话藏在心里，却用签到记录每一天的思念。', 0),
(2, '如果有一天能相见，我想告诉你：我一直都在。', 1);

SET FOREIGN_KEY_CHECKS = 1;
