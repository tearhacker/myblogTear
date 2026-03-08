package tear.conception;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import tear.conception.module.BlogApiService;
import tear.conception.ui.view.HeartParticleView;
import tear.conception.ui.view.RippleView;
import tear.conception.ui.view.StarParticleView;
import tear.conception.EmotionTreeHoleActivity;
import tear.conception.LoginActivity;
import tear.conception.R;
import tear.conception.util.SharedPreferencesUtil;

public class LoveSigninActivity extends Activity {

    private FrameLayout btnLoveSigninContainer;
    private TextView tvHeart;
    private TextView tvLoveSigninText;
    private TextView tvLoveDays;
    private TextView tvLoveMessage;
    private TextView tvLoveStatus;
    private TextView tvTotalDays;
    private TextView tvContinuousDays;
    private TextView tvTargetTitle;
    private EditText etTargetName;
    private RippleView rippleView;
    private StarParticleView starParticleView;
    private HeartParticleView heartParticleView;
    private View totalDaysCard;
    private View continuousDaysCard;
    private LinearLayout llEmotionTreeHole;
    private LinearLayout llDeveloperInfo;

    private SharedPreferencesUtil prefsUtil;
    private ObjectAnimator breathAnimator;
    
    private boolean isSignedInToday = false;
    private int currentDays = 0;
    private int totalDays = 0;
    private long userId = 0;
    private String qqNumber = "";
    private String targetName = "欧阳颖";
    
    private int totalDaysTapCount = 0;
    private long lastTotalDaysTapTime = 0;
    private int continuousDaysTapCount = 0;
    private long lastContinuousDaysTapTime = 0;
    private static final int SECRET_TAP_THRESHOLD = 3;
    private static final long SECRET_TAP_INTERVAL = 3000;

    private static final int REQUEST_LOGIN = 1002;
    private static final int REQUEST_STAR_CONFESSION = 1003;
    private static final int REQUEST_MEMORY_RECORD = 1004;

    private String[] loveMessages = {
        "每一次想起你，心都会微微颤动",
        "时光荏苒，对你的思念却从未减少",
        "愿你被这个世界温柔以待",
        "在最美的年华里遇见你，是我最大的幸运",
        "一念情深，念念不忘",
        "你的笑容是我最美的回忆",
        "初中那年的阳光，照进了我的心里",
        "时光不老，我们不散",
        "愿所有的美好都如期而至",
        "你是我青春里最美的风景"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_signin);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        userId = prefsUtil.getLong("user_id", 0);
        qqNumber = prefsUtil.getString("qq_number", "");
        targetName = prefsUtil.getString("love_target_name", "欧阳颖");

        initViews();
        setupListeners();
        
