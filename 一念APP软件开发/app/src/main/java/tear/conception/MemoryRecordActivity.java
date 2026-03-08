package tear.conception;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tear.conception.module.BlogApiService;
import tear.conception.ui.view.StarTrailView;
import tear.conception.util.SharedPreferencesUtil;

public class MemoryRecordActivity extends Activity {

    private StarTrailView starTrailView;
    private LinearLayout memoryListContainer;
    private TextView tvEmptyHint;
    private FrameLayout addMemoryButton;

    private SharedPreferencesUtil prefsUtil;
    private long userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_record);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        userId = prefsUtil.getLong("user_id", 0);

        initViews();
        setupListeners();
        loadMemoryRecords();
    }

    private void initViews() {
        starTrailView = findViewById(R.id.star_trail_view);
        memoryListContainer = findViewById(R.id.memory_list_container);
        tvEmptyHint = findViewById(R.id.tv_empty_hint);
        addMemoryButton = findViewById(R.id.add_memory_button);

        starTrailView.postDelayed(new Runnable() {
            @Override
            public void run() {
                starTrailView.startIntroAnimation(5);
            }
        }, 300);
    }

    private void setupListeners() {
        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadMemoryRecords() {
        if (userId <= 0) {
            showEmpty();
            return;
        }

        BlogApiService.getLoveSigninHistory(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        final JSONArray data = response.optJSONArray("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data != null && data.length() > 0) {
                                    displayMemoryRecords(data);
                                } else {
                                    showEmpty();
                                }
                            }
                        });
                    } else {
                        showEmpty();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showEmpty();
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showEmpty();
                    }
                });
            }
        });
    }

    private void displayMemoryRecords(JSONArray records) {
        memoryListContainer.removeAllViews();
        tvEmptyHint.setVisibility(View.GONE);

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年M月d日", Locale.getDefault());

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                
                String signinDateStr = record.optString("signinDate", "");
                String loveMessage = record.optString("loveMessage", "");
                int continuousDays = record.optInt("continuousDays", 1);
                int totalDays = record.optInt("totalDays", 1);

                View itemView = LayoutInflater.from(this).inflate(R.layout.item_memory_record, memoryListContainer, false);
                
                TextView tvDate = itemView.findViewById(R.id.tv_memory_date);
                TextView tvDays = itemView.findViewById(R.id.tv_memory_days);
                TextView tvContent = itemView.findViewById(R.id.tv_memory_content);
                TextView tvMessage = itemView.findViewById(R.id.tv_memory_message);

                try {
                    Date date = inputFormat.parse(signinDateStr);
                    tvDate.setText(outputFormat.format(date));
                } catch (Exception e) {
                    tvDate.setText(signinDateStr);
                }

                tvDays.setText("第" + totalDays + "天念你");
                tvContent.setText("念你的第" + continuousDays + "天，" + loveMessage);
                tvMessage.setText(loveMessage);

                memoryListContainer.addView(itemView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEmpty() {
        memoryListContainer.removeAllViews();
        tvEmptyHint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (starTrailView != null) {
            starTrailView.stopAnimation();
        }
    }
}
