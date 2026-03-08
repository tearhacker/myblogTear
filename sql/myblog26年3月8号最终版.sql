/*
 Navicat Premium Data Transfer

 Source Server         : 笨鸡localhost
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3306
 Source Schema         : myblog

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 07/03/2026 19:06:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_ai_config
-- ----------------------------
DROP TABLE IF EXISTS `app_ai_config`;
CREATE TABLE `app_ai_config`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置名称',
  `api_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API地址',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API密钥',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模型名称',
  `prompt_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提示词模板',
  `is_enabled` tinyint(0) NULL DEFAULT 0 COMMENT '是否启用 0禁用 1启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '大模型配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_ai_config
-- ----------------------------
INSERT INTO `app_ai_config` VALUES (1, '默认大模型', 'https://api.example.com/v1/chat/completions', '', 'gpt-3.5-turbo', '请为一位坚持签到{days}天的用户生成一句温暖治愈的签到寄语，要求简短有深意，不超过50字。', 0, '2026-03-07 18:24:00', '2026-03-07 18:24:00');

-- ----------------------------
-- Table structure for app_love_signin
-- ----------------------------
DROP TABLE IF EXISTS `app_love_signin`;
CREATE TABLE `app_love_signin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'QQ号',
  `target_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '欧阳颖' COMMENT '暗恋对象名称',
  `signin_date` date NOT NULL COMMENT '签到日期',
  `signin_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `continuous_days` int(0) NULL DEFAULT 1 COMMENT '连续签到天数',
  `love_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '恋爱寄语(AI生成)',
  `love_level` int(0) NULL DEFAULT 1 COMMENT '恋爱等级 1-10',
  `ai_generated` tinyint(0) NULL DEFAULT 0 COMMENT '是否AI生成寄语',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id`, `signin_date`) USING BTREE,
  INDEX `idx_qq_number`(`qq_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '恋爱签到表(隐形)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for app_signin
-- ----------------------------
DROP TABLE IF EXISTS `app_signin`;
CREATE TABLE `app_signin`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'QQ号',
  `signin_date` date NOT NULL COMMENT '签到日期',
  `signin_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `continuous_days` int(0) NULL DEFAULT 1 COMMENT '连续签到天数',
  `total_days` int(0) NULL DEFAULT 1 COMMENT '累计签到天数',
  `signin_message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '签到寄语(大模型生成)',
  `mood` tinyint(0) NULL DEFAULT 0 COMMENT '心情 0平静 1开心 2难过 3焦虑',
  `ai_generated` tinyint(0) NULL DEFAULT 0 COMMENT '是否AI生成寄语 0否 1是',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id`, `signin_date`) USING BTREE,
  INDEX `idx_qq_number`(`qq_number`) USING BTREE,
  INDEX `idx_signin_date`(`signin_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '一念签到表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for app_signin_message
-- ----------------------------
DROP TABLE IF EXISTS `app_signin_message`;
CREATE TABLE `app_signin_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_type` tinyint(0) NULL DEFAULT 1 COMMENT '类型 1一念签到 2恋爱签到',
  `message_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '寄语内容',
  `mood_type` tinyint(0) NULL DEFAULT 0 COMMENT '适用心情',
  `is_active` tinyint(0) NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '签到寄语模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_signin_message
-- ----------------------------
INSERT INTO `app_signin_message` VALUES (1, 1, '一念放下，万般自在。今日的你，已比昨日更从容。', 0, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (2, 1, '心若向阳，无惧悲伤。愿你今日温暖如初。', 1, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (3, 1, '放下执念，回归本真。你正在找回最好的自己。', 0, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (4, 1, '每一次签到，都是与自己的约定。坚持，就是力量。', 0, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (5, 1, '人生路漫漫，愿你落子无悔，所愿皆所得。', 0, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (6, 1, '不再沉沦苦海，今日的你，已踏上归途。', 2, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (7, 1, '焦虑终将散去，平静正在归来。深呼吸，一切都会好。', 3, 1, '2026-03-07 18:24:05');
INSERT INTO `app_signin_message` VALUES (8, 2, '暗恋是一场漫长的告白，每一天都在心里说爱你。', 0, 1, '2026-03-07 18:24:09');
INSERT INTO `app_signin_message` VALUES (9, 2, '欧阳颖，这个名字，是我青春最美的秘密。', 1, 1, '2026-03-07 18:24:09');
INSERT INTO `app_signin_message` VALUES (10, 2, '初中时的悸动，至今仍在心间。愿时光温柔以待。', 0, 1, '2026-03-07 18:24:09');
INSERT INTO `app_signin_message` VALUES (11, 2, '有些话藏在心里，却用签到记录每一天的思念。', 0, 1, '2026-03-07 18:24:09');
INSERT INTO `app_signin_message` VALUES (12, 2, '如果有一天能相见，我想告诉你：我一直都在。', 1, 1, '2026-03-07 18:24:09');

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `qq_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'QQ号(唯一标识)',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '个性签名',
  `gender` tinyint(0) NULL DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_qq_number`(`qq_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'APP用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for app_user_stats
-- ----------------------------
DROP TABLE IF EXISTS `app_user_stats`;
CREATE TABLE `app_user_stats`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `qq_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'QQ号',
  `total_signin_days` int(0) NULL DEFAULT 0 COMMENT '累计签到天数',
  `continuous_signin_days` int(0) NULL DEFAULT 0 COMMENT '连续签到天数',
  `max_continuous_days` int(0) NULL DEFAULT 0 COMMENT '最大连续天数',
  `last_signin_date` date NULL DEFAULT NULL COMMENT '最后签到日期',
  `love_signin_days` int(0) NULL DEFAULT 0 COMMENT '恋爱签到天数',
  `love_continuous_days` int(0) NULL DEFAULT 0 COMMENT '恋爱连续天数',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE,
  INDEX `idx_qq_number`(`qq_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户签到统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint(0) NULL DEFAULT NULL
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for t_api_secret
-- ----------------------------
DROP TABLE IF EXISTS `t_api_secret`;
CREATE TABLE `t_api_secret`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tear_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'API密钥',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1.0.0' COMMENT '版本号',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '过期时间(NULL永久有效)',
  `access_count` bigint(0) NOT NULL DEFAULT 0 COMMENT '全网总启动次数',
  `today_count` bigint(0) NOT NULL DEFAULT 0 COMMENT '今日启动次数',
  `today_date` date NULL DEFAULT NULL COMMENT '今日日期(用于重置today_count)',
  `last_access_time` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tear_secret`(`tear_secret`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'API密钥管理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_api_secret
-- ----------------------------
INSERT INTO `t_api_secret` VALUES (2, 'GPointerNPoIDEALKinger', 0, '一念灰烬', '4.0', NULL, 22045, 459, '2026-03-07', '2026-03-07 14:31:06', '泪心2026无痕驱动免费对接版本', '2026-01-05 08:48:37', '2026-03-07 14:31:06');
INSERT INTO `t_api_secret` VALUES (3, 'TEAR-54208BCA-289F-44', 0, '永恒泪心', '4.0', NULL, 13506, 44, '2026-03-07', '2026-03-07 14:32:55', '泪心作者本人专用驱动调用密钥', '2026-01-05 18:01:30', '2026-03-07 14:32:55');
INSERT INTO `t_api_secret` VALUES (4, 'TEARGAME-UPXFD192-2026', 0, '泪心加密UPX新年版', '6.0.0', NULL, 217, 3, '2026-03-05', '2026-03-05 19:52:34', '泪心2026年1月13日开发UPX加壳项目', '2026-01-13 08:51:16', '2026-03-05 19:52:34');
INSERT INTO `t_api_secret` VALUES (5, 'TEAR-20B57E5A-FC43-41', 0, '亲爱的提子', '1.0.0', NULL, 0, 0, NULL, NULL, '1842401201', '2026-01-24 11:59:47', '2026-01-24 11:59:47');

-- ----------------------------
-- Table structure for t_blog
-- ----------------------------
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appreciation` bit(1) NOT NULL,
  `commentabled` bit(1) NOT NULL,
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `first_picture` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `first_picture_upload_id` bigint(0) NULL DEFAULT NULL COMMENT '首图上传文件ID',
  `first_picture_type` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT 'link' COMMENT '首图类型：link-链接，upload-上传',
  `flag` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `published` bit(1) NOT NULL,
  `recommend` bit(1) NOT NULL,
  `share_statement` bit(1) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `views` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type_id` bigint(0) NULL DEFAULT NULL,
  `user_id` bigint(0) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `blog_status` int(0) NOT NULL DEFAULT 1 COMMENT '博文状态：1-普通公开，2-机密，3-绝密',
  `access_password` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '访问密码(MD5加密)',
  `comment_count` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK292449gwg5yf7ocdlmswv9w4j`(`type_id`) USING BTREE,
  INDEX `FK8ky5rrsxh01nkhctmo7d48p82`(`user_id`) USING BTREE,
  INDEX `idx_blog_first_picture_upload_id`(`first_picture_upload_id`) USING BTREE,
  INDEX `idx_blog_first_picture_type`(`first_picture_type`) USING BTREE,
  INDEX `idx_blog_status`(`blog_status`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 99 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_blog
-- ----------------------------
INSERT INTO `t_blog` VALUES (93, b'0', b'1', '# 序言\r\n**如果您能看完关于泪心的全文介绍,我将非常感谢,不看也没关系,这只是作者对于自己青春的自我叙述。**  **<font color=\"darkred\">也许你很想知道，明明大型平台这么多，为什么非要自己搭建个平台发布技术交流。原因很简单，因为技术限流，风险提示。只好自己搭建全新平台咯。非要一句话概括,实乃天下熙攘皆为利往。我不给平台打钱,它为啥不封禁我呢？</font>**\r\n先介绍一下，我是泪心，一名互联网上资深独立自由软件开发员。为什么要带着\"资深\"呢？这跟我大学期间胡乱碰撞有关，听说那个技术好那个技术能立马获取成就感就去学那个，最后发现自己浪费了年华结果一事无成。虽说如此，但是我大学期间的确是让我把全部编程语言入门了一次。\r\n<br>\r\n为什么要叫泪心呢,现在2025年则是满满的失意,对自己对外面对世界全是失意,一种说不上来的余生孤独感，仿若存在就像是个固定的NPC找不到自己的归属地。或许，“泪心”本来就是以泪洗面吧？也许,我已经失去了爱,生活已经逼得我披上了一层保护自己的无痕泡沫。\r\n\r\n---\r\n\r\n\r\n## 主题一：失意迷乱，身心俱疲\r\n\r\n沉默，是保护自己；  \r\n安静，是守护自己；  \r\n出品，是表现自己；  \r\n低调，是护全自己；  \r\n隐忍，是成就自己；  \r\n欣慰，是获取赞美。  \r\n\r\n世间利，人间利，本是利，既无利，何以为生？我亦不愿如此，可恨人世间就是一场盛大的虚拟游戏。爱恨离愁都是感受到感受不到的无关缓存，只是碎裂了套娃世界的中枢服务器，却导致了某个对象独立内存的溢出毁灭式崩溃。\r\n\r\n你活着，你要卑微的活着，你要去顺应你看得见的黑暗，你要拥抱你看得见的光明，然而操控者却非让你在黑暗与光明中反复融合出全新的结界。人与人之间的分离、兵变，只是我们选择了不同的人生和不同的际遇，不论结果如何都是一份完整的满分答卷。再次我默默的致谢：不用难过，我们都没错，好好活下去终有一天会发现自己存在的意义。\r\n\r\n---\r\n\r\n## 主题二：光芒一时，紊乱一期\r\n\r\n2017年，我误入了身心奴虐的地域，好比把我丢入了暮气沉沉的JSP框架程序中自生自灭。  \r\n2020年，我选择了流行光鲜无忧无虑的全新区域，开开心心好似对着Java世界第一次输出\"hello,world\"的新奇。  \r\n\r\n之后的学生生涯中：  \r\n- 从web安全研究到web项目开发  \r\n- 转到PC端安全研究  \r\n- PC端驱动开发  \r\n- PC端逆向研发  \r\n- 某次机缘下成为安卓C++底层世界开发的一粒微尘  \r\n\r\n---\r\n## 主题三：世界本暗，持汝初心\r\n\r\n不知不觉间，我已经在茫茫代码领域中发展成为了商业开发的一员，不知不觉已经成为了互联网独立开发程序员，有着自己的生活区域。我只是在漫长的迁跃中，走出了某条适应于自我的一种道路。对你而言可能错了，但是选择从来不分对错，结果更是不分好坏，只是命运的棋子悄然变动了。\r\n\r\n我的初心是美好且伟大的。或许每个大学生都怀有悬壶济世、热血江山的梦想，只是在社会摸爬滚打中渐渐认清了冰冷彻骨的现实。在我还没有接触代码的时候，为了避免\"穷人自有恶人磨\"的寒苦生活现状，我持续生活在别人设定的虚拟假象世界中（游戏、小说、电视）。甚至严重到了修仙成佛的地步，说来也搞笑，但这些都是曾经抓得住的切片记忆。\r\n\r\n我有一个梦想：\r\n- 让全世界所有人幸福安康，让所有苦难都消融，让微笑快乐常挂身边，以热爱快乐面对每一天的全新生活\r\n- 让冷漠被热情替代，让寒冰坚雪融化，让世间苦难不再持续发生，找回儿童真挚的笑容，重拾\"自由快乐平等\"\r\n- 既然所有的商业物品都是披着羊皮的狼，那就让全世界所有的商品都回归原始真实状态，让消费者安心舒适的选购\r\n- 让竞争不再头破血流，无论任何事，都可以平常心、同等心静静解决\r\n- 掌控江山，成为异界之神，坐拥亿万生灵，尊享华夏庙堂\r\n\r\n理想化丰满化的虚拟生活只存在于游戏中、影视中、虚拟中。人血馒头的残酷现实扼杀了全部的梦。\r\n\r\n我的初心是什么？只是想真诚实在、热心助人、平等对待，让风范从课本中走出来，有错吗？\r\n\r\n是的，大错特错：\r\n- 因为课本全是抽象化的错误式儒学奴化教育，目的就是为了掌控廉洁高质量的年轻劳动力\r\n- 因为让真实彻底走到明面上，将会剥夺大量行业的生存之道，甚至直接击垮整个社会\r\n- 本来，真真假假虚实不分，善恶交融有序离场，好坏不分才能正常运转。人世间的运转本就是复杂多样且不可预测的\r\n\r\n世界本就黑暗，但请保持你的初心。\r\n\r\n\r\n## 主题四:技术的永恒悖论(独立编程者自省)\r\n> \"别以为站在技术巅峰就能永恒——今天的屠龙者，注定是明天被AI和更年轻大脑淘汰的\'古典程序员\'。\"--泪心\r\n\r\n作为一名在网络安全与逆向工程领域深耕多年的技术从业者，我经历了从入门到精通的完整成长轨迹。这段旅程带给我的不仅是技术能力的提升，更引发了对技术本质的深刻思考。\r\n\r\n### 技术的双面性\r\n\r\n在这个日新月异的数字时代，技术就像一把双刃剑。我们引以为傲的代码成果，那些熬夜攻克的技术难关，很可能在未来成为颠覆我们自身的武器。这种宿命般的循环，正是技术从业者面临的最大悖论。\r\n\r\n从最初转发技术论坛内容，到后来能够独立开发复杂的逆向工具，我深刻体会到技术迭代的无情。那些曾经让我兴奋不已的技术突破，如今看来不过是技术长河中的一朵浪花。\r\n\r\n### 商业与理想的平衡\r\n\r\n随着技术能力的提升，我不得不面对一个现实问题：如何平衡技术理想与商业需求？从完全无偿的技术分享，到建立可持续的商业模式，这个过程充满挑战与反思。\r\n\r\n我始终坚持一个原则：即使在商业化运作中，也要保留技术的普惠性。每个项目都设有免费与付费版本的明确区分，这不仅是一种商业策略，更是对技术初心的坚守。\r\n\r\n### 给后来者的建议\r\n\r\n看着新一代技术爱好者涌入这个领域，我仿佛看到了当年的自己。在此，我想分享几点感悟：\r\n\r\n1. **技术会过时，但解决问题的能力永不过时**\r\n2. **保持学习热情比掌握特定技术更重要**\r\n3. **在追求技术深度的同时，不要忽视技术伦理**\r\n\r\n在这个快速变化的时代，我们或许无法永远站在技术前沿，但可以培养超越技术本身的思维方式。这才是技术从业者真正的核心竞争力。\r\n\r\n技术之路没有终点，只有不断的自我超越与反思。愿我们都能在技术探索中，找到属于自己的平衡点。\r\n\r\n很可惜,你们跟我走的是同一条路,只注重实战项目,只看最终结果。虽然说社会就是需要这样的人才,公司就是需要这样的员工。\r\n但是这样写代码写久了后,你会发现自己在某个方向渐行渐远,甚至分不清什么是虚拟什么是现实,简单点说就是空荡荡的。', '2025-06-28 13:04:31', '/images/bg2.jpg', NULL, 'link', '原创', b'0', b'1', b'0', '泪心的由来，一条充满复杂色的成长道路', '2025-06-29 03:05:04', '115385', 43, 3, '泪心,似乎是一个脆弱的标识。或许立名的开始只是因为那个“她”，后面却成为了悲剧中的那个“它”，明知道自己陷入了不归的泥炭沼泽，却再也无法跳出手敲键盘跳出代码的虚拟世界了。', 1, NULL, 0);
INSERT INTO `t_blog` VALUES (94, b'1', b'1', '# 个人博客系统技术概要\r\n## 序言\r\n很多时候，要的只是结果，而不是你累死累活的过程，资本化的快速商业战，是完全不需要过程，只要你最终给出完美的答卷就行。所以请不要在乎我利用了人工智能，如网络上说有工具不用非要手动“没苦硬吃”那是傻逼。\r\n## 项目概述\r\n本系统是基于SpringBoot+MyBatis的轻量级个人博客平台，采用前后端分离架构设计，具备文章管理、分类归档、互动留言、多媒体展示等完整博客功能。前端融合Semantic UI框架与多款JavaScript插件实现优雅交互，后端依托SpringBoot生态实现高效开发。--技术详解非常细致哦<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n---\r\n## 项目代码框架分析\r\n    src/main/java\r\n    ├─com.star\r\n      ├─config          // 配置类\r\n      ├─controller      // 控制层\r\n      ├─service         // 业务层\r\n         ├─impl         // 实现类\r\n      ├─dao             // 持久层接口\r\n      ├─entity          // 实体类\r\n      ├─interceptor     // 拦截器\r\n      ├─handle       // 异常处理\r\n      ├─util            // 工具类\r\n    resources\r\n      ├─templates       // 视图模板\r\n      ├─static          // 静态资源 含泪心自加资源\r\n\r\n## 技术架构概览\r\n### 前端技术栈\r\n| 模块 | 技术实现 |\r\n|------|----------|\r\n| **核心框架** | jQuery + Semantic UI |\r\n| **富文本编辑** | Markdown编辑器 |\r\n| **代码高亮** | prism.js |\r\n| **动态效果** | animate.css 动画库 |\r\n| **文章导航** | Tocbot目录生成器 | \r\n| **音乐播放** | zPlayer音乐播放器 |\r\n| **图片展示** | lightbox.js照片墙插件 |\r\n### 后端技术栈\r\n| 模块 | 技术组件 |\r\n|------|----------|\r\n| **核心框架** | SpringBoot 2.2.5 |\r\n| **ORM框架** | MyBatis |\r\n| **模板引擎** | Thymeleaf |\r\n| **分页处理** | PageHelper分页插件 |\r\n| **安全加密** | MD5密码加密 |\r\n| **项目构建** | Maven 3.x + JDK 8+ |\r\n### 数据库\r\n- **版本**: MySQL 8.x\r\n- **连接池**: HikariCP (SpringBoot默认)\r\n---\r\n### 核心数据表说明\r\n| 数据表       | 功能描述                          |\r\n|--------------|---------------------------------|\r\n| `t_blog`     | 存储文章主体内容及元数据           |\r\n| `t_type`     | 文章分类标签目录                  |\r\n| `t_user`     | 系统用户信息（含管理员）           |\r\n| `t_comment`  | 文章评论数据                     |\r\n| `t_message`  | 留言板消息记录                   |\r\n| `t_friend`   | 友情链接资源池                   |\r\n| `t_picture`  | 相册图片及元数据                 |\r\n\r\n\r\n<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n# 博客系统功能说明\r\n\r\n## 用户端功能\r\n\r\n### 1. 内容浏览功能\r\n- **文章展示**\r\n  - 支持文章列表分页展示\r\n  - 提供文章详情阅读页面\r\n  - 自动记录文章访问量\r\n\r\n- **内容检索**\r\n  - 按分类浏览（技术/生活/随笔等）\r\n  - 标签云导航\r\n  - 支持关键词搜索\r\n\r\n### 2. 多媒体功能\r\n- **音乐播放**\r\n  - 支持播放/暂停/切歌\r\n  - 显示同步歌词\r\n  - 音量调节控制\r\n\r\n- **相册展示**\r\n  - 图片瀑布流布局\r\n  - 点击放大查看\r\n  - 显示照片拍摄信息\r\n\r\n### 3. 互动功能\r\n- **评论系统**\r\n  - 支持留言回复\r\n  - 区分普通用户和管理员\r\n  - 表情符号支持\r\n\r\n- **友情链接**\r\n  - 合作伙伴展示\r\n  - 链接点击统计\r\n  - 友链申请入口\r\n\r\n## 管理端功能\r\n\r\n### 1. 内容管理\r\n- **文章管理**\r\n  - 新增/编辑/删除文章\r\n  - Markdown编辑器\r\n  - 草稿自动保存\r\n\r\n- **分类管理**\r\n  - 多级分类设置\r\n  - 分类排序调整\r\n  - 关联文章统计\r\n\r\n### 2. 消息管理\r\n- **评论审核**\r\n  - 新评论提醒\r\n  - 敏感词过滤\r\n  - 批量处理功能\r\n\r\n- **通知系统**\r\n  - 站内消息提醒\r\n  - 邮件通知设置\r\n  - 消息标记已读\r\n\r\n### 3. 系统安全\r\n- **登录验证**\r\n  - 账号密码登录\r\n  - 密码加密存储\r\n  - 登录日志记录\r\n\r\n- **权限控制**\r\n  - 管理后台隔离\r\n  - 操作日志追踪\r\n  - 异常登录检测\r\n\r\n> 部署环境要求：\r\n> - JDK 17+\r\n> - MySQL 8.0+\r\n> - Redis 6.0+\r\n\r\n> 开发进度：\r\n> - 用户端：基本完成\r\n> - 管理端：主要功能完成\r\n\r\n## 代码开发重难点分析\r\n### MD5加密工具类说明\r\n\r\n#### 功能图解\r\n![MD5加密流程示意图](/images/myblog/md5code.png)  \r\n\r\n*图：MD5加密算法处理流程*\r\n\r\n\r\n```java\r\n/**		md5不可逆加密核心代码实现\r\n * MD5加密工具类\r\n */\r\npublic class MD5Utils {\r\n    /**\r\n     * 生成32位MD5哈希值\r\n     * @param str 原始字符串\r\n     * @return 32位小写MD5值\r\n     */\r\n    public static String code(String str) {\r\n        try {\r\n            MessageDigest md = MessageDigest.getInstance(\"MD5\");\r\n            byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));\r\n            StringBuilder hexString = new StringBuilder();\r\n            for (byte b : digest) {\r\n                String hex = Integer.toHexString(0xff & b);\r\n                if(hex.length() == 1) {\r\n                    hexString.append(\'0\');\r\n                }\r\n                hexString.append(hex);\r\n            }\r\n            return hexString.toString();\r\n        } catch (NoSuchAlgorithmException e) {\r\n            throw new RuntimeException(\"MD5 algorithm not found\", e);\r\n        }\r\n    }\r\n}\r\n```\r\n\r\n> 根据泪心分析 这样一加密后意味着存储在数据库的密码是会被md5加密初始化的，什么意思呢，就是说只要你调用这个函数，你查找账号密码，那么这个密码必定是经过了md5加密混淆。因此如果你数据库上面的密码是admin,但是你在网页输入的是admin，结果自然而然就不通过了。因为这个是直接查询你数据库的密码，然后网页层面已经经过了md5混淆加密，跟你输入的值完全不对等了。<br>那么如果说我实在想用怎么办呢，方法一,直接加密对应密码生成后的md5数据，把数据载入数据库对应密码中。方法二，实现用户输入注册生成已加密的数据并自动载入数据库。方法二虽好但是必须做足安全性，否则很容易被人恶意调试直接拿到后端管理员账号密码。\r\n\r\n```java\r\n@Service\r\npublic class UserServiceImpl implements UserService {\r\n\r\n    @Autowired\r\n    private UserDao userDao;\r\n\r\n    /**\r\n     * @Description: 业务逻辑层代码实现\r\n     * @Auther: 泪心\r\n     * @Date: 21:25 2025/6/29\r\n     * @Param: username:用户名；password:密码\r\n     * @Return: 返回用户对象\r\n     */\r\n    @Override\r\n    public User checkUser(String username, String password) {\r\n        User user = userDao.findByUsernameAndPassword(username, MD5Utils.code(password));\r\n        return user;\r\n    }\r\n}\r\n```\r\n### 全局异常页面url处理\r\n```java\r\n// GlobalExceptionHandler.java\r\n@ControllerAdvice\r\npublic class GlobalExceptionHandler {\r\n    // 处理404等未匹配路径\r\n    @ExceptionHandler(NoHandlerFoundException.class)\r\n    public String handle404() {\r\n        return \"redirect:/error\";\r\n    }\r\n    // 统一错误页面\r\n    @GetMapping(\"/error\")\r\n    public String errorPage() {\r\n        return \"error\"; // templates/error.html\r\n    }\r\n```\r\n\r\n### 管理员权限拦截器实现\r\n```java\r\n// AuthInterceptor.java\r\npublic class AuthInterceptor implements HandlerInterceptor {\r\n    @Override\r\n    public boolean preHandle(HttpServletRequest request, \r\n                            HttpServletResponse response, \r\n                            Object handler) throws Exception {\r\n        // 检查登录状态（示例Session实现）\r\n        HttpSession session = request.getSession();\r\n        String username = (String) session.getAttribute(\"currentUser\");\r\n        // 排除登录接口\r\n        String uri = request.getRequestURI();\r\n        if(uri.contains(\"/login\") || uri.contains(\"/static\")){\r\n            return true;\r\n        }\r\n        // 未登录用户重定向\r\n        if(username == null || username.isEmpty()){\r\n            response.sendRedirect(\"/admin/login\");\r\n            return false;\r\n        }\r\n        // 管理员权限校验（扩展点）\r\n        return checkAdminRole(username); \r\n    }\r\n}\r\n// WebMvcConfig.java\r\n@Configuration\r\npublic class WebMvcConfig implements WebMvcConfigurer {\r\n    @Override\r\n    public void addInterceptors(InterceptorRegistry registry) {\r\n        registry.addInterceptor(new AuthInterceptor())\r\n                .addPathPatterns(\"/admin/**\")        // 拦截后台路径\r\n                .excludePathPatterns(\"/admin/login\");// 放行登录入口\r\n```\r\n\r\n### 分页插件PageHelper使用\r\n```java \r\n  //分页查询博客列表\r\n    @GetMapping(\"/\")\r\n    public String index(Model model, @RequestParam(defaultValue = \"1\",value = \"pageNum\") Integer pageNum, RedirectAttributes attributes){\r\n        PageHelper.startPage(pageNum,10);\r\n        //查询博客列表\r\n        List<FirstPageBlog> allFirstPageBlog = blogService.getAllFirstPageBlog();\r\n        //查询最新推荐博客\r\n        List<RecommendBlog> recommendedBlog = blogService.getRecommendedBlog();\r\n        //查询最新评论\r\n        List<NewComment> newComments = blogService.getNewComment();\r\n\r\n        PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(allFirstPageBlog);   //博客内容实体类  定义泛型类对象获取查询后的博客List列表\r\n        model.addAttribute(\"pageInfo\",pageInfo);  //页面信息传输到前端\r\n        model.addAttribute(\"recommendedBlogs\", recommendedBlog);  //推荐博客传输到前端\r\n        model.addAttribute(\"newComment\",newComments);  //评论\r\n        return \"index\";\r\n    }\r\n```\r\n\r\n```java\r\n@Controller\r\npublic class TypeShowController {\r\n\r\n    @Autowired\r\n    private TypeService typeService;   //分类实现接口\r\n\r\n    @Autowired\r\n    private BlogService blogService;   //博客实现接口\r\n\r\n    //    分页查询分类\r\n    @GetMapping(\"/types/{id}\")\r\n    public String types(@RequestParam(defaultValue = \"1\",value = \"pageNum\") Integer pageNum, @PathVariable Long id, Model model) {\r\n        List<Type> types = typeService.getAllTypeAndBlog();\r\n\r\n        //id为-1表示从首页导航栏点击进入分类页面\r\n        if (id == -1) {\r\n            if(!types.isEmpty()){\r\n                id = types.get(0).getId();\r\n            }\r\n        }\r\n        model.addAttribute(\"types\", types);\r\n        List<FirstPageBlog> blogs = blogService.getByTypeId(id);  //查询分类返回博客List列表对象\r\n\r\n        PageHelper.startPage(pageNum, 10000);\r\n        PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogs);\r\n        model.addAttribute(\"pageInfo\", pageInfo);   //把查询到的博客泛型类对象（list列表）全部加载传输到前端\r\n        model.addAttribute(\"activeTypeId\", id);  //特别标准传输分类ID标签\r\n        return \"types\";\r\n    }\r\n\r\n}\r\n```\r\n```html\r\n<!-- 分类列表容器 -->\r\n<div class=\"ui vertical menu\">\r\n  <!-- 遍历后端传来的types集合 -->\r\n  <div th:each=\"type : ${types}\" class=\"item\">\r\n    <!-- \r\n      点击分类时跳转到/type/{id}路由\r\n      后端交互：向后台发送带分类ID的GET请求\r\n      高亮逻辑：当前分类ID等于activeTypeId时添加active类\r\n    -->\r\n    <a th:href=\"@{/type/{id}(id=${type.id})}\" \r\n       th:text=\"${type.name}\"\r\n       th:classappend=\"${type.id == activeTypeId} ? \'active\'\">\r\n      默认分类\r\n    </a>\r\n  </div>\r\n</div>\r\n```\r\n```html\r\n<!-- \r\n  主内容区 - 显示筛选后的博客\r\n  数据来源：后端返回的pageInfo.list集合\r\n-->\r\n<div th:each=\"blog : ${pageInfo.list}\" class=\"blog-item\">\r\n  <h3 th:text=\"${blog.title}\"></h3>\r\n  \r\n  <!-- \r\n    显示该博客所属分类 \r\n    数据来源：后端在查询博客时联表查询type.name\r\n  -->\r\n  <div class=\"type-tag\" th:text=\"${blog.typeName}\"></div>\r\n  \r\n  <!-- 其他博客内容... -->\r\n</div>\r\n```\r\n```html\r\n<!-- \r\n  分页栏 - 关键点：翻页时保持当前分类\r\n  后端交互：分页链接始终携带当前activeTypeId\r\n-->\r\n<div class=\"pagination\">\r\n  <!-- 上一页 -->\r\n  <a th:href=\"@{/type/{id}(id=${activeTypeId}, pageNum=${pageInfo.prePage})}\" \r\n     th:unless=\"${pageInfo.isFirstPage}\">\r\n    上一页\r\n  </a>\r\n  \r\n  <!-- 页码显示 -->\r\n  <span th:text=\"${pageInfo.pageNum + \'/\' + pageInfo.pages}\"></span>\r\n  \r\n  <!-- 下一页 -->\r\n  <a th:href=\"@{/type/{id}(id=${activeTypeId}, pageNum=${pageInfo.nextPage})}\" \r\n     th:unless=\"${pageInfo.isLastPage}\">\r\n    下一页\r\n  </a>\r\n</div>\r\n```\r\n\r\n### SpringBoot核心配置文件YML\r\n1. 基本语法要求\r\n缩进规则：必须使用 2个空格 作为缩进层级（禁止使用 Tab 键）\r\n\r\n键值分隔：冒号 : 后必须加 1个空格（如 key: value）\r\n\r\n大小写敏感：配置项推荐全小写，单词间用下划线（如 max_retry_count）\r\n\r\n注释：使用 # 开头，后接1个空格（如 # 这是注释）\r\n\r\n2. 多环境配置\r\n使用 --- 分隔不同环境的配置\r\n\r\n通过 spring.profiles.active 指定当前激活的环境\r\n当然了 你可以根据jar包运行时候指定环境状态,如下所示\r\n```\r\n# 开发环境启动命令：\r\n#   java -jar your-app.jar --spring.profiles.active=dev\r\n# \r\n# 生产环境启动命令：\r\n#   java -jar your-app.jar --spring.profiles.active=pro\r\n```\r\n这是一个完整的本项目配置部署文件示范例子\r\n```yaml\r\n# ========================================================\r\n#                Spring Boot 通用配置文件\r\n# 特点：\r\n# 1. 不分 dev/pro 环境，所有配置直接生效\r\n# 2. 敏感信息通过环境变量注入（如数据库密码）\r\n# 3. 提供合理的默认值，保证直接启动不报错\r\n# ========================================================\r\n\r\n# #################### 核心框架配置 ####################\r\nspring:\r\n  # Thymeleaf 模板引擎\r\n  thymeleaf:\r\n    mode: HTML\r\n\r\n  # 数据源配置（默认使用本地数据库，可通过环境变量覆盖）\r\n  datasource:\r\n    driver-class-name: com.mysql.cj.jdbc.Driver\r\n    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:myblog}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC\r\n    username: ${DB_USER:root}       # 默认用户名，可用环境变量覆盖\r\n    password: ${DB_PASSWORD:admin} # 默认密码，生产环境务必通过环境变量修改！\r\n    hikari:\r\n      max-lifetime: 500000\r\n\r\n  # 邮件服务（默认关闭，需主动配置）\r\n  mail:\r\n    host: ${MAIL_HOST:smtp.163.com}\r\n    username: ${MAIL_USER:}         # 默认空，避免误发邮件\r\n    password: ${MAIL_PASSWORD:}\r\n    default-encoding: utf-8\r\n    protocol: smtp\r\n    port: 25\r\n    properties:\r\n      mail.smtp.starttls.enable: true\r\n\r\n  # Redis（默认连接本地，可环境变量覆盖）\r\n  redis:\r\n    host: ${REDIS_HOST:127.0.0.1}\r\n    port: ${REDIS_PORT:6379}\r\n    database: 0\r\n    timeout: 1800000\r\n\r\n  resources:\r\n    static-locations: classpath:/static/\r\n\r\n# #################### MyBatis 配置 ####################\r\nmybatis:\r\n  type-aliases-package: com.star.entity\r\n  mapper-locations: classpath:mapper/*.xml\r\n  configuration:\r\n    map-underscore-to-camel-case: true\r\n\r\n# #################### 日志配置 ####################\r\nlogging:\r\n  level:\r\n    root: info      # 默认日志级别\r\n    com.star: debug # 项目代码详细日志\r\n  file:\r\n    name: log/blog.log  # 统一日志路径\r\n\r\n# #################### 自定义配置 ####################\r\ncomment.avatar: /images/avatar.png\r\nmessage.avatar: /images/avatar.png\r\n```\r\n\r\n\r\n## 项目部署实战\r\n### MyBlog 开源博客项目部署指南\r\n#### 环境要求\r\n- JDK 8+\r\n- Maven 3.6+\r\n- MySQL 5.7+\r\n- Git\r\n- IDE工具（IntelliJ IDEA）\r\n- 数据库工具（Navicat）\r\n---\r\n### 部署步骤\r\n#### 1. 克隆项目源码(可能需要科学魔法上网)\r\n```javascript\r\ngit clone https://github.com/tearhacker/myblogTear.git\r\n```\r\n---\r\n### 2. 导入数据库\r\n1. 使用Navicat创建新数据库（建议名称为`myblog`）\r\n2. 右键新建的数据库 -> 选择「运行SQL文件」\r\n3. 选择项目中的`/sql/myblog.sql`文件导入\r\n4. 确认导入成功后验证表结构是否生成\r\n---\r\n### 3. 项目配置\r\n1. 使用IDEA打开项目：\r\n   - 选择 `File > Open` \r\n   - 定位到项目根目录下的 `pom.xml` 文件\r\n   - 选择「Open as Project」\r\n2. 修改配置文件：\r\n   - 打开 `src/main/resources/application.yml`\r\n   - 修改数据库连接配置\r\n   - 修改redis配置\r\n   - 修改其他配置\r\n---\r\n### 4. 启动应用\r\n1. 找到主启动类：\r\n   - 通常位于 `src/main/java/com/star/myblog/MyBlogApplication.java`\r\n2. 运行启动类：\r\n   - 右键选择「Run MyBlogApplication」\r\n   - 或通过Maven命令：\r\n3. 验证启动：\r\n   - 控制台输出 `Started MyBlogApplication in X.XXX seconds` \r\n   - 访问 `http://localhost:8080`（端口以实际配置为准）\r\n---\r\n## 常见问题\r\n1. **数据库连接失败**\r\n   - 检查yml配置中的用户名/密码\r\n   - 确认MySQL服务已启动\r\n2. **端口冲突**\r\n   - 在application.yml中修改`server.port`\r\n3. **依赖下载缓慢**\r\n   - 更换Maven镜像源至阿里云\r\n4. **新人求助**\r\n   - 提供一切帮助,请联系泪心(有偿服务)\r\n## 可能用上的外链资源\r\n1. **音乐外链解析**\r\n   - <a href=\"https://api.toubiec.cn/wyapi.html\">免费解析音乐</a>\r\n2. **泪心游戏禁地**\r\n   - <a href=\"http://t.me/TearGame\">泪心TG电报频道(闲人勿扰,乱汝心智)</a>\r\n   \r\n\r\n\r\n', '2025-06-29 02:21:56', '/images/myblog/blogInexSHow.png', NULL, 'link', '原创', b'0', b'1', b'0', 'MYBLOG个人博客系统搭建和技术要点分析', '2025-06-30 02:05:17', '106419', 44, 3, 'SpringBoot技术关于泪心myblog(原一颗星)个人博客项目的完整见闻,这应该是第n次搭建了,不同的是这次是完全独立分析和自主研学的。欢迎大家点评和提出更多的意见，感谢大家。', 1, NULL, 1);
INSERT INTO `t_blog` VALUES (95, b'1', b'1', '# 泪心LOL端游PC端游戏使用教程\r\n## 一、必备资源下载地址\r\n--- 密码是  kingglobal\r\n- 下载链接：[https://wwqo.lanzouo.com/b00q0x4lij](https://wwqo.lanzouo.com/b00q0x4lij)\r\n\r\n- 提取密码：3uji\r\n\r\n\r\n[详细图文教程点我跳转](https://flowus.cn/share/5bcbf0fb-737c-4ecb-a4e2-c79d47eb28c7?code=YWP6FT)\r\n\r\n# 二、Proxifier 软件启动与配置文件导入步骤\r\n\r\n1. 安装软件时，需查看 `readme.txt` 文件，从中获取激活密钥以完成软件激活。\r\n\r\n2. 打开已激活的 Proxifier 软件，点击顶部菜单栏的 **File** 选项，在下拉菜单中选择 **Import File**。\r\n\r\n3. 在弹出的文件选择窗口中，选中名为 “泪心规则” 的配置文件并导入。\r\n\r\n4. 若导入配置文件时需要密码，可咨询泪心获取，完成以上操作后即可正常进行游戏。\r\n\r\n\r\n\r\n\r\n## 一、访问端口验证地址\r\n\r\n打开浏览器，输入或点击以下验证地址，测试是否成功连接服务器：[34.160.111.145](http://34.160.111.145/)\r\n\r\n[34.160.111.145](http://34.160.111.145/)\r\n\r\n[34.160.111.145](http://34.160.111.145/)\r\n\r\n## 验证成功标识\r\n\r\n\r\n', '2025-10-04 08:14:48', '/api/files/download/16', 16, 'upload', '原创', b'1', b'1', b'1', 'LOL英雄联盟WEGAME端游实现端口封包无视封号', '2025-10-29 02:16:09', '108339', 44, 3, 'LOL端游换肤插件端口防封技术！', 3, 'a89e37a7acdf507b95d4cbafac0a840d', 0);
INSERT INTO `t_blog` VALUES (97, b'0', b'0', '第一章 言情小说是怎么来的，开局之我是A城逆袭。这都是这十年来小说作者观察到现实现代生活的无线放大反馈。没有真实的生活情景或体验，用什么凭空创造魔幻虚夸的小说世界。\r\n第二章 成为掌控世界规则的上帝\r\n男人实在为了社会这个富人的生活长久而不断燃烧自我，女人为了不断攀高而堵死自己一生的自我意识觉醒。人人都想要掌握命运，可是人人都被这个富人法则锁死脚步。如果一个普通的男孩，突然逆袭变成闪耀光芒的成功人士，那说明他背后背着无数你看不到的血腥和恐怖荆棘;如果一个平庸的女孩，突然蜕变变成柔波脉脉的公主，那已经点缀并彻底绽开了她的原始欲望，为攀高不择手段。\r\n\r\n', '2025-12-28 11:25:12', '/api/files/download/20', 20, 'upload', '原创', b'0', b'0', b'0', '鲜艳主播刺眼玫瑰:一次次选择都是一次次血腥的交换', '2025-12-28 11:25:12', '23335', 43, 3, '这世界哪有什么逆袭变强，这世界哪有什么废土重生，这世界何来赏金之好，都是血淋淋的利益交换和实实在在的自我泯灭', 1, NULL, 0);
INSERT INTO `t_blog` VALUES (98, b'0', b'0', '# 蕴宇科技有限责任公司（Yunyu Technology Co., Ltd.）\r\n\r\n> **访问密码：`9LSpace0010086`**  \r\n> **GitHub 源码仓库：** `git@github.com:TearGame/SUMSpaceGame.git`  \r\n> **Git 命令：**  \r\n> ```bash\r\n> git remote add origin git@github.com:TearGame/SUMSpaceGame.git\r\n> ```\r\n\r\n---\r\n\r\n## 公司定位\r\n\r\n一家专注于技术创新与数字服务的 IT/互联网企业，致力于为客户提供高质量、高安全性的软件解决方案与技术服务。\r\n\r\n\r\n## 需要人才\r\n律师朋友（非就职）真有事临时来一次就可以        专业程序员（1位即可）   强力销售    剪切宣传   \r\n\r\n---\r\n\r\n## 所属行业\r\n\r\n信息传输、软件和信息技术服务业（IT / 互联网）\r\n\r\n---\r\n\r\n## 经营范围\r\n\r\n- SaaS 系统对接与使用（软件即服务）\r\n- 软件开发、销售及技术服务\r\n- 信息系统集成服务\r\n- 商业源代码的授权、转让与定制开发\r\n- 全栈式 Web 及移动应用系统定制\r\n- 技术咨询与数字化转型解决方案\r\n- 互联网技术培训与职业技能教育（不含学历教育）\r\n- 计算机软硬件及辅助设备销售\r\n- 数据处理与存储服务\r\n\r\n> ⚠️ 注：依法须经批准的项目，经相关部门批准后方可开展经营活动。\r\n\r\n---\r\n\r\n## 核心价值观\r\n\r\n### 平等协作\r\n拒绝个人崇拜，尊重每位成员的贡献，倡导能力互补、人格平等的团队文化。\r\n\r\n### 责任共担\r\n坚持“一荣俱荣，一损俱损”的团队精神，强调集体成果与共同成长。\r\n\r\n---\r\n\r\n## 员工守则（精炼版）\r\n\r\n1. **人人平等**  \r\n   不神化任何人，无论职位、能力或社交影响力，所有员工享有平等尊重与发展机会。\r\n\r\n2. **团队至上**  \r\n   个人成功源于团队协作，公司成败系于每位成员——我们共享荣誉，共担责任。\r\n\r\n---\r\n\r\n## 企业愿景\r\n\r\n成为值得信赖的技术伙伴，用代码构建透明、高效、可持续的数字未来。\r\n\r\n\r\n\r\n\r\n# 蕴宇科技有限责任公司 — 盈利方向（合规版）\r\n\r\n## 核心定位：SaaS + 全栈系统服务商\r\n\r\n### 1. 标准化源码产品包（¥99 – ¥499）\r\n- 面向：学生、个体户、小微创业者\r\n- 特点：开箱即用、含部署文档、基础售后\r\n- 示例：校园选课系统、服装进销存、培训机构CRM\r\n\r\n### 2. 私有化部署与年度运维服务（¥3,000 – ¥20,000/年）\r\n- 面向：学校、中小企业、地方机构\r\n- 包含：部署 + 域名绑定 + 微信对接 + 月度更新 + 远程支持\r\n\r\n### 3. 技术培训业务（公司主体开展）\r\n- **安全方向 A：全栈开发实战营**  \r\n  内容：Vue3 + Spring Boot + Docker 项目开发  \r\n  定价：¥2,000 – ¥5,000  \r\n  形式：直播+录播+项目评审\r\n\r\n- **安全方向 B：安卓底层开发（合规版）**  \r\n  内容：AOSP 编译、GKI 驱动适配、嵌入式 Linux 开发  \r\n  **严禁涉及：ACE/SafetyNet绕过、内存篡改、游戏保护对抗**  \r\n  定价：¥800 – ¥3,000（入门级原理课）\r\n\r\n> 所有培训签署《学员合规承诺书》，禁止用于非法用途。\r\n\r\n\r\n\r\n\r\n-----------------------------------------------------------------------禁用 否则会变成法人判刑证据---------------------------------\r\n盈利方向            **公司核心站位方向**即Saas传统IT行业\r\n第一部分 售卖网站全栈系统源码    \r\n需要大量前期准备工作 从开发到演示到宣传到部署  都需要时间成本  目前市场似乎是低价10~100元  我们源码可以定制化优质化500元之内售卖\r\n\r\n服务对象：\r\n学生群体  部分小微企业（无专业程序员）     个体工商户老板（无专业程序员）\r\n\r\n第二部分 部署全栈系统并实时服务（小微企业 中型企业 学校 培训机构） \r\n应用于微信小程序/公众号前台和PC电脑网站前后台  实时使用和随时维护更新\r\n比如服装管理系统、校园选课管理系统、校园ID卡水电充值系统\r\n\r\n第三部分 教育培训方向（法人是我）别乱来！    \r\n利用公司主体  进行针对逆向技术开发教学（风险）    当前市场行情如下 这是千问AI联网自动分析出来的 不够准确  但是也够用\r\n利用公司主体  进行针对AI全栈编程项目开发教学（安全）  正常收钱正常做就行\r\n| 培训形式 | 费用范围 | 适合人群 | 特点 |\r\n|--------|--------|--------|------|\r\n| 线上录播课（入门级） | ¥800 – ¥3,000 | 零基础试水、学生、自学者 | 内容较基础，如IDA使用、简单APK反编译；缺乏实时答疑与实战环境 |\r\n| 线上直播小班课（系统班） | ¥5,000 – ¥12,000 | 有编程或安全基础者 | 包含APP逆向、加解密算法分析、Frida/Xposed动态调试等；部分含CTF或接单项目演练 |\r\n| 线下全日制实训班 | ¥15,000 – ¥30,000+ | 转行/就业导向学员 | 提供真实靶场、企业级案例（如游戏保护、金融APP加固对抗）、就业推荐；周期通常3–6个月 |\r\n| 高端专项班（如Windows内核/固件逆向） | ¥20,000 – ¥40,000 | 有汇编/C/C++基础的进阶者 | 涉及驱动逆向、恶意代码分析、IoT设备固件提取等，师资多来自安全厂商或红队 |\r\n\r\n\r\n我们的费用教学培训表价格应该是\r\n100~1000元左右   针对编译完整GKI谷歌驱动\r\n2000~5000元左右  培训Java/Php/C.net动态网站系统零基础快速开发\r\n\r\n\r\n第四部分 绘制辅助隐形收入（万万不可使用公司名义） \r\n这是你们自己的事情  被抓了你们自己承担全部责任 一切与公司业务无关  与公司经营无关  别连累我这个法人\r\n-----------------------------------------------------------------------禁用 否则会变成法人判刑证据---------------------------------\r\n\r\n\r\n\r\n\r\n', '2026-02-14 15:07:14', '/api/files/download/24', 24, 'upload', '原创', b'1', b'0', b'0', '员工手册和内部文档', '2026-02-15 01:12:34', '46269', 45, 3, '员工工作绝密文档', 3, '491a03dccee8eb827222ee5acd962435', 0);

-- ----------------------------
-- Table structure for t_blog_tags
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_tags`;
CREATE TABLE `t_blog_tags`  (
  `blogs_id` bigint(0) NOT NULL AUTO_INCREMENT,
  `tags_id` bigint(0) NOT NULL,
  INDEX `FK5feau0gb4lq47fdb03uboswm8`(`tags_id`) USING BTREE,
  INDEX `FKh4pacwjwofrugxa9hpwaxg6mr`(`blogs_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `blog_id` bigint(0) NULL DEFAULT NULL,
  `parent_comment_id` bigint(0) NULL DEFAULT NULL,
  `admin_comment` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2194 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_comment
-- ----------------------------
INSERT INTO `t_comment` VALUES (2193, '泪心', '2254013571@qq.com', '部分功能可能无效，这是预算问题，没有额外资金支持子功能如邮箱服务或redis缓存支持等等。', '/images/avatar.png', '2025-06-29 05:43:39', 94, -1, b'0');

-- ----------------------------
-- Table structure for t_file_upload
-- ----------------------------
DROP TABLE IF EXISTS `t_file_upload`;
CREATE TABLE `t_file_upload`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_filename` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '原始文件名',
  `stored_filename` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件访问URL',
  `file_size` bigint(0) NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件类型/MIME类型',
  `file_extension` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '文件扩展名',
  `upload_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_ip` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '上传者IP',
  `description` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件描述',
  `download_count` int(0) NOT NULL DEFAULT 0 COMMENT '下载次数',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_upload_time`(`upload_time`) USING BTREE,
  INDEX `idx_file_type`(`file_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件上传表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_file_upload
-- ----------------------------
INSERT INTO `t_file_upload` VALUES (7, '环境0907泪心sukisu整合9月份终极版隐藏方案 (1).zip', '6b56b225fb82495fadb88052cbd3e977_1758445933563.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\09\\21\\6b56b225fb82495fadb88052cbd3e977_1758445933563.zip', '/api/files/download/7', 173066863, 'application/x-zip-compressed', 'zip', '2025-09-21 09:12:14', '113.87.38.186', NULL, 0, 0);
INSERT INTO `t_file_upload` VALUES (8, 'ndk安卓.zip', 'ed1cfeac8297498385009da03c42ffd8_1758543629175.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\09\\22\\ed1cfeac8297498385009da03c42ffd8_1758543629175.zip', '/api/files/download/8', 124847281, 'application/zip', 'zip', '2025-09-22 12:20:29', '113.84.81.28', 'ndkollvm17', 14, 0);
INSERT INTO `t_file_upload` VALUES (10, '泪心内存扫码综合工具完全.zip', '95c0547ba2ea4508a3f690f1e0eb0621_1758944310825.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\09\\27\\95c0547ba2ea4508a3f690f1e0eb0621_1758944310825.zip', '/api/files/download/10', 44749983, 'application/x-zip-compressed', 'zip', '2025-09-27 03:38:31', '113.87.36.80', NULL, 5, 0);
INSERT INTO `t_file_upload` VALUES (11, '和平精英0928触摸自瞄版本.zip', 'c2a8cf43f9594dcabbd7e0df806d24e3_1759054448763.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\09\\28\\c2a8cf43f9594dcabbd7e0df806d24e3_1759054448763.zip', '/api/files/download/11', 22207579, 'application/x-zip-compressed', 'zip', '2025-09-28 10:14:09', '113.87.36.80', NULL, 11, 1);
INSERT INTO `t_file_upload` VALUES (12, 'TUSI-Ollvm-ndk-r28c-aide-Magic-Fix.tgz', '962924914f014911a2f565585d479dec_1759118884692.tgz', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\09\\29\\962924914f014911a2f565585d479dec_1759118884692.tgz', '/api/files/download/12', 562330264, 'application/x-compressed', 'tgz', '2025-09-29 04:08:05', '113.87.36.14', NULL, 0, 0);
INSERT INTO `t_file_upload` VALUES (13, 'scrcpy-win64-v3.3.1允许直接控制手机.zip', '9cf8045e7de14dd584cc098145c2ddfb_1760519241858.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\10\\15\\9cf8045e7de14dd584cc098145c2ddfb_1760519241858.zip', '/api/files/download/13', 29506408, 'application/x-zip-compressed', 'zip', '2025-10-15 09:07:22', '113.87.38.139', NULL, 1, 1);
INSERT INTO `t_file_upload` VALUES (14, 'LOL端游插件泪心辅助1027.zip', '5788c3a83b834a178883f7a9ae581b55_1761534136679.zip', 'C:\\Program Files (x86)\\泪心网站搭建\\uploads\\2025\\10\\27\\5788c3a83b834a178883f7a9ae581b55_1761534136679.zip', '/api/files/download/14', 3616590, 'application/x-zip-compressed', 'zip', '2025-10-27 03:02:17', '113.91.145.127', NULL, 2, 1);
INSERT INTO `t_file_upload` VALUES (15, '朵力亚.jpg', '0c1ba7979208403b9d1bf56b32276b89_1761639520836.jpg', 'C:\\Users\\qq523\\AppData\\Local\\Temp\\tomcat.5469742016477550375.80\\webapps\\uploads\\2025\\10\\28\\0c1ba7979208403b9d1bf56b32276b89_1761639520836.jpg', '/api/files/download/15', 1656974, 'image/jpeg', 'jpg', '2025-10-28 08:18:41', '0:0:0:0:0:0:0:1', '博客首图', 5, 1);
INSERT INTO `t_file_upload` VALUES (16, '朵力亚.jpg', 'e0cf8a4219a14900ac2bfbf4a13b594e_1761704161332.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\10\\29\\e0cf8a4219a14900ac2bfbf4a13b594e_1761704161332.jpg', '/api/files/download/16', 1656974, 'image/jpeg', 'jpg', '2025-10-29 02:16:01', '113.91.145.127', '博客首图', 17, 1);
INSERT INTO `t_file_upload` VALUES (17, 'eb65aa2faa5dff78a41e05a3b31baa69_720.png', 'def1b8a8efc1447bb0876188e1b960e0_1763001457670.png', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\11\\13\\def1b8a8efc1447bb0876188e1b960e0_1763001457670.png', '/api/files/download/17', 173776, 'image/png', 'png', '2025-11-13 02:37:38', '113.91.146.177', '博客首图', 0, 1);
INSERT INTO `t_file_upload` VALUES (18, '泪心图.jpg', 'aa0a88c5ba414657a4c57373ba7de25e_1763001627197.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\11\\13\\aa0a88c5ba414657a4c57373ba7de25e_1763001627197.jpg', '/api/files/download/18', 1289335, 'image/jpeg', 'jpg', '2025-11-13 02:40:27', '113.91.146.177', '博客首图', 7, 1);
INSERT INTO `t_file_upload` VALUES (19, '泪心PC端魔法工具.zip', '5a12bb8947f54e559ee600134ec942df_1763458945402.zip', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\11\\18\\5a12bb8947f54e559ee600134ec942df_1763458945402.zip', '/api/files/download/19', 38138468, 'application/x-zip-compressed', 'zip', '2025-11-18 09:42:26', '113.91.146.153', NULL, 5, 0);
INSERT INTO `t_file_upload` VALUES (20, 'ThereisAGirl.jpg', '541f78470e32490c8e3335bdb2062438_1766920951673.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\12\\28\\541f78470e32490c8e3335bdb2062438_1766920951673.jpg', '/api/files/download/20', 209310, 'image/jpeg', 'jpg', '2025-12-28 11:22:32', '113.87.36.93', '博客首图', 4, 1);
INSERT INTO `t_file_upload` VALUES (21, 'TearMyBlog.jar', 'b17e83dbdd684a9e8851d94e551d9d55_1767104489640.jar', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.16532713260665637335.80\\webapps\\uploads\\2025\\12\\30\\b17e83dbdd684a9e8851d94e551d9d55_1767104489640.jar', '/api/files/download/21', 172978217, 'application/octet-stream', 'jar', '2025-12-30 14:21:30', '113.87.36.93', NULL, 0, 1);
INSERT INTO `t_file_upload` VALUES (22, 'BGFlower.jpg', '3e995531326848fda2dac0d8d66dcfdb_1771081492755.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.9252016329083138837.80\\webapps\\uploads\\2026\\02\\14\\3e995531326848fda2dac0d8d66dcfdb_1771081492755.jpg', '/api/files/download/22', 179970, 'image/jpeg', 'jpg', '2026-02-14 15:04:53', '117.169.196.21', '博客首图', 1, 1);
INSERT INTO `t_file_upload` VALUES (23, 'Hacker.jpg', '90fc51160b474a7595bf4a672a946e96_1771081507227.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.9252016329083138837.80\\webapps\\uploads\\2026\\02\\14\\90fc51160b474a7595bf4a672a946e96_1771081507227.jpg', '/api/files/download/23', 1193051, 'image/jpeg', 'jpg', '2026-02-14 15:05:07', '117.169.196.21', '博客首图', 1, 0);
INSERT INTO `t_file_upload` VALUES (24, '刻晴.jpg', '72d2c1b5777c4a299b6801a244c1710a_1771081567643.jpg', 'C:\\Users\\Administrator\\AppData\\Local\\Temp\\1\\tomcat.9252016329083138837.80\\webapps\\uploads\\2026\\02\\14\\72d2c1b5777c4a299b6801a244c1710a_1771081567643.jpg', '/api/files/download/24', 1605126, 'image/jpeg', 'jpg', '2026-02-14 15:06:08', '117.169.196.21', '博客首图', 22533, 0);

-- ----------------------------
-- Table structure for t_friend
-- ----------------------------
DROP TABLE IF EXISTS `t_friend`;
CREATE TABLE `t_friend`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `blogaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `blogname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `pictureaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_memory
-- ----------------------------
DROP TABLE IF EXISTS `t_memory`;
CREATE TABLE `t_memory`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `picture_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `memory` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `parent_message_id` bigint(0) NULL DEFAULT NULL,
  `admin_message` bit(1) NOT NULL,
  `parent_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8133 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (8132, '泪心', '2254013571@qq.com', '曾深深痴迷不愿睡醒的梦,怎就突然一朝醒来了？', '/images/avatar.png', '2025-06-29 05:45:12', -1, b'0', NULL);

-- ----------------------------
-- Table structure for t_picture
-- ----------------------------
DROP TABLE IF EXISTS `t_picture`;
CREATE TABLE `t_picture`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `pictureaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturedescription` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturename` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturetime` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_resources
-- ----------------------------
DROP TABLE IF EXISTS `t_resources`;
CREATE TABLE `t_resources`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `resource_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `resource_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `first_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `second_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picture_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `resource_description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `sort` int unsigned NULL,
  `published` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 473 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_resources
-- ----------------------------
INSERT INTO `t_resources` VALUES (472, '泪心自叙', '', '梦一场', '虚拟现实', 'https://gitee.com/thoughtful123/tearyoByCompanyLife/raw/master/photosuse/bg33.jpg', '关于生活中的经历以及一些想说的话', NULL, 1, 1);

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_type
-- ----------------------------
DROP TABLE IF EXISTS `t_type`;
CREATE TABLE `t_type`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 47 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_type
-- ----------------------------
INSERT INTO `t_type` VALUES (43, '泪心叙事、人生圆缺');
INSERT INTO `t_type` VALUES (44, '泪心技术、经验分享');
INSERT INTO `t_type` VALUES (45, '蕴宇科技有限责任公司');
INSERT INTO `t_type` VALUES (46, '一念正邪');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type` int(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (3, '/images/avatar.png', '2025-06-27 21:36:00', '51561@qq.com', 'tear', '3950db3179acc96ee41b2d111c785c58', 0, '2025-06-27 21:36:17', 'tear');

SET FOREIGN_KEY_CHECKS = 1;