        if (userId > 0) {
            loadFromServer();
        } else {
            showLoginRequired();
        }
        startBreathAnimation();
    }

    private void initViews() {
        btnLoveSigninContainer = findViewById(R.id.btn_love_signin_container);
        tvHeart = findViewById(R.id.tv_heart);
        tvLoveSigninText = findViewById(R.id.tv_love_signin_text);
        tvLoveDays = findViewById(R.id.tv_love_days);
        tvLoveMessage = findViewById(R.id.tv_love_message);
        tvLoveStatus = findViewById(R.id.tv_love_status);
        tvTotalDays = findViewById(R.id.tv_total_days);
        tvContinuousDays = findViewById(R.id.tv_continuous_days);
        tvTargetTitle = findViewById(R.id.tv_target_title);
        etTargetName = findViewById(R.id.et_target_name);
        rippleView = findViewById(R.id.ripple_view);
        starParticleView = findViewById(R.id.star_particle_view);
        heartParticleView = findViewById(R.id.heart_particle_view);
        
        totalDaysCard = (View) findViewById(R.id.tv_total_days).getParent();
        continuousDaysCard = (View) findViewById(R.id.tv_continuous_days).getParent();
        llEmotionTreeHole = findViewById(R.id.ll_emotion_tree_hole);
        llDeveloperInfo = findViewById(R.id.ll_developer_info);
        
        updateTargetTitle();
    }

    private void updateTargetTitle() {
        if (tvTargetTitle != null) {
            tvTargetTitle.setText("致 " + targetName);
        }
    }

    private void setupListeners() {
        btnLoveSigninContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoveSigninClicked();
            }
        });
        
        if (totalDaysCard != null) {
            totalDaysCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkTotalDaysTap();
                }
            });
        }
        
        if (continuousDaysCard != null) {
            continuousDaysCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkContinuousDaysTap();
                }
            });
        }
        
        if (llEmotionTreeHole != null) {
            llEmotionTreeHole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openEmotionTreeHole();
                }
            });
        }
        
        if (llDeveloperInfo != null) {
            llDeveloperInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeveloperInfo();
                }
            });
        }
    }
    
    private void checkTotalDaysTap() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastTotalDaysTapTime < SECRET_TAP_INTERVAL) {
            totalDaysTapCount++;
            if (totalDaysTapCount >= SECRET_TAP_THRESHOLD) {
                openStarConfession();
                totalDaysTapCount = 0;
            }
        } else {
            totalDaysTapCount = 1;
        }
        
        lastTotalDaysTapTime = currentTime;
    }
    
    private void checkContinuousDaysTap() {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastContinuousDaysTapTime < SECRET_TAP_INTERVAL) {
            continuousDaysTapCount++;
            if (continuousDaysTapCount >= SECRET_TAP_THRESHOLD) {
                openMemoryRecord();
                continuousDaysTapCount = 0;
            }
        } else {
            continuousDaysTapCount = 1;
        }
        
        lastContinuousDaysTapTime = currentTime;
    }
    
    private void openStarConfession() {
        Intent intent = new Intent(this, StarConfessionActivity.class);
        startActivityForResult(intent, REQUEST_STAR_CONFESSION);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    private void openMemoryRecord() {
        Intent intent = new Intent(this, MemoryRecordActivity.class);
        startActivityForResult(intent, REQUEST_MEMORY_RECORD);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openEmotionTreeHole() {
        Intent intent = new Intent(this, EmotionTreeHoleActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openDeveloperInfo() {
        Intent intent = new Intent(this, DeveloperInfoActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void showLoginRequired() {
        tvLoveDays.setText("0");
        tvContinuousDays.setText("0");
        tvTotalDays.setText("0");
        btnLoveSigninContainer.setEnabled(true);
        tvLoveSigninText.setText("登录");
        tvLoveStatus.setText("请先登录，记录你的思念");
        tvLoveMessage.setVisibility(View.GONE);
        if (etTargetName != null) {
            etTargetName.setVisibility(View.GONE);
        }
    }

    private void loadFromServer() {
        BlogApiService.getTodayLoveSignin(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        final JSONObject data = response.optJSONObject("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data != null) {
                                    isSignedInToday = true;
                                    currentDays = data.optInt("continuousDays", 1);
                                    totalDays = data.optInt("totalDays", 1);
                                    String message = data.optString("loveMessage", "");
                                    String serverTargetName = data.optString("targetName", "");
                                    if (!serverTargetName.isEmpty()) {
                                        targetName = serverTargetName;
                                        prefsUtil.putString("love_target_name", targetName);
                                        updateTargetTitle();
                                    }
                                    
                                    tvLoveDays.setText(String.valueOf(currentDays));
                                    tvContinuousDays.setText(String.valueOf(currentDays));
                                    tvTotalDays.setText(String.valueOf(totalDays));
                                    
                                    btnLoveSigninContainer.setEnabled(false);
                                    tvLoveSigninText.setText("已念你");
                                    tvLoveStatus.setText("今日已念，明日继续~");
                                    stopBreathAnimation();
                                    
                                    if (!message.isEmpty()) {
                                        tvLoveMessage.setText(message);
                                        tvLoveMessage.setVisibility(View.VISIBLE);
                                    }
                                    if (etTargetName != null) {
                                        etTargetName.setVisibility(View.GONE);
                                    }
                                } else {
                                    loadLoveHistoryFromServer();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoveSigninActivity.this, "网络错误，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadLoveHistoryFromServer() {
        BlogApiService.getLoveSigninHistory(userId, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        final JSONObject data = response.optJSONObject("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (data != null) {
                                    currentDays = data.optInt("continuousDays", 0);
                                    totalDays = data.optInt("totalDays", 0);
                                    String serverTargetName = data.optString("targetName", "");
                                    if (!serverTargetName.isEmpty()) {
                                        targetName = serverTargetName;
                                        prefsUtil.putString("love_target_name", targetName);
                                        updateTargetTitle();
                                    }
                                    tvLoveDays.setText(String.valueOf(currentDays));
                                    tvContinuousDays.setText(String.valueOf(currentDays));
                                    tvTotalDays.setText(String.valueOf(totalDays));
                                }
                                btnLoveSigninContainer.setEnabled(true);
                                tvLoveSigninText.setText("念你");
                                tvLoveStatus.setText("");
                                if (etTargetName != null) {
                                    etTargetName.setVisibility(View.VISIBLE);
                                    etTargetName.setHint("喜欢TA:欧阳颖");
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void startBreathAnimation() {
        if (isSignedInToday) return;
        
        breathAnimator = ObjectAnimator.ofFloat(tvHeart, "alpha", 1f, 0.5f, 1f);
        breathAnimator.setDuration(1200);
        breathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathAnimator.start();
    }

    private void stopBreathAnimation() {
        if (breathAnimator != null) {
            breathAnimator.cancel();
            tvHeart.setAlpha(1f);
        }
    }

    private void onLoveSigninClicked() {
        if (userId > 0 && !qqNumber.isEmpty()) {
            doServerLoveSignin();
        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == RESULT_OK) {
            userId = prefsUtil.getLong("user_id", 0);
            qqNumber = prefsUtil.getString("qq_number", "");
            if (userId > 0) {
                loadFromServer();
            }
        } else if (requestCode == REQUEST_STAR_CONFESSION && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("accepted", false)) {
                showHeartMark();
            }
        }
    }
    
    private void showHeartMark() {
        if (tvTotalDays != null) {
            String currentText = tvTotalDays.getText().toString();
            tvTotalDays.setText(currentText + " ❤");
            tvTotalDays.setTextColor(getResources().getColor(R.color.love_heart));
        }
    }

    private void doServerLoveSignin() {
        btnLoveSigninContainer.setEnabled(false);
        
        String inputTargetName = "欧阳颖";
        if (etTargetName != null) {
            String input = etTargetName.getText().toString().trim();
            if (!input.isEmpty()) {
                inputTargetName = input;
            }
        }
        
        if (!inputTargetName.equals(targetName)) {
            targetName = inputTargetName;
            prefsUtil.putString("love_target_name", targetName);
            updateTargetTitle();
        }
        
        final String finalTargetName = inputTargetName;
        
        BlogApiService.doLoveSignin(userId, qqNumber, finalTargetName, new BlogApiService.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code == 200) {
                        final JSONObject data = response.getJSONObject("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentDays = data.optInt("continuousDays", 1);
                                totalDays = data.optInt("totalDays", 1);
                                final String message = data.optString("loveMessage", "");
                                final String serverTargetName = data.optString("targetName", finalTargetName);
                                targetName = serverTargetName;
                                prefsUtil.putString("love_target_name", targetName);
                                updateTargetTitle();
                                
                                playLoveSigninAnimation(message);
                            }
                        });
                    } else {
                        final String msg = response.optString("message", "签到失败");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnLoveSigninContainer.setEnabled(true);
                                Toast.makeText(LoveSigninActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnLoveSigninContainer.setEnabled(true);
                            Toast.makeText(LoveSigninActivity.this, "签到失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLoveSigninContainer.setEnabled(true);
                        Toast.makeText(LoveSigninActivity.this, "网络错误: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void playLoveSigninAnimation(final String message) {
        stopBreathAnimation();
        
        rippleView.startRipple();
        starParticleView.startAnimation(15);
        heartParticleView.startAnimation(25);
        
        animateDaysCounter(currentDays);
        
        btnLoveSigninContainer.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(150)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    btnLoveSigninContainer.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                }
            })
            .start();
        
        tvLoveSigninText.setText("已念你");
        tvContinuousDays.setText(String.valueOf(currentDays));
        tvTotalDays.setText(String.valueOf(totalDays));
        
        if (etTargetName != null) {
            etTargetName.setVisibility(View.GONE);
        }
        
        final String displayMessage = message.isEmpty() ? 
            loveMessages[new java.util.Random().nextInt(loveMessages.length)] : message;
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isSignedInToday = true;
                btnLoveSigninContainer.setEnabled(false);
                tvLoveStatus.setText("今日已念，明日继续~");
                
                tvLoveMessage.setText(displayMessage);
                tvLoveMessage.setVisibility(View.VISIBLE);
                tvLoveMessage.setAlpha(0f);
                tvLoveMessage.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .start();
            }
        }, 800);
    }

    private void animateDaysCounter(int to) {
        int from = currentDays > 0 ? currentDays - 1 : 0;
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(1000);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                tvLoveDays.setText(String.valueOf(value));
            }
        });
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBreathAnimation();
        if (starParticleView != null) {
            starParticleView.stopAnimation();
        }
        if (heartParticleView != null) {
            heartParticleView.stopAnimation();
        }
        if (rippleView != null) {
            rippleView.stopRipple();
        }
    }
}
