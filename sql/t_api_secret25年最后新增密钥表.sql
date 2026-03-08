/*
 API密钥管理表 - 泪心博客系统
 场景：给C++开发者对接，一个密钥授权一个开发者
 防护机制在代码层实现：时间戳校验、签名验证、内存级频率限制
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `t_api_secret`;
CREATE TABLE `t_api_secret` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tear_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1.0.0' COMMENT '版本号',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间(NULL永久有效)',
  `access_count` bigint NOT NULL DEFAULT 0 COMMENT '全网总启动次数',
  `today_count` bigint NOT NULL DEFAULT 0 COMMENT '今日启动次数',
  `today_date` date NULL DEFAULT NULL COMMENT '今日日期(用于重置today_count)',
  `last_access_time` datetime NULL DEFAULT NULL COMMENT '最后访问时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tear_secret`(`tear_secret`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API密钥管理表' ROW_FORMAT = DYNAMIC;

-- 测试数据
INSERT INTO `t_api_secret` (`tear_secret`, `status`, `contact`, `version`, `remark`) 
VALUES ('TEAR-2024-SECRET-DEMO-001', 0, '2254013571@qq.com', '1.0.0', '泪心测试密钥');

SET FOREIGN_KEY_CHECKS = 1;
