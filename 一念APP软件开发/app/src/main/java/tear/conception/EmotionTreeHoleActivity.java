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

public class EmotionTreeHoleActivity extends Activity {

    private LinearLayout llBack;
    private LinearLayout llChatContainer;
    private LinearLayout llInputContainer;
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
        
        setContentView(R.layout.activity_emotion_tree_hole);

        initViews();
        setupListeners();
    }

    private void initViews() {
        llBack = findViewById(R.id.ll_back);
        llChatContainer = findViewById(R.id.ll_chat_container);
        llInputContainer = findViewById(R.id.ll_input_container);
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
        addMessageView(message, true, "我");
        
        showLoading(true);

        String prompt = buildEmotionPrompt(message);
        
        BlogApiService.aiChat(prompt, new BlogApiService.ApiCallback() {
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
                                    String aiResponse = data.optString("response", "我在听...");
                                    addMessageView(aiResponse, false, "树洞");
                                }
                            } else {
                                String errorMsg = response.optString("message", "我暂时无法回应，请稍后再试");
                                addMessageView(errorMsg, false, "树洞");
                            }
                        } catch (Exception e) {
                            addMessageView("我暂时无法回应，请稍后再试", false, "树洞");
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
                        addMessageView("网络似乎不太顺畅，但我一直在", false, "树洞");
                    }
                });
            }
        });
    }

    private String buildEmotionPrompt(String userMessage) {
        return "你是一个温暖、善解人意的情感树洞。用户向你倾诉心事，请用温柔、理解、治愈的语气回应。" +
               "回应要简短（不超过100字），真诚，有共情力。不要说教，只是倾听和陪伴。" +
               "用户说：" + userMessage;
    }

    private void addMessageView(String message, boolean isUser, String label) {
        View messageView = LayoutInflater.from(this).inflate(
                R.layout.item_emotion_message, llChatContainer, false);
        
        TextView tvAvatar = messageView.findViewById(R.id.tv_avatar);
        TextView tvLabel = messageView.findViewById(R.id.tv_label);
        TextView tvMessage = messageView.findViewById(R.id.tv_message);
        
        tvLabel.setText(label);
        tvMessage.setText(message);
        
        if (isUser) {
            tvAvatar.setText("😊");
            tvMessage.setBackgroundResource(R.drawable.bg_message_user);
            tvMessage.setTextColor(getResources().getColor(R.color.white));
        } else {
            tvAvatar.setText("☁");
            tvMessage.setBackgroundResource(R.drawable.bg_message_ai);
            tvMessage.setTextColor(getResources().getColor(R.color.love_primary));
        }
        
        llChatContainer.addView(messageView);
        scrollToBottom();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvSend.setEnabled(!show);
    }

    private void scrollToBottom() {
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }, 100);
    }
}
