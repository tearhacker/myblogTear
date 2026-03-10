package tear.conception.module;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlogApiService {

    private static final String BASE_URL = "http://121.35.251.197:80";
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public interface BlogListCallback {
        void onSuccess(List<tear.conception.model.Blog> blogList, int total, int pageNum, boolean hasNextPage);
        void onError(String error);
    }

    public static void login(String qqNumber, String nickname, ApiCallback callback) {
        String url = BASE_URL + "/app/user/login";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("qqNumber", qqNumber);
        if (nickname != null && !nickname.isEmpty()) {
            params.put("nickname", nickname);
        }
        postRequest(url, params, callback);
    }

    public static void doSignin(long userId, String qqNumber, ApiCallback callback) {
        String url = BASE_URL + "/app/signin/do";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("userId", String.valueOf(userId));
        params.put("qqNumber", qqNumber);
        postRequest(url, params, callback);
    }

    public static void getTodaySignin(long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/signin/today/" + userId;
        getRequest(url, callback);
    }

    public static void getSigninStats(long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/signin/stats/" + userId;
        getRequest(url, callback);
    }

    public static void doLoveSignin(long userId, String qqNumber, String targetName, ApiCallback callback) {
        String url = BASE_URL + "/app/love/signin";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("userId", String.valueOf(userId));
        params.put("qqNumber", qqNumber);
        if (targetName != null && !targetName.isEmpty()) {
            params.put("targetName", targetName);
        }
        postRequest(url, params, callback);
    }

    public static void getTodayLoveSignin(long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/love/today/" + userId;
        getRequest(url, callback);
    }

    public static void getLoveSigninHistory(long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/love/history/" + userId;
        getRequest(url, callback);
    }

    public static void updateNickname(long userId, String nickname, ApiCallback callback) {
        String url = BASE_URL + "/app/user/update/nickname";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("id", String.valueOf(userId));
        params.put("nickname", nickname);
        postRequest(url, params, callback);
    }

    public static void checkVersion(int versionCode, ApiCallback callback) {
        String url = BASE_URL + "/app/version/check?versionCode=" + versionCode;
        getRequest(url, callback);
    }

    public static void getLatestVersion(ApiCallback callback) {
        String url = BASE_URL + "/app/version/latest";
        getRequest(url, callback);
    }

    public static void getBlogList(int pageNum, int pageSize, final BlogListCallback callback) {
        String url = BASE_URL + "/app/blog/list?pageNum=" + pageNum + "&pageSize=" + pageSize;
        getRequest(url, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.optInt("code", -1);
                    if (code == 200) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            JSONArray listArray = data.optJSONArray("list");
                            List<tear.conception.model.Blog> blogList = new ArrayList<>();
                            if (listArray != null) {
                                for (int i = 0; i < listArray.length(); i++) {
                                    JSONObject blogJson = listArray.optJSONObject(i);
                                    if (blogJson != null) {
                                        blogList.add(tear.conception.model.Blog.fromJson(blogJson));
                                    }
                                }
                            }
                            int total = data.optInt("total", 0);
                            int pageNum = data.optInt("pageNum", 1);
                            boolean hasNextPage = data.optBoolean("hasNextPage", false);
                            callback.onSuccess(blogList, total, pageNum, hasNextPage);
                        } else {
                            callback.onError("数据格式错误");
                        }
                    } else {
                        String message = response.optString("message", "获取失败");
                        callback.onError(message);
                    }
                } catch (Exception e) {
                    callback.onError("解析数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public static void getBlogDetail(long blogId, ApiCallback callback) {
        String url = BASE_URL + "/app/blog/detail/" + blogId;
        getRequest(url, callback);
    }

    public static void verifyBlogPassword(long blogId, String password, ApiCallback callback) {
        String url = BASE_URL + "/app/blog/detail/" + blogId + "/verify";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("password", password);
        postRequest(url, params, callback);
    }

    public static void searchBlog(String query, int pageNum, int pageSize, final BlogListCallback callback) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = BASE_URL + "/app/blog/search?query=" + encodedQuery + "&pageNum=" + pageNum + "&pageSize=" + pageSize;
            getRequest(url, new ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        int code = response.optInt("code", -1);
                        if (code == 200) {
                            JSONObject data = response.optJSONObject("data");
                            if (data != null) {
                                JSONArray listArray = data.optJSONArray("list");
                                List<tear.conception.model.Blog> blogList = new ArrayList<>();
                                if (listArray != null) {
                                    for (int i = 0; i < listArray.length(); i++) {
                                        JSONObject blogJson = listArray.optJSONObject(i);
                                        if (blogJson != null) {
                                            blogList.add(tear.conception.model.Blog.fromJson(blogJson));
                                        }
                                    }
                                }
                                int total = data.optInt("total", 0);
                                int pageNum = data.optInt("pageNum", 1);
                                boolean hasNextPage = data.optBoolean("hasNextPage", false);
                                callback.onSuccess(blogList, total, pageNum, hasNextPage);
                            } else {
                                callback.onError("数据格式错误");
                            }
                        } else {
                            String message = response.optString("message", "搜索失败");
                            callback.onError(message);
                        }
                    } catch (Exception e) {
                        callback.onError("解析数据失败: " + e.getMessage());
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } catch (Exception e) {
            callback.onError("编码搜索词失败: " + e.getMessage());
        }
    }

    public static void getBlogByType(long typeId, int pageNum, int pageSize, final BlogListCallback callback) {
        String url = BASE_URL + "/app/blog/type/" + typeId + "?pageNum=" + pageNum + "&pageSize=" + pageSize;
        getRequest(url, new ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.optInt("code", -1);
                    if (code == 200) {
                        JSONObject data = response.optJSONObject("data");
                        if (data != null) {
                            JSONArray listArray = data.optJSONArray("list");
                            List<tear.conception.model.Blog> blogList = new ArrayList<>();
                            if (listArray != null) {
                                for (int i = 0; i < listArray.length(); i++) {
                                    JSONObject blogJson = listArray.optJSONObject(i);
                                    if (blogJson != null) {
                                        blogList.add(tear.conception.model.Blog.fromJson(blogJson));
                                    }
                                }
                            }
                            int total = data.optInt("total", 0);
                            int pageNum = data.optInt("pageNum", 1);
                            boolean hasNextPage = data.optBoolean("hasNextPage", false);
                            callback.onSuccess(blogList, total, pageNum, hasNextPage);
                        } else {
                            callback.onError("数据格式错误");
                        }
                    } else {
                        String message = response.optString("message", "获取失败");
                        callback.onError(message);
                    }
                } catch (Exception e) {
                    callback.onError("解析数据失败: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public static void getRecommendBlog(ApiCallback callback) {
        String url = BASE_URL + "/app/blog/recommend";
        getRequest(url, callback);
    }

    public static void getCommentList(long blogId, ApiCallback callback) {
        String url = BASE_URL + "/app/comment/list/" + blogId;
        getRequest(url, callback);
    }

    public static void addComment(long blogId, String nickname, String content, String avatar, Long parentId, ApiCallback callback) {
        String url = BASE_URL + "/app/comment/add";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("blogId", String.valueOf(blogId));
        params.put("nickname", nickname);
        params.put("content", content);
        if (avatar != null && !avatar.isEmpty()) {
            params.put("avatar", avatar);
        }
        if (parentId != null && parentId > 0) {
            params.put("parentId", String.valueOf(parentId));
        }
        postRequest(url, params, callback);
    }

    public static void deleteComment(long blogId, long commentId, long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/comment/delete/" + blogId + "/" + commentId + "?userId=" + userId;
        getRequest(url, callback);
    }

    public static void aiChat(String message, ApiCallback callback) {
        String url = BASE_URL + "/app/ai/chat";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("message", message);
        postRequestWithTimeout(url, params, 60000, 120000, callback);
    }

    public static void getAiSigninMessage(int days, ApiCallback callback) {
        String url = BASE_URL + "/app/ai/signin/message?days=" + days;
        getRequestWithTimeout(url, 60000, 120000, callback);
    }

    public static void generateLoveLetter(String targetName, int days, String userWords, ApiCallback callback) {
        String url = BASE_URL + "/app/ai/love-letter";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("targetName", targetName);
        params.put("days", String.valueOf(days));
        if (userWords != null && !userWords.isEmpty()) {
            params.put("userWords", userWords);
        }
        postRequestWithTimeout(url, params, 60000, 120000, callback);
    }

    public static void createDiscussion(long userId, String qqNumber, String nickname, String avatar, String title, String content, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/create";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("userId", String.valueOf(userId));
        params.put("qqNumber", qqNumber);
        params.put("nickname", nickname);
        if (avatar != null && !avatar.isEmpty()) {
            params.put("avatar", avatar);
        }
        params.put("title", title);
        params.put("content", content);
        postRequest(url, params, callback);
    }

    public static void getDiscussionList(int page, int pageSize, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/list?page=" + page + "&pageSize=" + pageSize;
        getRequest(url, callback);
    }

    public static void searchDiscussions(String keyword, int page, int pageSize, ApiCallback callback) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
            String url = BASE_URL + "/app/discussion/search?keyword=" + encodedKeyword + "&page=" + page + "&pageSize=" + pageSize;
            getRequest(url, callback);
        } catch (Exception e) {
            callback.onError("编码搜索词失败: " + e.getMessage());
        }
    }

    public static void getDiscussionDetail(long id, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/detail/" + id;
        getRequest(url, callback);
    }

    public static void getMyDiscussions(long userId, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/my/" + userId;
        getRequest(url, callback);
    }

    public static void likeDiscussion(long id, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/like/" + id;
        postRequest(url, new java.util.HashMap<String, String>(), callback);
    }

    public static void createComment(long discussionId, long userId, String qqNumber, String nickname, String avatar, String content, Long parentCommentId, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/comment/create";
        Map<String, String> params = new java.util.HashMap<>();
        params.put("discussionId", String.valueOf(discussionId));
        params.put("userId", String.valueOf(userId));
        params.put("qqNumber", qqNumber);
        params.put("nickname", nickname);
        if (avatar != null && !avatar.isEmpty()) {
            params.put("avatar", avatar);
        }
        params.put("content", content);
        if (parentCommentId != null && parentCommentId > 0) {
            params.put("parentCommentId", String.valueOf(parentCommentId));
        }
        postRequest(url, params, callback);
    }

    public static void getComments(long discussionId, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/comment/" + discussionId;
        getRequest(url, callback);
    }

    public static void getReplies(long parentCommentId, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/comment/replies/" + parentCommentId;
        getRequest(url, callback);
    }

    public static void likeComment(long id, ApiCallback callback) {
        String url = BASE_URL + "/app/discussion/comment/like/" + id;
        postRequest(url, new java.util.HashMap<String, String>(), callback);
    }

    private static void getRequestWithTimeout(final String urlString, final int connectTimeout, final int readTimeout, final ApiCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(connectTimeout);
                    conn.setReadTimeout(readTimeout);

                    final int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final JSONObject jsonResponse = new JSONObject(response.toString());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(jsonResponse);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("服务器错误: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("网络错误: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private static void postRequestWithTimeout(final String urlString, final Map<String, String> params, final int connectTimeout, final int readTimeout, final ApiCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(connectTimeout);
                    conn.setReadTimeout(readTimeout);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (postData.length() != 0) {
                            postData.append("&");
                        }
                        postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        postData.append("=");
                        postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.toString().getBytes("UTF-8"));
                    os.close();

                    final int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final JSONObject jsonResponse = new JSONObject(response.toString());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(jsonResponse);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("服务器错误: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("网络错误: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private static void getRequest(final String urlString, final ApiCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    final int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final JSONObject jsonResponse = new JSONObject(response.toString());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(jsonResponse);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("请求失败: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("网络错误: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private static void postRequest(final String urlString, final Map<String, String> params, final ApiCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (postData.length() != 0) {
                            postData.append("&");
                        }
                        postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        postData.append("=");
                        postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.toString().getBytes("UTF-8"));
                    os.close();

                    final int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        final JSONObject jsonResponse = new JSONObject(response.toString());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(jsonResponse);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError("请求失败: " + responseCode);
                            }
                        });
                    }
                    conn.disconnect();
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError("网络错误: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }
}
