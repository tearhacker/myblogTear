package tear.conception;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tear.conception.module.BlogApiService;
import tear.conception.util.SharedPreferencesUtil;

public class LoveLetterActivity extends Activity {

    private LinearLayout llBackHeader;
    private LinearLayout llLetterContainer;
    private LinearLayout llInputContainer;
    private LinearLayout llActionButtons;
    private LinearLayout llBackButton;
    private TextView tvLetterContent;
    private TextView tvLetterDate;
    private EditText etUserWords;
    private TextView btnGenerate;
    private TextView btnRegenerate;
    private TextView btnSave;
    private TextView btnBack;
    private ProgressBar progressBar;
    private TextView tvLoadingText;

    private SharedPreferencesUtil prefsUtil;
    private int loveDays = 0;
    private String targetName = "欧阳颖";
    private String currentLetter = "";

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
        
        setContentView(R.layout.activity_love_letter);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        loveDays = prefsUtil.getInt("love_total_days", 0);
        targetName = prefsUtil.getString("target_name", "欧阳颖");

        initViews();
        setupListeners();
    }

    private void initViews() {
        llBackHeader = findViewById(R.id.ll_back_header);
        llLetterContainer = findViewById(R.id.ll_letter_container);
        llInputContainer = findViewById(R.id.ll_input_container);
        llActionButtons = findViewById(R.id.ll_action_buttons);
        llBackButton = findViewById(R.id.ll_back_button);
        tvLetterContent = findViewById(R.id.tv_letter_content);
        tvLetterDate = findViewById(R.id.tv_letter_date);
        etUserWords = findViewById(R.id.et_user_words);
        btnGenerate = findViewById(R.id.btn_generate);
        btnRegenerate = findViewById(R.id.btn_regenerate);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        progressBar = findViewById(R.id.progress_bar);
        tvLoadingText = findViewById(R.id.tv_loading_text);
    }

    private void setupListeners() {
        llBackHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateLetter();
            }
        });

        btnRegenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateLetter();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLetter();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void generateLetter() {
        String userWords = etUserWords.getText().toString().trim();
        
        showLoading(true);

        String prompt = buildLetterPrompt(userWords);

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
                                    String letter = data.optString("response", "");
                                    showLetter(letter);
                                }
                            } else {
                                Toast.makeText(LoveLetterActivity.this, "生成失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoveLetterActivity.this, "生成失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoveLetterActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String buildLetterPrompt(String userWords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位浪漫的情书作家。请为用户写一封真挚动人的情书。\n\n");
        prompt.append("背景信息：\n");
        prompt.append("- 用户已经持续思念TA（").append(targetName).append("）").append(loveDays).append("天\n");
        prompt.append("- 这是一段从初中开始的暗恋故事\n");
        prompt.append("- 用户想对TA说的话：").append(userWords.isEmpty() ? "（用户没有特别说明，请自由发挥）" : userWords).append("\n\n");
        prompt.append("要求：\n");
        prompt.append("1. 情感真挚，语言优美\n");
        prompt.append("2. 字数200-300字\n");
        prompt.append("3. 不要太肉麻，要真诚\n");
        prompt.append("4. 结尾要有期待和祝福\n");
        
        return prompt.toString();
    }

    private void showLetter(String letter) {
        currentLetter = letter;
        
        llInputContainer.setVisibility(View.GONE);
        llLetterContainer.setVisibility(View.VISIBLE);
        llActionButtons.setVisibility(View.VISIBLE);
        llBackButton.setVisibility(View.VISIBLE);
        
        tvLetterContent.setText(letter);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        tvLetterDate.setText(sdf.format(new Date()));
    }

    private void saveLetter() {
        if (currentLetter.isEmpty()) {
            return;
        }
        
        int letterCount = prefsUtil.getInt("love_letter_count", 0);
        letterCount++;
        prefsUtil.putInt("love_letter_count", letterCount);
        prefsUtil.putString("love_letter_" + letterCount, currentLetter);
        prefsUtil.putString("love_letter_date_" + letterCount, 
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        
        Toast.makeText(this, "情书已保存到回忆中", Toast.LENGTH_SHORT).show();
        
        btnSave.setText("已保存");
        btnSave.setEnabled(false);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvLoadingText.setVisibility(show ? View.VISIBLE : View.GONE);
        btnGenerate.setEnabled(!show);
        btnRegenerate.setEnabled(!show);
    }
}
