# 泪心API密钥对接文档

## 接口说明

### 验证接口(推荐)
- URL: `GET /api/secret/v?k=你的密钥`
- 或: `POST /api/secret/check` 参数 `key=你的密钥`

### 返回格式
```json
// 成功
{
  "code": 0,
  "msg": "ok",
  "data": {
    "version": "1.0.0",
    "contact": "2254013571@qq.com",
    "expireTime": 0,
    "accessCount": 128,
    "todayCount": 5
  }
}

// 失败
{"code":-4,"msg":"密钥无效"}
```

### 返回字段说明
| 字段 | 类型 | 说明 |
|------|------|------|
| version | string | 版本号 |
| contact | string | 联系方式 |
| expireTime | long | 过期时间戳(秒)，0表示永久有效 |
| accessCount | long | 全网总启动次数 |
| todayCount | long | 今日启动次数 |

### 错误码
| code | 说明 |
|------|------|
| 0 | 成功 |
| -1 | 密钥不能为空 |
| -2 | IP临时封禁(失败次数过多) |
| -3 | 请求过于频繁 |
| -4 | 密钥无效 |
| -5 | 密钥已禁用 |
| -6 | 密钥已过期 |

### 健康检查
- URL: `GET /api/secret/ping`
- 返回: `{"code":0,"msg":"pong","ts":1735545600}`

---

## 数据库表结构

```sql
CREATE TABLE `t_api_secret` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tear_secret` varchar(64) NOT NULL COMMENT 'API密钥',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-启用，1-禁用',
  `contact` varchar(255) NULL COMMENT '联系方式',
  `version` varchar(50) NULL DEFAULT '1.0.0' COMMENT '版本号',
  `expire_time` datetime NULL COMMENT '过期时间(NULL永久有效)',
  `access_count` bigint NOT NULL DEFAULT 0 COMMENT '全网总启动次数',
  `today_count` bigint NOT NULL DEFAULT 0 COMMENT '今日启动次数',
  `today_date` date NULL COMMENT '今日日期(用于重置today_count)',
  `last_access_time` datetime NULL COMMENT '最后访问时间',
  `remark` varchar(500) NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uk_tear_secret`(`tear_secret`)
) ENGINE=InnoDB COMMENT='API密钥管理表';
```

---

## C++ CURL调用示例

```cpp
#include <iostream>
#include <string>
#include <curl/curl.h>

// 回调函数
size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* out) {
    out->append((char*)contents, size * nmemb);
    return size * nmemb;
}

// 验证密钥
bool verifyKey(const std::string& key, const std::string& server) {
    CURL* curl = curl_easy_init();
    if (!curl) return false;

    std::string url = server + "/api/secret/v?k=" + key;
    std::string response;

    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &response);
    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 10L);
    curl_easy_setopt(curl, CURLOPT_SSL_VERIFYPEER, 0L); // HTTPS时需要

    CURLcode res = curl_easy_perform(curl);
    curl_easy_cleanup(curl);

    if (res != CURLE_OK) {
        std::cerr << "网络错误: " << curl_easy_strerror(res) << std::endl;
        return false;
    }

    std::cout << "响应: " << response << std::endl;
    // 可解析JSON获取 accessCount, todayCount 等信息
    return response.find("\"code\":0") != std::string::npos;
}

int main() {
    curl_global_init(CURL_GLOBAL_ALL);
    
    if (verifyKey("TEAR-2024-SECRET-DEMO-001", "http://你的服务器")) {
        std::cout << "授权验证成功!" << std::endl;
    } else {
        std::cout << "授权验证失败!" << std::endl;
        return -1;
    }
    
    curl_global_cleanup();
    return 0;
}
```

### 编译
```bash
g++ -o app main.cpp -lcurl
```

---

## 简洁封装版

```cpp
#include <curl/curl.h>
#include <string>

class TearAuth {
public:
    static bool check(const char* key, const char* server = "http://你的服务器") {
        CURL* curl = curl_easy_init();
        if (!curl) return false;
        
        std::string url = std::string(server) + "/api/secret/v?k=" + key;
        std::string resp;
        
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, [](void* p, size_t s, size_t n, std::string* o) {
            o->append((char*)p, s * n); return s * n;
        });
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &resp);
        curl_easy_setopt(curl, CURLOPT_TIMEOUT, 5L);
        
        bool ok = (curl_easy_perform(curl) == CURLE_OK) && (resp.find("\"code\":0") != std::string::npos);
        curl_easy_cleanup(curl);
        return ok;
    }
};

// 使用: if (!TearAuth::check("你的密钥")) return -1;
```

---

## 管理接口(内部使用)

### 创建密钥
- URL: `POST /api/secret/TearGame/new_create`
- 参数:
  - `contact` (必填): 联系方式
  - `version` (可选): 版本号，默认1.0.0
  - `remark` (可选): 备注
  - `days` (可选): 有效天数，0或不传为永久

```json
// 返回
{
  "code": 0,
  "msg": "创建成功",
  "data": {
    "tearSecret": "TEAR-XXXX-XXXX-XXXX",
    "contact": "xxx@qq.com",
    "version": "1.0.0",
    "expireTime": 0
  }
}
```
