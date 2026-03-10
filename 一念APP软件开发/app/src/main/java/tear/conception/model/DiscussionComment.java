package tear.conception.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiscussionComment {
    
    private Long id;
    private Long discussionId;
    private Long userId;
    private String qqNumber;
    private String nickname;
    private String avatar;
    private String content;
    private Long parentCommentId;
    private Integer likeCount;
    private String createTime;
    private List<DiscussionComment> replies;

    public DiscussionComment() {
        this.replies = new ArrayList<>();
    }

    public static DiscussionComment fromJson(JSONObject json) {
        DiscussionComment comment = new DiscussionComment();
        try {
            comment.id = json.optLong("id", 0L);
            comment.discussionId = json.optLong("discussionId", 0L);
            comment.userId = json.optLong("userId", 0L);
            // 尝试两种可能的字段名
            comment.qqNumber = json.optString("qqNumber", "");
            if (comment.qqNumber.isEmpty()) {
                comment.qqNumber = json.optString("qq_number", "");
            }
            comment.nickname = json.optString("nickname", "");
            comment.avatar = json.optString("avatar", "");
            comment.content = json.optString("content", "");
            comment.likeCount = json.optInt("likeCount", 0);
            comment.createTime = json.optString("createTime", "");
            
            long parentId = json.optLong("parentCommentId", -1);
            comment.parentCommentId = parentId > 0 ? parentId : null;
            
            JSONArray replyArray = json.optJSONArray("replies");
            if (replyArray != null && replyArray.length() > 0) {
                for (int i = 0; i < replyArray.length(); i++) {
                    JSONObject replyJson = replyArray.optJSONObject(i);
                    if (replyJson != null) {
                        comment.replies.add(DiscussionComment.fromJson(replyJson));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comment;
    }

    public static List<DiscussionComment> fromJsonArray(JSONArray array) {
        List<DiscussionComment> comments = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.optJSONObject(i);
                if (json != null) {
                    comments.add(DiscussionComment.fromJson(json));
                }
            }
        }
        return comments;
    }

    public String getFormattedTime() {
        if (createTime == null || createTime.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(createTime);
            long timestamp = date.getTime();
            
            long now = System.currentTimeMillis();
            long diff = now - timestamp;
            
            if (diff < 60 * 1000) {
                return "刚刚";
            } else if (diff < 60 * 60 * 1000) {
                return (diff / (60 * 1000)) + "分钟前";
            } else if (diff < 24 * 60 * 60 * 1000) {
                return (diff / (60 * 60 * 1000)) + "小时前";
            } else if (diff < 7 * 24 * 60 * 60 * 1000) {
                return (diff / (24 * 60 * 60 * 1000)) + "天前";
            } else {
                SimpleDateFormat displayFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createTime;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Long discussionId) {
        this.discussionId = discussionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<DiscussionComment> getReplies() {
        return replies;
    }

    public void setReplies(List<DiscussionComment> replies) {
        this.replies = replies;
    }
}
