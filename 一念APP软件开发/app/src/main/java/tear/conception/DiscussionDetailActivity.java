package tear.conception;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import tear.conception.model.Discussion;
import tear.conception.model.DiscussionComment;
import tear.conception.module.BlogApiService;
import tear.conception.R;
import tear.conception.util.SharedPreferencesUtil;

public class DiscussionDetailActivity extends Activity {

    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvTime;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvLikeCount;
    private TextView tvViewCount;
    private TextView tvCommentCount;
    private LinearLayout layoutLike;
    private ImageView ivLike;
    private EditText etComment;
    private TextView tvSubmitComment;
    private LinearLayout commentsLayout;
    private LinearLayout commentsContainer;
    private TextView tvBack;

    private SharedPreferencesUtil prefsUtil;
    private long userId = 0;
    private String qqNumber = "";
    private String nickname = "";
    private String avatar = "";
    private long discussionId = 0;
    private Discussion discussion;
    private List<DiscussionComment> comments;
    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_detail);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        userId = prefsUtil.getLong("user_id", 0);
        qqNumber = prefsUtil.getString("qq_number", "");
        nickname = prefsUtil.getString("nickname", "");
        avatar = prefsUtil.getString("avatar", "");

        discussionId = getIntent().getLongExtra("discussionId", 0);
        if (discussionId == 0) {
            Toast.makeText(this, "话题ID错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadDiscussionDetail();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        contentLayout = findViewById(R.id.content_layout);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvTime = findViewById(R.id.tv_time);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvLikeCount = findViewById(R.id.tv_like_count);
        tvViewCount = findViewById(R.id.tv_view_count);
        tvCommentCount = findViewById(R.id.tv_comment_count);
        layoutLike = findViewById(R.id.layout_like);
        ivLike = findViewById(R.id.iv_like);
        etComment = findViewById(R.id.et_comment);
        tvSubmitComment = findViewById(R.id.tv_submit_comment);
        commentsLayout = findViewById(R.id.comments_layout);
        commentsContainer = findViewById(R.id.comments_container);
        tvBack = findViewById(R.id.tv_back);
    }

    private void setupListeners() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeDiscussion();
            }
        });

        tvSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitComment();
            }
        });
    }

    private void loadDiscussionDetail() {
        showLoading();

        BlogApiService.getDiscussionDetail(discussionId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        try {
                            int code = response.optInt("code", -1);
                            if (code == 200) {
                                JSONObject data = response.optJSONObject("data");
                                if (data != null) {
                                    JSONObject discussionJson = data.optJSONObject("discussion");
                                    JSONArray commentsArray = data.optJSONArray("comments");

                                    if (discussionJson != null) {
                                        discussion = Discussion.fromJson(discussionJson);
                                        displayDiscussion();
                                    }

                                    if (commentsArray != null) {
                                        comments = DiscussionComment.fromJsonArray(commentsArray);
                                        displayComments();
                                    }
                                }
                            } else {
                                String message = response.optString("message", "获取失败");
                                Toast.makeText(DiscussionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(DiscussionDetailActivity.this, "解析数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Toast.makeText(DiscussionDetailActivity.this, "加载失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void displayDiscussion() {
        if (discussion == null) return;

        tvNickname.setText(discussion.getNickname());
        tvTitle.setText(discussion.getTitle());
        tvContent.setText(discussion.getContent());
        tvLikeCount.setText(formatCount(discussion.getLikeCount()));
        tvViewCount.setText(formatCount(discussion.getViewCount()));
        tvCommentCount.setText(formatCount(discussion.getCommentCount()));

        String timeText = formatTime(discussion.getCreateTime());
        tvTime.setText(timeText);

        ivAvatar.setImageResource(R.drawable.default_avatar);
        if (discussion.getAvatar() != null && !discussion.getAvatar().isEmpty()) {
            loadAvatar(ivAvatar, discussion.getAvatar());
        }

        contentLayout.setVisibility(View.VISIBLE);
    }

    private void displayComments() {
        if (comments == null || comments.isEmpty()) {
            commentsLayout.setVisibility(View.GONE);
            return;
        }

        commentsLayout.setVisibility(View.VISIBLE);
        commentsContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (DiscussionComment comment : comments) {
            View commentView = createCommentView(inflater, comment, 0);
            commentsContainer.addView(commentView);
        }
    }

    private View createCommentView(LayoutInflater inflater, DiscussionComment comment, int depth) {
        View view = inflater.inflate(R.layout.item_discussion_comment, null);

        ImageView ivAvatar = view.findViewById(R.id.iv_avatar);
        TextView tvNickname = view.findViewById(R.id.tv_nickname);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvLikeCount = view.findViewById(R.id.tv_like_count);
        LinearLayout repliesContainer = view.findViewById(R.id.replies_container);

        tvNickname.setText(comment.getNickname());
        tvContent.setText(comment.getContent());
        tvLikeCount.setText(formatCount(comment.getLikeCount()));
        tvTime.setText(comment.getFormattedTime());

        ivAvatar.setImageResource(R.drawable.default_avatar);
        if (comment.getAvatar() != null && !comment.getAvatar().isEmpty()) {
            loadAvatar(ivAvatar, comment.getAvatar());
        }

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            repliesContainer.setVisibility(View.VISIBLE);
            for (DiscussionComment reply : comment.getReplies()) {
                View replyView = createCommentView(inflater, reply, depth + 1);
                repliesContainer.addView(replyView);
            }
        } else {
            repliesContainer.setVisibility(View.GONE);
        }

        return view;
    }

    private void likeDiscussion() {
        if (discussion == null) return;

        BlogApiService.likeDiscussion(discussionId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int code = response.optInt("code", -1);
                        if (code == 200) {
                            isLiked = !isLiked;
                            int newLikeCount = discussion.getLikeCount() + (isLiked ? 1 : -1);
                            discussion.setLikeCount(newLikeCount);
                            tvLikeCount.setText(formatCount(newLikeCount));
                            ivLike.setImageResource(isLiked ? R.drawable.ic_like_filled : R.drawable.ic_like);
                            Toast.makeText(DiscussionDetailActivity.this, isLiked ? "点赞成功" : "取消点赞", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.optString("message", "操作失败");
                            Toast.makeText(DiscussionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DiscussionDetailActivity.this, "操作失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void submitComment() {
        if (userId == 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String content = etComment.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }

        tvSubmitComment.setEnabled(false);
        tvSubmitComment.setText("发表中...");

        BlogApiService.createComment(discussionId, userId, qqNumber, nickname, avatar, content, null, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvSubmitComment.setEnabled(true);
                        tvSubmitComment.setText("发表评论");

                        int code = response.optInt("code", -1);
                        if (code == 200) {
                            etComment.setText("");
                            Toast.makeText(DiscussionDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            loadDiscussionDetail();
                        } else {
                            String message = response.optString("message", "评论失败");
                            Toast.makeText(DiscussionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvSubmitComment.setEnabled(true);
                        tvSubmitComment.setText("发表评论");
                        Toast.makeText(DiscussionDetailActivity.this, "评论失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private String formatCount(int count) {
        if (count >= 10000) {
            return String.format("%.1fw", count / 10000.0);
        } else if (count >= 1000) {
            return String.format("%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private String formatTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return "";
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(timeString);
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
                java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MM-dd HH:mm", java.util.Locale.getDefault());
                return displayFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return timeString;
        }
    }

    private void loadAvatar(final ImageView imageView, final String url) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL imageUrl = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream input = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if (result != null && imageView != null) {
                    imageView.setImageBitmap(result);
                }
            }
        }.execute(url);
    }
}
