package tear.conception.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Blog {

    private static final String BASE_URL = "http://121.35.251.197:80";

    private long id;
    private String title;
    private String firstPicture;
    private String description;
    private String content;
    private int views;
    private int commentCount;
    private long createTime;
    private long updateTime;
    private String typeName;
    private String nickname;
    private String avatar;
    private int blogStatus;
    private boolean needPassword;

    public Blog() {
    }

    public static Blog fromJson(JSONObject json) {
        Blog blog = new Blog();
        try {
            blog.setId(json.optLong("id", 0));
            blog.setTitle(json.optString("title", ""));
            blog.setFirstPicture(toFullUrl(json.optString("firstPicture", "")));
            blog.setDescription(json.optString("description", ""));
            blog.setContent(json.optString("content", ""));
            blog.setViews(json.optInt("views", 0));
            blog.setCommentCount(json.optInt("commentCount", 0));
            blog.setTypeName(json.optString("typeName", ""));
            blog.setNickname(json.optString("nickname", ""));
            blog.setAvatar(toFullUrl(json.optString("avatar", "")));
            blog.setBlogStatus(json.optInt("blogStatus", 1));
            blog.setNeedPassword(json.optBoolean("needPassword", false));
            
            String createTimeStr = json.optString("createTime", "");
            String updateTimeStr = json.optString("updateTime", "");
            blog.setCreateTime(parseDateToTimestamp(createTimeStr));
            blog.setUpdateTime(parseDateToTimestamp(updateTimeStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blog;
    }

    private static String toFullUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.trim().isEmpty()) {
            return "";
        }
        
        relativeUrl = relativeUrl.trim();
        
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            return relativeUrl;
        }
        
        if (relativeUrl.startsWith("/")) {
            return BASE_URL + relativeUrl;
        }
        
        return BASE_URL + "/" + relativeUrl;
    }

    private static long parseDateToTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : 0;
        } catch (ParseException e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
                Date date = sdf.parse(dateStr);
                return date != null ? date.getTime() : 0;
            } catch (ParseException e2) {
                return 0;
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBlogStatus() {
        return blogStatus;
    }

    public void setBlogStatus(int blogStatus) {
        this.blogStatus = blogStatus;
    }

    public boolean isNeedPassword() {
        return needPassword;
    }

    public void setNeedPassword(boolean needPassword) {
        this.needPassword = needPassword;
    }
}
