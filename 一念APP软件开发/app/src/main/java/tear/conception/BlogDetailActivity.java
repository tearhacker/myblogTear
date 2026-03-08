package tear.conception;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tear.conception.model.Blog;
import tear.conception.model.Comment;
import tear.conception.module.BlogApiService;
import tear.conception.util.SharedPreferencesUtil;

public class BlogDetailActivity extends Activity {

    private ImageView ivCover;
    private ImageView ivAvatar;
    private TextView tvTitle;
    private TextView tvNickname;
    private TextView tvTime;
    private TextView tvType;
    private TextView tvContent;
    private TextView tvViews;
    private TextView tvCommentCount;
    private ProgressBar progressBar;
    private LinearLayout llBack;
    private LinearLayout llPasswordContainer;
    private LinearLayout llStats;
    private EditText etPassword;
    private TextView tvVerify;
    
    private LinearLayout llCommentSection;
    private LinearLayout llCommentList;
    private TextView tvNoComment;
    private ProgressBar pbCommentLoading;
    private LinearLayout llCommentInput;
    private EditText etComment;
    private TextView tvSendComment;

    private long blogId;
    private Blog currentBlog;
    private SharedPreferencesUtil prefsUtil;
    
    private long userId;
    private String userNickname;
    private String userAvatar;
    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        
        setContentView(R.layout.activity_blog_detail);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        checkLoginStatus();
        
        blogId = getIntent().getLongExtra("blogId", 0);

        initViews();
        setupListeners();
        
