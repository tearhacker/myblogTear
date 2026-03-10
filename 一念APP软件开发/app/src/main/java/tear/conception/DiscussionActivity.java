package tear.conception;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;
import tear.conception.R;
import tear.conception.util.SharedPreferencesUtil;

public class DiscussionActivity extends Activity {

    private EditText etTitle;
    private EditText etContent;
    private TextView tvPublish;
    private TextView tvBack;

    private SharedPreferencesUtil prefsUtil;
    private long userId = 0;
    private String qqNumber = "";
    private String nickname = "";
    private String avatar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        userId = prefsUtil.getLong("user_id", 0);
        qqNumber = prefsUtil.getString("qq_number", "");
        nickname = prefsUtil.getString("nickname", "");
        avatar = prefsUtil.getString("avatar", "");

        initViews();
        setupListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_discussion_title);
        etContent = findViewById(R.id.et_discussion_content);
        tvPublish = findViewById(R.id.tv_publish_discussion);
        tvBack = findViewById(R.id.tv_back);
    }

    private void setupListeners() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishDiscussion();
            }
        });
    }

    private void publishDiscussion() {
        if (userId == 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入话题标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "请输入话题内容", Toast.LENGTH_SHORT).show();
            return;
        }

        tvPublish.setEnabled(false);
        tvPublish.setText("发布中...");

        BlogApiService.createDiscussion(userId, qqNumber, nickname, avatar, title, content, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPublish.setEnabled(true);
                        tvPublish.setText("发布");
                        Toast.makeText(DiscussionActivity.this, "发布成功，等待审核", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvPublish.setEnabled(true);
                        tvPublish.setText("发布");
                        Toast.makeText(DiscussionActivity.this, "发布失败：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}