package tear.conception;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;

public class AiChatActivity extends Activity {

    private LinearLayout llBack;
    private LinearLayout llChatContainer;
    private ScrollView scrollView;
    private EditText etMessage;
    private TextView tvSend;
    private ProgressBar progressBar;

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
        
        setContentView(R.layout.activity_ai_chat);

        initViews();
        setupListeners();
    }

    private void initViews() {
        llBack = findViewById(R.id.ll_back);
        llChatContainer = findViewById(R.id.ll_chat_container);
        scrollView = findViewById(R.id.scroll_view);
        etMessage = findViewById(R.id.et_message);
        tvSend = findViewById(R.id.tv_send);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        final String message = etMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        etMessage.setText("");
        addMessageView(message, true);
        
        showLoading(true);

        BlogApiService.aiChat(message, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(final JSONObject response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        try {
                            int code = response.optInt("code", -1);
                            if (code == 200) {
                                JSONObject data = response.optJSONObject("data");
                                if (data != null) {
                                    String aiResponse = data.optString("response", "AI未返回内容");
                                    addMessageView(aiResponse, false);
                                }
                            } else {
                                String errorMsg = response.optString("message", "AI服务暂时不可用");
                                addMessageView(errorMsg, false);
                            }
                        } catch (Exception e) {
                            addMessageView("解析响应失败: " + e.getMessage(), false);
                        }
                    }
                });
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        addMessageView("网络错误: " + error, false);
                    }
                });
            }
        });
    }

    private void addMessageView(String message, boolean isUser) {
        View messageView = LayoutInflater.from(this).inflate(
                R.layout.item_ai_message, llChatContainer, false);
        
        TextView tvMessage = messageView.findViewById(R.id.tv_message);
        TextView tvLabel = messageView.findViewById(R.id.tv_label);
        View viewUser = messageView.findViewById(R.id.view_user_bg);
        View viewAi = messageView.findViewById(R.id.view_ai_bg);
        
        tvMessage.setText(message);
        
        if (isUser) {
            tvLabel.setText("我");
            viewUser.setVisibility(View.VISIBLE);
            viewAi.setVisibility(View.GONE);
            tvMessage.setBackgroundResource(R.drawable.bg_message_user);
        } else {
            tvLabel.setText("AI");
            viewUser.setVisibility(View.GONE);
            viewAi.setVisibility(View.VISIBLE);
            tvMessage.setBackgroundResource(R.drawable.bg_message_ai);
        }
        
        llChatContainer.addView(messageView);
        scrollToBottom();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvSend.setEnabled(!show);
    }

    private void scrollToBottom() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
