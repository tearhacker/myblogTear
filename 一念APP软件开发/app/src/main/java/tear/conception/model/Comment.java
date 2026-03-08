package tear.conception.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Comment {

    private static final String BASE_URL = "http://121.35.251.197:80";

    private long id;
    private String nickname;
    private String email;
    private String content;
    private String avatar;
    private long createTime;
    private long blogId;
    private Long parentCommentId;
    private boolean adminComment;
    private List<Comment> replyComments;
    private String parentNickname;

    public Comment() {
        this.replyComments = new ArrayList<>();
    }

    public static Comment fromJson(JSONObject json) {
        Comment comment = new Comment();
        try {
            comment.setId(json.optLong("id", 0));
            comment.setNickname(json.optString("nickname", ""));
            comment.setEmail(json.optString("email", ""));
            comment.setContent(json.optString("content", ""));
            comment.setAvatar(toFullUrl(json.optString("avatar", "")));
            comment.setBlogId(json.optLong("blogId", 0));
            
            long parentId = json.optLong("parentCommentId", -1);
            comment.setParentCommentId(parentId > 0 ? parentId : null);
            
            comment.setAdminComment(json.optBoolean("adminComment", false));
            comment.setParentNickname(json.optString("parentNickname", ""));
            
            String createTimeStr = json.optString("createTime", "");
            comment.setCreateTime(parseDateToTimestamp(createTimeStr));
            
            JSONArray replyArray = json.optJSONArray("replyComments");
            if (replyArray != null && replyArray.length() > 0) {
                for (int i = 0; i < replyArray.length(); i++) {
                    JSONObject replyJson = replyArray.optJSONObject(i);
                    if (replyJson != null) {
                        comment.getReplyComments().add(Comment.fromJson(replyJson));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    public static List<Comment> fromJsonArray(JSONArray array) {
        List<Comment> comments = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.optJSONObject(i);
                if (json != null) {
                    comments.add(Comment.fromJson(json));
                }
            }
        }
        return comments;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getBlogId() {
        return blogId;
    }

    public void setBlogId(long blogId) {
        this.blogId = blogId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public boolean isAdminComment() {
        return adminComment;
    }

    public void setAdminComment(boolean adminComment) {
        this.adminComment = adminComment;
    }

    public List<Comment> getReplyComments() {
        return replyComments;
    }

    public void setReplyComments(List<Comment> replyComments) {
        this.replyComments = replyComments;
    }

    public String getParentNickname() {
        return parentNickname;
    }

    public void setParentNickname(String parentNickname) {
        this.parentNickname = parentNickname;
    }
}
