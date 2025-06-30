/*
 Navicat Premium Dump SQL

 Source Server         : 泪心
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : myblog

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 30/06/2025 10:05:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hibernate_sequence
-- ----------------------------
DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of hibernate_sequence
-- ----------------------------

-- ----------------------------
-- Table structure for t_blog
-- ----------------------------
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appreciation` bit(1) NOT NULL,
  `commentabled` bit(1) NOT NULL,
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `first_picture` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `flag` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `published` bit(1) NOT NULL,
  `recommend` bit(1) NOT NULL,
  `share_statement` bit(1) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `views` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type_id` bigint NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `comment_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK292449gwg5yf7ocdlmswv9w4j`(`type_id`) USING BTREE,
  INDEX `FK8ky5rrsxh01nkhctmo7d48p82`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 95 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_blog
-- ----------------------------
INSERT INTO `t_blog` VALUES (93, b'0', b'1', '# 序言\r\n**如果您能看完关于泪心的全文介绍,我将非常感谢,不看也没关系,这只是作者对于自己青春的自我叙述。**  **<font color=\"darkred\">也许你很想知道，明明大型平台这么多，为什么非要自己搭建个平台发布技术交流。原因很简单，因为技术限流，风险提示。只好自己搭建全新平台咯。非要一句话概括,实乃天下熙攘皆为利往。我不给平台打钱,它为啥不封禁我呢？</font>**\r\n先介绍一下，我是泪心，一名互联网上资深独立自由软件开发员。为什么要带着\"资深\"呢？这跟我大学期间胡乱碰撞有关，听说那个技术好那个技术能立马获取成就感就去学那个，最后发现自己浪费了年华结果一事无成。虽说如此，但是我大学期间的确是让我把全部编程语言入门了一次。\r\n<br>\r\n为什么要叫泪心呢,现在2025年则是满满的失意,对自己对外面对世界全是失意,一种说不上来的余生孤独感，仿若存在就像是个固定的NPC找不到自己的归属地。或许，“泪心”本来就是以泪洗面吧？也许,我已经失去了爱,生活已经逼得我披上了一层保护自己的无痕泡沫。\r\n\r\n---\r\n\r\n\r\n## 主题一：失意迷乱，身心俱疲\r\n\r\n沉默，是保护自己；  \r\n安静，是守护自己；  \r\n出品，是表现自己；  \r\n低调，是护全自己；  \r\n隐忍，是成就自己；  \r\n欣慰，是获取赞美。  \r\n\r\n世间利，人间利，本是利，既无利，何以为生？我亦不愿如此，可恨人世间就是一场盛大的虚拟游戏。爱恨离愁都是感受到感受不到的无关缓存，只是碎裂了套娃世界的中枢服务器，却导致了某个对象独立内存的溢出毁灭式崩溃。\r\n\r\n你活着，你要卑微的活着，你要去顺应你看得见的黑暗，你要拥抱你看得见的光明，然而操控者却非让你在黑暗与光明中反复融合出全新的结界。人与人之间的分离、兵变，只是我们选择了不同的人生和不同的际遇，不论结果如何都是一份完整的满分答卷。再次我默默的致谢：不用难过，我们都没错，好好活下去终有一天会发现自己存在的意义。\r\n\r\n---\r\n\r\n## 主题二：光芒一时，紊乱一期\r\n\r\n2017年，我误入了身心奴虐的地域，好比把我丢入了暮气沉沉的JSP框架程序中自生自灭。  \r\n2020年，我选择了流行光鲜无忧无虑的全新区域，开开心心好似对着Java世界第一次输出\"hello,world\"的新奇。  \r\n\r\n之后的学生生涯中：  \r\n- 从web安全研究到web项目开发  \r\n- 转到PC端安全研究  \r\n- PC端驱动开发  \r\n- PC端逆向研发  \r\n- 某次机缘下成为安卓C++底层世界开发的一粒微尘  \r\n\r\n---\r\n## 主题三：世界本暗，持汝初心\r\n\r\n不知不觉间，我已经在茫茫代码领域中发展成为了商业开发的一员，不知不觉已经成为了互联网独立开发程序员，有着自己的生活区域。我只是在漫长的迁跃中，走出了某条适应于自我的一种道路。对你而言可能错了，但是选择从来不分对错，结果更是不分好坏，只是命运的棋子悄然变动了。\r\n\r\n我的初心是美好且伟大的。或许每个大学生都怀有悬壶济世、热血江山的梦想，只是在社会摸爬滚打中渐渐认清了冰冷彻骨的现实。在我还没有接触代码的时候，为了避免\"穷人自有恶人磨\"的寒苦生活现状，我持续生活在别人设定的虚拟假象世界中（游戏、小说、电视）。甚至严重到了修仙成佛的地步，说来也搞笑，但这些都是曾经抓得住的切片记忆。\r\n\r\n我有一个梦想：\r\n- 让全世界所有人幸福安康，让所有苦难都消融，让微笑快乐常挂身边，以热爱快乐面对每一天的全新生活\r\n- 让冷漠被热情替代，让寒冰坚雪融化，让世间苦难不再持续发生，找回儿童真挚的笑容，重拾\"自由快乐平等\"\r\n- 既然所有的商业物品都是披着羊皮的狼，那就让全世界所有的商品都回归原始真实状态，让消费者安心舒适的选购\r\n- 让竞争不再头破血流，无论任何事，都可以平常心、同等心静静解决\r\n- 掌控江山，成为异界之神，坐拥亿万生灵，尊享华夏庙堂\r\n\r\n理想化丰满化的虚拟生活只存在于游戏中、影视中、虚拟中。人血馒头的残酷现实扼杀了全部的梦。\r\n\r\n我的初心是什么？只是想真诚实在、热心助人、平等对待，让风范从课本中走出来，有错吗？\r\n\r\n是的，大错特错：\r\n- 因为课本全是抽象化的错误式儒学奴化教育，目的就是为了掌控廉洁高质量的年轻劳动力\r\n- 因为让真实彻底走到明面上，将会剥夺大量行业的生存之道，甚至直接击垮整个社会\r\n- 本来，真真假假虚实不分，善恶交融有序离场，好坏不分才能正常运转。人世间的运转本就是复杂多样且不可预测的\r\n\r\n世界本就黑暗，但请保持你的初心。\r\n\r\n\r\n## 主题四:技术的永恒悖论(独立编程者自省)\r\n> \"别以为站在技术巅峰就能永恒——今天的屠龙者，注定是明天被AI和更年轻大脑淘汰的\'古典程序员\'。\"--泪心\r\n\r\n作为一名在网络安全与逆向工程领域深耕多年的技术从业者，我经历了从入门到精通的完整成长轨迹。这段旅程带给我的不仅是技术能力的提升，更引发了对技术本质的深刻思考。\r\n\r\n### 技术的双面性\r\n\r\n在这个日新月异的数字时代，技术就像一把双刃剑。我们引以为傲的代码成果，那些熬夜攻克的技术难关，很可能在未来成为颠覆我们自身的武器。这种宿命般的循环，正是技术从业者面临的最大悖论。\r\n\r\n从最初转发技术论坛内容，到后来能够独立开发复杂的逆向工具，我深刻体会到技术迭代的无情。那些曾经让我兴奋不已的技术突破，如今看来不过是技术长河中的一朵浪花。\r\n\r\n### 商业与理想的平衡\r\n\r\n随着技术能力的提升，我不得不面对一个现实问题：如何平衡技术理想与商业需求？从完全无偿的技术分享，到建立可持续的商业模式，这个过程充满挑战与反思。\r\n\r\n我始终坚持一个原则：即使在商业化运作中，也要保留技术的普惠性。每个项目都设有免费与付费版本的明确区分，这不仅是一种商业策略，更是对技术初心的坚守。\r\n\r\n### 给后来者的建议\r\n\r\n看着新一代技术爱好者涌入这个领域，我仿佛看到了当年的自己。在此，我想分享几点感悟：\r\n\r\n1. **技术会过时，但解决问题的能力永不过时**\r\n2. **保持学习热情比掌握特定技术更重要**\r\n3. **在追求技术深度的同时，不要忽视技术伦理**\r\n\r\n在这个快速变化的时代，我们或许无法永远站在技术前沿，但可以培养超越技术本身的思维方式。这才是技术从业者真正的核心竞争力。\r\n\r\n技术之路没有终点，只有不断的自我超越与反思。愿我们都能在技术探索中，找到属于自己的平衡点。\r\n\r\n很可惜,你们跟我走的是同一条路,只注重实战项目,只看最终结果。虽然说社会就是需要这样的人才,公司就是需要这样的员工。\r\n但是这样写代码写久了后,你会发现自己在某个方向渐行渐远,甚至分不清什么是虚拟什么是现实,简单点说就是空荡荡的。', '2025-06-28 13:04:31', '/images/bg2.jpg', '原创', b'0', b'1', b'0', '泪心的由来，一条充满复杂色的成长道路', '2025-06-29 03:05:04', '14', 43, 3, '泪心,似乎是一个脆弱的标识。或许立名的开始只是因为那个“她”，后面却成为了悲剧中的那个“它”，明知道自己陷入了不归的泥炭沼泽，却再也无法跳出手敲键盘跳出代码的虚拟世界了。', 0);
INSERT INTO `t_blog` VALUES (94, b'1', b'1', '# 个人博客系统技术概要\r\n## 序言\r\n很多时候，要的只是结果，而不是你累死累活的过程，资本化的快速商业战，是完全不需要过程，只要你最终给出完美的答卷就行。所以请不要在乎我利用了人工智能，如网络上说有工具不用非要手动“没苦硬吃”那是傻逼。\r\n## 项目概述\r\n本系统是基于SpringBoot+MyBatis的轻量级个人博客平台，采用前后端分离架构设计，具备文章管理、分类归档、互动留言、多媒体展示等完整博客功能。前端融合Semantic UI框架与多款JavaScript插件实现优雅交互，后端依托SpringBoot生态实现高效开发。--技术详解非常细致哦<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n---\r\n## 项目代码框架分析\r\n    src/main/java\r\n    ├─com.star\r\n      ├─config          // 配置类\r\n      ├─controller      // 控制层\r\n      ├─service         // 业务层\r\n         ├─impl         // 实现类\r\n      ├─dao             // 持久层接口\r\n      ├─entity          // 实体类\r\n      ├─interceptor     // 拦截器\r\n      ├─handle       // 异常处理\r\n      ├─util            // 工具类\r\n    resources\r\n      ├─templates       // 视图模板\r\n      ├─static          // 静态资源 含泪心自加资源\r\n\r\n## 技术架构概览\r\n### 前端技术栈\r\n| 模块 | 技术实现 |\r\n|------|----------|\r\n| **核心框架** | jQuery + Semantic UI |\r\n| **富文本编辑** | Markdown编辑器 |\r\n| **代码高亮** | prism.js |\r\n| **动态效果** | animate.css 动画库 |\r\n| **文章导航** | Tocbot目录生成器 | \r\n| **音乐播放** | zPlayer音乐播放器 |\r\n| **图片展示** | lightbox.js照片墙插件 |\r\n### 后端技术栈\r\n| 模块 | 技术组件 |\r\n|------|----------|\r\n| **核心框架** | SpringBoot 2.2.5 |\r\n| **ORM框架** | MyBatis |\r\n| **模板引擎** | Thymeleaf |\r\n| **分页处理** | PageHelper分页插件 |\r\n| **安全加密** | MD5密码加密 |\r\n| **项目构建** | Maven 3.x + JDK 8+ |\r\n### 数据库\r\n- **版本**: MySQL 8.x\r\n- **连接池**: HikariCP (SpringBoot默认)\r\n---\r\n### 核心数据表说明\r\n| 数据表       | 功能描述                          |\r\n|--------------|---------------------------------|\r\n| `t_blog`     | 存储文章主体内容及元数据           |\r\n| `t_type`     | 文章分类标签目录                  |\r\n| `t_user`     | 系统用户信息（含管理员）           |\r\n| `t_comment`  | 文章评论数据                     |\r\n| `t_message`  | 留言板消息记录                   |\r\n| `t_friend`   | 友情链接资源池                   |\r\n| `t_picture`  | 相册图片及元数据                 |\r\n\r\n\r\n<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n<!--泪心原创 @TearGame QQ2254013571 禁止侵权-->\r\n# 博客系统功能说明\r\n\r\n## 用户端功能\r\n\r\n### 1. 内容浏览功能\r\n- **文章展示**\r\n  - 支持文章列表分页展示\r\n  - 提供文章详情阅读页面\r\n  - 自动记录文章访问量\r\n\r\n- **内容检索**\r\n  - 按分类浏览（技术/生活/随笔等）\r\n  - 标签云导航\r\n  - 支持关键词搜索\r\n\r\n### 2. 多媒体功能\r\n- **音乐播放**\r\n  - 支持播放/暂停/切歌\r\n  - 显示同步歌词\r\n  - 音量调节控制\r\n\r\n- **相册展示**\r\n  - 图片瀑布流布局\r\n  - 点击放大查看\r\n  - 显示照片拍摄信息\r\n\r\n### 3. 互动功能\r\n- **评论系统**\r\n  - 支持留言回复\r\n  - 区分普通用户和管理员\r\n  - 表情符号支持\r\n\r\n- **友情链接**\r\n  - 合作伙伴展示\r\n  - 链接点击统计\r\n  - 友链申请入口\r\n\r\n## 管理端功能\r\n\r\n### 1. 内容管理\r\n- **文章管理**\r\n  - 新增/编辑/删除文章\r\n  - Markdown编辑器\r\n  - 草稿自动保存\r\n\r\n- **分类管理**\r\n  - 多级分类设置\r\n  - 分类排序调整\r\n  - 关联文章统计\r\n\r\n### 2. 消息管理\r\n- **评论审核**\r\n  - 新评论提醒\r\n  - 敏感词过滤\r\n  - 批量处理功能\r\n\r\n- **通知系统**\r\n  - 站内消息提醒\r\n  - 邮件通知设置\r\n  - 消息标记已读\r\n\r\n### 3. 系统安全\r\n- **登录验证**\r\n  - 账号密码登录\r\n  - 密码加密存储\r\n  - 登录日志记录\r\n\r\n- **权限控制**\r\n  - 管理后台隔离\r\n  - 操作日志追踪\r\n  - 异常登录检测\r\n\r\n> 部署环境要求：\r\n> - JDK 17+\r\n> - MySQL 8.0+\r\n> - Redis 6.0+\r\n\r\n> 开发进度：\r\n> - 用户端：基本完成\r\n> - 管理端：主要功能完成\r\n\r\n## 代码开发重难点分析\r\n### MD5加密工具类说明\r\n\r\n#### 功能图解\r\n![MD5加密流程示意图](/images/myblog/md5code.png)  \r\n\r\n*图：MD5加密算法处理流程*\r\n\r\n\r\n```java\r\n/**		md5不可逆加密核心代码实现\r\n * MD5加密工具类\r\n */\r\npublic class MD5Utils {\r\n    /**\r\n     * 生成32位MD5哈希值\r\n     * @param str 原始字符串\r\n     * @return 32位小写MD5值\r\n     */\r\n    public static String code(String str) {\r\n        try {\r\n            MessageDigest md = MessageDigest.getInstance(\"MD5\");\r\n            byte[] digest = md.digest(str.getBytes(StandardCharsets.UTF_8));\r\n            StringBuilder hexString = new StringBuilder();\r\n            for (byte b : digest) {\r\n                String hex = Integer.toHexString(0xff & b);\r\n                if(hex.length() == 1) {\r\n                    hexString.append(\'0\');\r\n                }\r\n                hexString.append(hex);\r\n            }\r\n            return hexString.toString();\r\n        } catch (NoSuchAlgorithmException e) {\r\n            throw new RuntimeException(\"MD5 algorithm not found\", e);\r\n        }\r\n    }\r\n}\r\n```\r\n\r\n> 根据泪心分析 这样一加密后意味着存储在数据库的密码是会被md5加密初始化的，什么意思呢，就是说只要你调用这个函数，你查找账号密码，那么这个密码必定是经过了md5加密混淆。因此如果你数据库上面的密码是admin,但是你在网页输入的是admin，结果自然而然就不通过了。因为这个是直接查询你数据库的密码，然后网页层面已经经过了md5混淆加密，跟你输入的值完全不对等了。<br>那么如果说我实在想用怎么办呢，方法一,直接加密对应密码生成后的md5数据，把数据载入数据库对应密码中。方法二，实现用户输入注册生成已加密的数据并自动载入数据库。方法二虽好但是必须做足安全性，否则很容易被人恶意调试直接拿到后端管理员账号密码。\r\n\r\n```java\r\n@Service\r\npublic class UserServiceImpl implements UserService {\r\n\r\n    @Autowired\r\n    private UserDao userDao;\r\n\r\n    /**\r\n     * @Description: 业务逻辑层代码实现\r\n     * @Auther: 泪心\r\n     * @Date: 21:25 2025/6/29\r\n     * @Param: username:用户名；password:密码\r\n     * @Return: 返回用户对象\r\n     */\r\n    @Override\r\n    public User checkUser(String username, String password) {\r\n        User user = userDao.findByUsernameAndPassword(username, MD5Utils.code(password));\r\n        return user;\r\n    }\r\n}\r\n```\r\n### 全局异常页面url处理\r\n```java\r\n// GlobalExceptionHandler.java\r\n@ControllerAdvice\r\npublic class GlobalExceptionHandler {\r\n    // 处理404等未匹配路径\r\n    @ExceptionHandler(NoHandlerFoundException.class)\r\n    public String handle404() {\r\n        return \"redirect:/error\";\r\n    }\r\n    // 统一错误页面\r\n    @GetMapping(\"/error\")\r\n    public String errorPage() {\r\n        return \"error\"; // templates/error.html\r\n    }\r\n```\r\n\r\n### 管理员权限拦截器实现\r\n```java\r\n// AuthInterceptor.java\r\npublic class AuthInterceptor implements HandlerInterceptor {\r\n    @Override\r\n    public boolean preHandle(HttpServletRequest request, \r\n                            HttpServletResponse response, \r\n                            Object handler) throws Exception {\r\n        // 检查登录状态（示例Session实现）\r\n        HttpSession session = request.getSession();\r\n        String username = (String) session.getAttribute(\"currentUser\");\r\n        // 排除登录接口\r\n        String uri = request.getRequestURI();\r\n        if(uri.contains(\"/login\") || uri.contains(\"/static\")){\r\n            return true;\r\n        }\r\n        // 未登录用户重定向\r\n        if(username == null || username.isEmpty()){\r\n            response.sendRedirect(\"/admin/login\");\r\n            return false;\r\n        }\r\n        // 管理员权限校验（扩展点）\r\n        return checkAdminRole(username); \r\n    }\r\n}\r\n// WebMvcConfig.java\r\n@Configuration\r\npublic class WebMvcConfig implements WebMvcConfigurer {\r\n    @Override\r\n    public void addInterceptors(InterceptorRegistry registry) {\r\n        registry.addInterceptor(new AuthInterceptor())\r\n                .addPathPatterns(\"/admin/**\")        // 拦截后台路径\r\n                .excludePathPatterns(\"/admin/login\");// 放行登录入口\r\n```\r\n\r\n### 分页插件PageHelper使用\r\n```java \r\n  //分页查询博客列表\r\n    @GetMapping(\"/\")\r\n    public String index(Model model, @RequestParam(defaultValue = \"1\",value = \"pageNum\") Integer pageNum, RedirectAttributes attributes){\r\n        PageHelper.startPage(pageNum,10);\r\n        //查询博客列表\r\n        List<FirstPageBlog> allFirstPageBlog = blogService.getAllFirstPageBlog();\r\n        //查询最新推荐博客\r\n        List<RecommendBlog> recommendedBlog = blogService.getRecommendedBlog();\r\n        //查询最新评论\r\n        List<NewComment> newComments = blogService.getNewComment();\r\n\r\n        PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(allFirstPageBlog);   //博客内容实体类  定义泛型类对象获取查询后的博客List列表\r\n        model.addAttribute(\"pageInfo\",pageInfo);  //页面信息传输到前端\r\n        model.addAttribute(\"recommendedBlogs\", recommendedBlog);  //推荐博客传输到前端\r\n        model.addAttribute(\"newComment\",newComments);  //评论\r\n        return \"index\";\r\n    }\r\n```\r\n\r\n```java\r\n@Controller\r\npublic class TypeShowController {\r\n\r\n    @Autowired\r\n    private TypeService typeService;   //分类实现接口\r\n\r\n    @Autowired\r\n    private BlogService blogService;   //博客实现接口\r\n\r\n    //    分页查询分类\r\n    @GetMapping(\"/types/{id}\")\r\n    public String types(@RequestParam(defaultValue = \"1\",value = \"pageNum\") Integer pageNum, @PathVariable Long id, Model model) {\r\n        List<Type> types = typeService.getAllTypeAndBlog();\r\n\r\n        //id为-1表示从首页导航栏点击进入分类页面\r\n        if (id == -1) {\r\n            if(!types.isEmpty()){\r\n                id = types.get(0).getId();\r\n            }\r\n        }\r\n        model.addAttribute(\"types\", types);\r\n        List<FirstPageBlog> blogs = blogService.getByTypeId(id);  //查询分类返回博客List列表对象\r\n\r\n        PageHelper.startPage(pageNum, 10000);\r\n        PageInfo<FirstPageBlog> pageInfo = new PageInfo<>(blogs);\r\n        model.addAttribute(\"pageInfo\", pageInfo);   //把查询到的博客泛型类对象（list列表）全部加载传输到前端\r\n        model.addAttribute(\"activeTypeId\", id);  //特别标准传输分类ID标签\r\n        return \"types\";\r\n    }\r\n\r\n}\r\n```\r\n```html\r\n<!-- 分类列表容器 -->\r\n<div class=\"ui vertical menu\">\r\n  <!-- 遍历后端传来的types集合 -->\r\n  <div th:each=\"type : ${types}\" class=\"item\">\r\n    <!-- \r\n      点击分类时跳转到/type/{id}路由\r\n      后端交互：向后台发送带分类ID的GET请求\r\n      高亮逻辑：当前分类ID等于activeTypeId时添加active类\r\n    -->\r\n    <a th:href=\"@{/type/{id}(id=${type.id})}\" \r\n       th:text=\"${type.name}\"\r\n       th:classappend=\"${type.id == activeTypeId} ? \'active\'\">\r\n      默认分类\r\n    </a>\r\n  </div>\r\n</div>\r\n```\r\n```html\r\n<!-- \r\n  主内容区 - 显示筛选后的博客\r\n  数据来源：后端返回的pageInfo.list集合\r\n-->\r\n<div th:each=\"blog : ${pageInfo.list}\" class=\"blog-item\">\r\n  <h3 th:text=\"${blog.title}\"></h3>\r\n  \r\n  <!-- \r\n    显示该博客所属分类 \r\n    数据来源：后端在查询博客时联表查询type.name\r\n  -->\r\n  <div class=\"type-tag\" th:text=\"${blog.typeName}\"></div>\r\n  \r\n  <!-- 其他博客内容... -->\r\n</div>\r\n```\r\n```html\r\n<!-- \r\n  分页栏 - 关键点：翻页时保持当前分类\r\n  后端交互：分页链接始终携带当前activeTypeId\r\n-->\r\n<div class=\"pagination\">\r\n  <!-- 上一页 -->\r\n  <a th:href=\"@{/type/{id}(id=${activeTypeId}, pageNum=${pageInfo.prePage})}\" \r\n     th:unless=\"${pageInfo.isFirstPage}\">\r\n    上一页\r\n  </a>\r\n  \r\n  <!-- 页码显示 -->\r\n  <span th:text=\"${pageInfo.pageNum + \'/\' + pageInfo.pages}\"></span>\r\n  \r\n  <!-- 下一页 -->\r\n  <a th:href=\"@{/type/{id}(id=${activeTypeId}, pageNum=${pageInfo.nextPage})}\" \r\n     th:unless=\"${pageInfo.isLastPage}\">\r\n    下一页\r\n  </a>\r\n</div>\r\n```\r\n\r\n### SpringBoot核心配置文件YML\r\n1. 基本语法要求\r\n缩进规则：必须使用 2个空格 作为缩进层级（禁止使用 Tab 键）\r\n\r\n键值分隔：冒号 : 后必须加 1个空格（如 key: value）\r\n\r\n大小写敏感：配置项推荐全小写，单词间用下划线（如 max_retry_count）\r\n\r\n注释：使用 # 开头，后接1个空格（如 # 这是注释）\r\n\r\n2. 多环境配置\r\n使用 --- 分隔不同环境的配置\r\n\r\n通过 spring.profiles.active 指定当前激活的环境\r\n当然了 你可以根据jar包运行时候指定环境状态,如下所示\r\n```\r\n# 开发环境启动命令：\r\n#   java -jar your-app.jar --spring.profiles.active=dev\r\n# \r\n# 生产环境启动命令：\r\n#   java -jar your-app.jar --spring.profiles.active=pro\r\n```\r\n这是一个完整的本项目配置部署文件示范例子\r\n```yaml\r\n# ========================================================\r\n#                Spring Boot 通用配置文件\r\n# 特点：\r\n# 1. 不分 dev/pro 环境，所有配置直接生效\r\n# 2. 敏感信息通过环境变量注入（如数据库密码）\r\n# 3. 提供合理的默认值，保证直接启动不报错\r\n# ========================================================\r\n\r\n# #################### 核心框架配置 ####################\r\nspring:\r\n  # Thymeleaf 模板引擎\r\n  thymeleaf:\r\n    mode: HTML\r\n\r\n  # 数据源配置（默认使用本地数据库，可通过环境变量覆盖）\r\n  datasource:\r\n    driver-class-name: com.mysql.cj.jdbc.Driver\r\n    url: jdbc:mysql://${DB_HOST:localhost}:3306/${DB_NAME:myblog}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC\r\n    username: ${DB_USER:root}       # 默认用户名，可用环境变量覆盖\r\n    password: ${DB_PASSWORD:admin} # 默认密码，生产环境务必通过环境变量修改！\r\n    hikari:\r\n      max-lifetime: 500000\r\n\r\n  # 邮件服务（默认关闭，需主动配置）\r\n  mail:\r\n    host: ${MAIL_HOST:smtp.163.com}\r\n    username: ${MAIL_USER:}         # 默认空，避免误发邮件\r\n    password: ${MAIL_PASSWORD:}\r\n    default-encoding: utf-8\r\n    protocol: smtp\r\n    port: 25\r\n    properties:\r\n      mail.smtp.starttls.enable: true\r\n\r\n  # Redis（默认连接本地，可环境变量覆盖）\r\n  redis:\r\n    host: ${REDIS_HOST:127.0.0.1}\r\n    port: ${REDIS_PORT:6379}\r\n    database: 0\r\n    timeout: 1800000\r\n\r\n  resources:\r\n    static-locations: classpath:/static/\r\n\r\n# #################### MyBatis 配置 ####################\r\nmybatis:\r\n  type-aliases-package: com.star.entity\r\n  mapper-locations: classpath:mapper/*.xml\r\n  configuration:\r\n    map-underscore-to-camel-case: true\r\n\r\n# #################### 日志配置 ####################\r\nlogging:\r\n  level:\r\n    root: info      # 默认日志级别\r\n    com.star: debug # 项目代码详细日志\r\n  file:\r\n    name: log/blog.log  # 统一日志路径\r\n\r\n# #################### 自定义配置 ####################\r\ncomment.avatar: /images/avatar.png\r\nmessage.avatar: /images/avatar.png\r\n```\r\n\r\n\r\n## 项目部署实战\r\n### MyBlog 开源博客项目部署指南\r\n#### 环境要求\r\n- JDK 8+\r\n- Maven 3.6+\r\n- MySQL 5.7+\r\n- Git\r\n- IDE工具（IntelliJ IDEA）\r\n- 数据库工具（Navicat）\r\n---\r\n### 部署步骤\r\n#### 1. 克隆项目源码(可能需要科学魔法上网)\r\n```javascript\r\ngit clone https://github.com/tearhacker/myblogTear.git\r\n```\r\n---\r\n### 2. 导入数据库\r\n1. 使用Navicat创建新数据库（建议名称为`myblog`）\r\n2. 右键新建的数据库 -> 选择「运行SQL文件」\r\n3. 选择项目中的`/sql/myblog.sql`文件导入\r\n4. 确认导入成功后验证表结构是否生成\r\n---\r\n### 3. 项目配置\r\n1. 使用IDEA打开项目：\r\n   - 选择 `File > Open` \r\n   - 定位到项目根目录下的 `pom.xml` 文件\r\n   - 选择「Open as Project」\r\n2. 修改配置文件：\r\n   - 打开 `src/main/resources/application.yml`\r\n   - 修改数据库连接配置\r\n   - 修改redis配置\r\n   - 修改其他配置\r\n---\r\n### 4. 启动应用\r\n1. 找到主启动类：\r\n   - 通常位于 `src/main/java/com/star/myblog/MyBlogApplication.java`\r\n2. 运行启动类：\r\n   - 右键选择「Run MyBlogApplication」\r\n   - 或通过Maven命令：\r\n3. 验证启动：\r\n   - 控制台输出 `Started MyBlogApplication in X.XXX seconds` \r\n   - 访问 `http://localhost:8080`（端口以实际配置为准）\r\n---\r\n## 常见问题\r\n1. **数据库连接失败**\r\n   - 检查yml配置中的用户名/密码\r\n   - 确认MySQL服务已启动\r\n2. **端口冲突**\r\n   - 在application.yml中修改`server.port`\r\n3. **依赖下载缓慢**\r\n   - 更换Maven镜像源至阿里云\r\n4. **新人求助**\r\n   - 提供一切帮助,请联系泪心(有偿服务)\r\n## 可能用上的外链资源\r\n1. **音乐外链解析**\r\n   - <a href=\"https://api.toubiec.cn/wyapi.html\">免费解析音乐</a>\r\n2. **泪心游戏禁地**\r\n   - <a href=\"http://t.me/TearGame\">泪心TG电报频道(闲人勿扰,乱汝心智)</a>\r\n   \r\n\r\n\r\n', '2025-06-29 02:21:56', '/images/myblog/blogInexSHow.png', '原创', b'0', b'1', b'0', 'MYBLOG个人博客系统搭建和技术要点分析', '2025-06-30 02:05:17', '44', 44, 3, 'SpringBoot技术关于泪心myblog(原一颗星)个人博客项目的完整见闻,这应该是第n次搭建了,不同的是这次是完全独立分析和自主研学的。欢迎大家点评和提出更多的意见，感谢大家。', 1);

-- ----------------------------
-- Table structure for t_blog_tags
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_tags`;
CREATE TABLE `t_blog_tags`  (
  `blogs_id` bigint NOT NULL AUTO_INCREMENT,
  `tags_id` bigint NOT NULL,
  INDEX `FK5feau0gb4lq47fdb03uboswm8`(`tags_id`) USING BTREE,
  INDEX `FKh4pacwjwofrugxa9hpwaxg6mr`(`blogs_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = FIXED;

-- ----------------------------
-- Records of t_blog_tags
-- ----------------------------

-- ----------------------------
-- Table structure for t_comment
-- ----------------------------
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `blog_id` bigint NULL DEFAULT NULL,
  `parent_comment_id` bigint NULL DEFAULT NULL,
  `admin_comment` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2194 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_comment
-- ----------------------------
INSERT INTO `t_comment` VALUES (2193, '泪心', '2254013571@qq.com', '部分功能可能无效，这是预算问题，没有额外资金支持子功能如邮箱服务或redis缓存支持等等。', '/images/avatar.png', '2025-06-29 05:43:39', 94, -1, b'0');

-- ----------------------------
-- Table structure for t_friend
-- ----------------------------
DROP TABLE IF EXISTS `t_friend`;
CREATE TABLE `t_friend`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blogaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `blogname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `pictureaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 92 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_friend
-- ----------------------------

-- ----------------------------
-- Table structure for t_memory
-- ----------------------------
DROP TABLE IF EXISTS `t_memory`;
CREATE TABLE `t_memory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `create_time` datetime NULL DEFAULT NULL,
  `picture_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `memory` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_memory
-- ----------------------------

-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `parent_message_id` bigint NULL DEFAULT NULL,
  `admin_message` bit(1) NOT NULL,
  `parent_email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8133 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_message
-- ----------------------------
INSERT INTO `t_message` VALUES (8132, '泪心', '2254013571@qq.com', '曾深深痴迷不愿睡醒的梦,怎就突然一朝醒来了？', '/images/avatar.png', '2025-06-29 05:45:12', -1, b'0', NULL);

-- ----------------------------
-- Table structure for t_picture
-- ----------------------------
DROP TABLE IF EXISTS `t_picture`;
CREATE TABLE `t_picture`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pictureaddress` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturedescription` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturename` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picturetime` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_picture
-- ----------------------------

-- ----------------------------
-- Table structure for t_resources
-- ----------------------------
DROP TABLE IF EXISTS `t_resources`;
CREATE TABLE `t_resources`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `resource_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `first_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `second_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `picture_address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `resource_description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `sort` int UNSIGNED NULL DEFAULT 0,
  `published` tinyint(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 473 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_resources
-- ----------------------------
INSERT INTO `t_resources` VALUES (472, '泪心自叙', '', '梦一场', '虚拟现实', 'https://gitee.com/thoughtful123/tearyoByCompanyLife/raw/master/photosuse/bg33.jpg', '关于生活中的经历以及一些想说的话', NULL, 1, 1);

-- ----------------------------
-- Table structure for t_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_tag
-- ----------------------------

-- ----------------------------
-- Table structure for t_type
-- ----------------------------
DROP TABLE IF EXISTS `t_type`;
CREATE TABLE `t_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 45 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_type
-- ----------------------------
INSERT INTO `t_type` VALUES (43, '泪心叙事、人生圆缺');
INSERT INTO `t_type` VALUES (44, '泪心技术、经验分享');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type` int NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES (3, '/images/avatar.png', '2025-06-27 21:36:00', '51561@qq.com', 'tear', 'e10adc3949ba59abbe56e057f20f883e', 0, '2025-06-27 21:36:17', '123456');

SET FOREIGN_KEY_CHECKS = 1;