        if (blogId > 0) {
            loadBlogDetail();
        } else {
            showError("文章ID无效");
            finish();
        }
    }

    private void checkLoginStatus() {
        userId = prefsUtil.getLong("user_id", 0);
        userNickname = prefsUtil.getString("nickname", "");
        userAvatar = prefsUtil.getString("avatar", "");
        isLoggedIn = userId > 0;
    }

    private void initViews() {
        ivCover = findViewById(R.id.iv_cover);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvTitle = findViewById(R.id.tv_title);
        tvNickname = findViewById(R.id.tv_nickname);
        tvTime = findViewById(R.id.tv_time);
        tvType = findViewById(R.id.tv_type);
        tvContent = findViewById(R.id.tv_content);
        tvViews = findViewById(R.id.tv_views);
        tvCommentCount = findViewById(R.id.tv_comment_count);
        progressBar = findViewById(R.id.progress_bar);
        llBack = findViewById(R.id.ll_back);
        llPasswordContainer = findViewById(R.id.ll_password_container);
        llStats = findViewById(R.id.ll_stats);
        etPassword = findViewById(R.id.et_password);
        tvVerify = findViewById(R.id.tv_verify);
        
        llCommentSection = findViewById(R.id.ll_comment_section);
        llCommentList = findViewById(R.id.ll_comment_list);
        tvNoComment = findViewById(R.id.tv_no_comment);
        pbCommentLoading = findViewById(R.id.pb_comment_loading);
        llCommentInput = findViewById(R.id.ll_comment_input);
        etComment = findViewById(R.id.et_comment);
        tvSendComment = findViewById(R.id.tv_send_comment);
    }

    private void setupListeners() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPassword();
            }
        });
        
        tvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    private void loadBlogDetail() {
        progressBar.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        llStats.setVisibility(View.GONE);
        llPasswordContainer.setVisibility(View.GONE);
        llCommentSection.setVisibility(View.GONE);
        llCommentInput.setVisibility(View.GONE);

        BlogApiService.getBlogDetail(blogId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleBlogDetailResponse(response);
                    }
                });
            }

            @Override
            public void onError(final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError(errorMsg);
                    }
                });
            }
        });
    }

    private void handleBlogDetailResponse(JSONObject response) {
        try {
            int code = response.optInt("code", -1);
            if (code == 200) {
                JSONObject data = response.optJSONObject("data");
                if (data != null) {
                    currentBlog = Blog.fromJson(data);
                    
                    if (data.optBoolean("needPassword", false)) {
                        showPasswordInput();
                    } else {
                        showBlogContent(currentBlog);
                        loadComments();
                    }
                } else {
                    showError("数据格式错误");
                }
            } else {
                String message = response.optString("message", "获取失败");
                showError(message);
            }
        } catch (Exception e) {
            showError("解析数据失败: " + e.getMessage());
        }
    }

    private void showPasswordInput() {
        progressBar.setVisibility(View.GONE);
        llPasswordContainer.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        llStats.setVisibility(View.GONE);
        llCommentSection.setVisibility(View.GONE);
        llCommentInput.setVisibility(View.GONE);
        
        if (currentBlog != null) {
            tvTitle.setText(currentBlog.getTitle());
            if (currentBlog.getFirstPicture() != null && !currentBlog.getFirstPicture().isEmpty()) {
                new ImageLoadTask(ivCover).execute(currentBlog.getFirstPicture());
            }
        }
    }

    private void verifyPassword() {
        final String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        llPasswordContainer.setVisibility(View.GONE);

        BlogApiService.verifyBlogPassword(blogId, password, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleVerifyResponse(response);
                    }
                });
            }

            @Override
            public void onError(final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError(errorMsg);
                        showPasswordInput();
                    }
                });
            }
        });
    }

    private void handleVerifyResponse(JSONObject response) {
        try {
            int code = response.optInt("code", -1);
            if (code == 200) {
                JSONObject data = response.optJSONObject("data");
                if (data != null) {
                    Blog blog = Blog.fromJson(data);
                    showBlogContent(blog);
                    loadComments();
                    Toast.makeText(BlogDetailActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                String message = response.optString("message", "密码错误");
                showError(message);
                showPasswordInput();
            }
        } catch (Exception e) {
            showError("验证失败: " + e.getMessage());
            showPasswordInput();
        }
    }

    private void showBlogContent(Blog blog) {
        progressBar.setVisibility(View.GONE);
        llPasswordContainer.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);
        llStats.setVisibility(View.VISIBLE);
        llCommentSection.setVisibility(View.VISIBLE);
        
        if (isLoggedIn) {
            llCommentInput.setVisibility(View.VISIBLE);
        }

        tvTitle.setText(blog.getTitle());
        tvNickname.setText(blog.getNickname());
        tvType.setText(blog.getTypeName());
        tvViews.setText(formatCount(blog.getViews()));
        tvCommentCount.setText(formatCount(blog.getCommentCount()));

        String timeText = formatTime(blog.getUpdateTime());
        if (timeText.isEmpty()) {
            timeText = formatTime(blog.getCreateTime());
        }
        tvTime.setText(timeText);

        if (blog.getFirstPicture() != null && !blog.getFirstPicture().isEmpty()) {
            new ImageLoadTask(ivCover).execute(blog.getFirstPicture());
        }

        if (blog.getAvatar() != null && !blog.getAvatar().isEmpty()) {
            new ImageLoadTask(ivAvatar).execute(blog.getAvatar());
        }

        String content = blog.getContent();
        if (content != null && !content.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvContent.setText(Html.fromHtml(content));
            }
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tvContent.setText("暂无内容");
        }
    }

    private void loadComments() {
        llCommentSection.setVisibility(View.VISIBLE);
        pbCommentLoading.setVisibility(View.VISIBLE);
        tvNoComment.setVisibility(View.GONE);
        llCommentList.removeAllViews();

        BlogApiService.getCommentList(blogId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleCommentResponse(response);
                    }
                });
            }

            @Override
            public void onError(final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbCommentLoading.setVisibility(View.GONE);
                        tvNoComment.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void handleCommentResponse(JSONObject response) {
        pbCommentLoading.setVisibility(View.GONE);
        
        try {
            int code = response.optInt("code", -1);
            if (code == 200) {
                JSONArray dataArray = response.optJSONArray("data");
                if (dataArray != null && dataArray.length() > 0) {
                    List<Comment> comments = Comment.fromJsonArray(dataArray);
                    renderComments(comments);
                } else {
                    tvNoComment.setVisibility(View.VISIBLE);
                    tvNoComment.setText("暂无评论，快来抢沙发吧~");
                }
            } else {
                tvNoComment.setVisibility(View.VISIBLE);
                tvNoComment.setText("加载评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvNoComment.setVisibility(View.VISIBLE);
            tvNoComment.setText("解析评论失败: " + e.getMessage());
        }
    }

    private void renderComments(List<Comment> comments) {
        llCommentList.removeAllViews();
        tvNoComment.setVisibility(View.GONE);
        
        if (comments == null || comments.isEmpty()) {
            tvNoComment.setVisibility(View.VISIBLE);
            tvNoComment.setText("暂无评论，快来抢沙发吧~");
            return;
        }
        
        for (Comment comment : comments) {
            View commentView = createCommentView(comment, 0);
            if (commentView != null) {
                llCommentList.addView(commentView);
            }
        }
    }

    private View createCommentView(Comment comment, int level) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_comment, llCommentList, false);
        
        ImageView ivAvatar = view.findViewById(R.id.iv_comment_avatar);
        TextView tvNickname = view.findViewById(R.id.tv_comment_nickname);
        TextView tvContent = view.findViewById(R.id.tv_comment_content);
        TextView tvTime = view.findViewById(R.id.tv_comment_time);
        LinearLayout llReplies = view.findViewById(R.id.ll_replies);
        
        tvNickname.setText(comment.getNickname());
        tvContent.setText(comment.getContent());
        tvTime.setText(formatTime(comment.getCreateTime()));
        
        if (comment.getAvatar() != null && !comment.getAvatar().isEmpty()) {
            new ImageLoadTask(ivAvatar).execute(comment.getAvatar());
        }
        
        if (comment.getReplyComments() != null && !comment.getReplyComments().isEmpty()) {
            llReplies.setVisibility(View.VISIBLE);
            llReplies.removeAllViews();
            for (Comment reply : comment.getReplyComments()) {
                View replyView = createCommentView(reply, level + 1);
                if (replyView != null) {
                    llReplies.addView(replyView);
                }
            }
        } else {
            llReplies.setVisibility(View.GONE);
        }
        
        return view;
    }

    private void sendComment() {
        if (!isLoggedIn) {
            showLoginDialog();
            return;
        }
        
        String content = etComment.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
            return;
        }
        
        tvSendComment.setEnabled(false);
        tvSendComment.setText("发送中...");

        BlogApiService.addComment(blogId, userNickname, content, userAvatar, null, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvSendComment.setEnabled(true);
                        tvSendComment.setText("发送");
                        
                        int code = response.optInt("code", -1);
                        if (code == 200) {
                            etComment.setText("");
                            Toast.makeText(BlogDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                            loadComments();
                        } else {
                            String message = response.optString("message", "评论失败");
                            Toast.makeText(BlogDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(final String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvSendComment.setEnabled(true);
                        tvSendComment.setText("发送");
                        Toast.makeText(BlogDetailActivity.this, "评论失败: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("评论功能需要先登录，是否前往登录？")
                .setPositiveButton("去登录", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        Intent intent = new Intent(BlogDetailActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String formatCount(int count) {
        if (count >= 10000) {
            return String.format(Locale.getDefault(), "%.1fw", count / 10000.0);
        } else if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1fk", count / 1000.0);
        }
        return String.valueOf(count);
    }

    private String formatTime(long timestamp) {
        if (timestamp <= 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewRef;

        ImageLoadTask(ImageView imageView) {
            this.imageViewRef = new WeakReference<>(imageView);
        }

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
            ImageView imageView = imageViewRef.get();
            if (result != null && imageView != null) {
                imageView.setImageBitmap(result);
            }
        }
    }
}
