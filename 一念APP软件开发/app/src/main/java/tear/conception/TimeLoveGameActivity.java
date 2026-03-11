package tear.conception;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tear.conception.ui.view.PetalParticleView;
import tear.conception.ui.view.RippleWaveView;
import tear.conception.util.SharedPreferencesUtil;

public class TimeLoveGameActivity extends Activity {

    private FrameLayout gameContainer;
    private ImageView ivBackground;
    private PetalParticleView petalParticleView;
    private TextView tvChapterTitle;
    private TextView tvChapterYear;
    private TextView tvChapterContent;
    private TextView tvPetalCount;
    private TextView tvProgress;
    private TextView tvHint;
    private LinearLayout llChapterInfo;
    private FrameLayout endingOverlay;
    private TextView tvEndingTitle;
    private TextView tvEndingContent;
    private PetalParticleView endingPetalView;
    private TextView tvEndingTarget;
    private TextView btnReplay;
    private TextView btnClose;

    private FrameLayout introOverlay;
    private RippleWaveView introRippleView;
    private PetalParticleView introPetalView;
    private TextView btnAccept;
    private TextView btnDecline;

    private SharedPreferencesUtil prefsUtil;
    
    private MediaPlayer bgmPlayer;
    
    private long userId = 0;
    private String targetName = "";
    
    private int currentChapter = 1;
    private int petalCount = 0;
    private int[] chapterThresholds = {10, 25, 45, 70};
    
    private boolean isGameCompleted = false;
    private boolean isShowingEnding = false;
    
    private String[] chapterTitles = {
        "初见",
        "心动", 
        "未说出口",
        "告白"
    };
    
    private String[] chapterYears = {
        "2008年 · 小学",
        "初中时光",
        "初中毕业",
        "现在"
    };
    
    private String[] chapterContents = {
        "2008年的夏天，\n你干净又好看，\n是我藏在心底的第一份喜欢。",
        "为数不多的聊天，\n我却记了好多年。",
        "那天我送去毕业证，\n之后我们，再也没有见过。",
        "时隔多年，\n我喜欢你，一直都是。"
    };
    
    private String[] chapterHints = {
        "点击屏幕，让花瓣飘落",
        "继续累积思念...",
        "每一片花瓣都是对你的想念",
        "即将揭晓..."
    };
    
    private int[] backgroundResIds;
    
    private String getKey(String baseKey) {
        return baseKey + "_" + userId;
    }

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
        
        setContentView(R.layout.activity_time_love_game);

        prefsUtil = SharedPreferencesUtil.getInstance(this);
        
        userId = prefsUtil.getLong("user_id", 0);
        targetName = prefsUtil.getString("love_target_name", "");
        
        if (targetName == null || targetName.isEmpty()) {
            targetName = "你心中的TA";
        }
        
        backgroundResIds = new int[] {
            R.drawable.time1,
            R.drawable.time2,
            R.drawable.time3,
            R.drawable.time4
        };
        
        Log.d("TimeLoveGame", "Background resource IDs: " + 
            backgroundResIds[0] + ", " + backgroundResIds[1] + ", " + 
            backgroundResIds[2] + ", " + backgroundResIds[3]);
        
        loadGameData();
        
        initViews();
        setupTouchListener();
        
        startBgm();
        
        if (!hasAcceptedIntro()) {
            showIntroDialog();
        } else {
            startGame();
        }
    }
    
    private void startBgm() {
        try {
            bgmPlayer = MediaPlayer.create(this, R.raw.shouhou);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
                bgmPlayer.setVolume(0.7f, 0.7f);
                bgmPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void stopBgm() {
        if (bgmPlayer != null) {
            try {
                if (bgmPlayer.isPlaying()) {
                    bgmPlayer.stop();
                }
                bgmPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            bgmPlayer = null;
        }
    }
    
    private boolean hasAcceptedIntro() {
        return prefsUtil.getBoolean("time_love_intro_accepted_" + userId, false);
    }
    
    private void showIntroDialog() {
        introOverlay = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.dialog_game_intro, gameContainer, false);
        gameContainer.addView(introOverlay);
        
        introRippleView = introOverlay.findViewById(R.id.ripple_wave_view);
        introPetalView = introOverlay.findViewById(R.id.petal_view);
        btnAccept = introOverlay.findViewById(R.id.btn_accept);
        btnDecline = introOverlay.findViewById(R.id.btn_decline);
        
        introRippleView.startRippleAnimation();
        introPetalView.startContinuousMode();
        
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptIntro();
            }
        });
        
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineIntro();
            }
        });
    }
    
    private void acceptIntro() {
        prefsUtil.putBoolean("time_love_intro_accepted_" + userId, true);
        
        introOverlay.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (introRippleView != null) introRippleView.stopAnimation();
                    if (introPetalView != null) introPetalView.stopAnimation();
                    gameContainer.removeView(introOverlay);
                    introOverlay = null;
                    startGame();
                }
            })
            .start();
    }
    
    private void declineIntro() {
        finish();
    }
    
    private void startGame() {
        updateBackgroundImage();
        updateUIWithoutAnimation();
        
        petalParticleView.startContinuousMode();
        
        if (isGameCompleted) {
            showEndingDirectly();
        } else {
            startChapterAnimation();
        }
    }
    
    private void updateBackgroundImage() {
        int bgIndex = currentChapter - 1;
        Log.d("TimeLoveGame", "updateBackgroundImage: currentChapter=" + currentChapter + ", bgIndex=" + bgIndex);
        if (bgIndex >= 0 && bgIndex < backgroundResIds.length) {
            int bgResId = backgroundResIds[bgIndex];
            Log.d("TimeLoveGame", "Setting background resource: " + bgResId);
            if (bgResId != 0) {
                ivBackground.setImageResource(bgResId);
            } else {
                Log.e("TimeLoveGame", "Background resource ID is 0!");
                ivBackground.setBackgroundColor(Color.parseColor("#1a1a2e"));
            }
        } else {
            Log.d("TimeLoveGame", "No background image for this chapter, using dark background");
            ivBackground.setBackgroundColor(Color.parseColor("#1a1a2e"));
        }
    }

    private void initViews() {
        gameContainer = findViewById(R.id.game_container);
        ivBackground = findViewById(R.id.iv_background);
        petalParticleView = findViewById(R.id.petal_particle_view);
        tvChapterTitle = findViewById(R.id.tv_chapter_title);
        tvChapterYear = findViewById(R.id.tv_chapter_year);
        tvChapterContent = findViewById(R.id.tv_chapter_content);
        tvPetalCount = findViewById(R.id.tv_petal_count);
        tvProgress = findViewById(R.id.tv_progress);
        tvHint = findViewById(R.id.tv_hint);
        llChapterInfo = findViewById(R.id.ll_chapter_info);
        endingOverlay = findViewById(R.id.ending_overlay);
        tvEndingTitle = findViewById(R.id.tv_ending_title);
        tvEndingContent = findViewById(R.id.tv_ending_content);
        endingPetalView = findViewById(R.id.ending_petal_view);
        tvEndingTarget = findViewById(R.id.tv_ending_target);
        btnReplay = findViewById(R.id.btn_replay);
        btnClose = findViewById(R.id.btn_close);
        
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        if (btnReplay != null) {
            btnReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showReplayConfirmDialog();
                }
            });
        }
        
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    
    private void showReplayConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("重新开始")
            .setMessage("确定要重新开始游戏吗？\n所有进度将被重置。")
            .setPositiveButton("重新开始", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    resetGame();
                }
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void setupTouchListener() {
        gameContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isShowingEnding && !isGameCompleted && introOverlay == null) {
                        handleTouch(event.getX(), event.getY());
                    }
                }
                return true;
            }
        });
    }
    
    private void handleTouch(float x, float y) {
        petalParticleView.addPetalOnTouch(x, y);
        
        petalCount++;
        saveGameData();
        updatePetalDisplay();
        
        checkChapterProgress();
    }
    
    private void updatePetalDisplay() {
        tvPetalCount.setText(String.valueOf(petalCount));
        
        int threshold = chapterThresholds[currentChapter - 1];
        int progress = (int) ((float) petalCount / threshold * 100);
        progress = Math.min(progress, 100);
        tvProgress.setText(progress + "%");
        
        ObjectAnimator scaleAnim = ObjectAnimator.ofPropertyValuesHolder(
            tvPetalCount,
            android.animation.PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f, 1f),
            android.animation.PropertyValuesHolder.ofFloat("scaleY", 1f, 1.2f, 1f)
        );
        scaleAnim.setDuration(200);
        scaleAnim.start();
    }
    
    private void checkChapterProgress() {
        if (currentChapter < 4 && petalCount >= chapterThresholds[currentChapter - 1]) {
            advanceToNextChapter();
        } else if (currentChapter == 4 && petalCount >= chapterThresholds[3]) {
            completeGame();
        }
    }
    
    private void advanceToNextChapter() {
        currentChapter++;
        saveGameData();
        
        Log.d("TimeLoveGame", "advanceToNextChapter: newChapter=" + currentChapter);
        
        llChapterInfo.animate()
            .alpha(0f)
            .translationY(-50f)
            .setDuration(500)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    updateBackgroundImage();
                    updateUIWithoutAnimation();
                    startChapterAnimation();
                }
            })
            .start();
    }
    
    private void updateUIWithoutAnimation() {
        tvChapterTitle.setText(chapterTitles[currentChapter - 1]);
        tvChapterYear.setText(chapterYears[currentChapter - 1]);
        tvChapterContent.setText(chapterContents[currentChapter - 1]);
        tvHint.setText(chapterHints[currentChapter - 1]);
        
        tvPetalCount.setText(String.valueOf(petalCount));
        
        int threshold = chapterThresholds[currentChapter - 1];
        int progress = (int) ((float) petalCount / threshold * 100);
        progress = Math.min(progress, 100);
        tvProgress.setText(progress + "%");
    }
    
    private void startChapterAnimation() {
        llChapterInfo.setAlpha(0f);
        llChapterInfo.setTranslationY(50f);
        
        llChapterInfo.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(new OvershootInterpolator())
            .start();
        
        tvHint.setAlpha(0f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvHint.animate()
                    .alpha(0.7f)
                    .setDuration(500)
                    .start();
                startHintPulse();
            }
        }, 1000);
    }
    
    private void startHintPulse() {
        ObjectAnimator pulseAnim = ObjectAnimator.ofFloat(tvHint, "alpha", 0.7f, 0.3f, 0.7f);
        pulseAnim.setDuration(2000);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnim.start();
    }
    
    private void completeGame() {
        isGameCompleted = true;
        isShowingEnding = true;
        prefsUtil.putBoolean(getKey("time_love_completed"), true);
        
        showEndingAnimation();
    }
    
    private void showEndingAnimation() {
        endingOverlay.setVisibility(View.VISIBLE);
        endingOverlay.setAlpha(0f);
        
        endingOverlay.animate()
            .alpha(1f)
            .setDuration(1000)
            .start();
        
        endingPetalView.startAnimation(50);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showEndingText();
            }
        }, 500);
    }
    
    private void showEndingText() {
        tvEndingTarget.setText("致 " + targetName);
        tvEndingContent.setText("");
        
        tvEndingTarget.setAlpha(0f);
        tvEndingTarget.setScaleX(0.5f);
        tvEndingTarget.setScaleY(0.5f);
        
        tvEndingTarget.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setInterpolator(new OvershootInterpolator())
            .start();
        
        final String endingText = 
            "从小学初见\n\n" +
            "到初中心动\n\n" +
            "从毕业分别\n\n" +
            "到时隔多年\n\n" +
            "我喜欢你\n\n" +
            "一直都是，从未改变";
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                typeEndingText(endingText, 0);
            }
        }, 1000);
    }
    
    private void typeEndingText(final String text, final int index) {
        if (index < text.length()) {
            tvEndingContent.setText(text.substring(0, index + 1));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    typeEndingText(text, index + 1);
                }
            }, 80);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    endingPetalView.startAnimation(80);
                    showEndingButtons();
                }
            }, 500);
        }
    }
    
    private void showEndingButtons() {
        if (btnReplay != null) {
            btnReplay.setVisibility(View.VISIBLE);
            btnReplay.setAlpha(0f);
            btnReplay.setScaleX(0.8f);
            btnReplay.setScaleY(0.8f);
            btnReplay.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();
        }
        
        if (btnClose != null) {
            btnClose.setVisibility(View.VISIBLE);
            btnClose.setAlpha(0f);
            btnClose.setScaleX(0.8f);
            btnClose.setScaleY(0.8f);
            btnClose.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setStartDelay(150)
                .setInterpolator(new OvershootInterpolator())
                .start();
        }
    }
    
    private void showEndingDirectly() {
        isShowingEnding = true;
        endingOverlay.setVisibility(View.VISIBLE);
        endingOverlay.setAlpha(1f);
        tvEndingTarget.setText("致 " + targetName);
        tvEndingTarget.setAlpha(1f);
        tvEndingTarget.setScaleX(1f);
        tvEndingTarget.setScaleY(1f);
        tvEndingContent.setText(
            "从小学初见\n\n" +
            "到初中心动\n\n" +
            "从毕业分别\n\n" +
            "到时隔多年\n\n" +
            "我喜欢你\n\n" +
            "一直都是，从未改变"
        );
        endingPetalView.startContinuousMode();
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showEndingButtons();
            }
        }, 500);
    }

    private void loadGameData() {
        if (userId <= 0) {
            petalCount = 0;
            currentChapter = 1;
            isGameCompleted = false;
            return;
        }
        
        petalCount = prefsUtil.getInt(getKey("time_love_petal_count"), 0);
        currentChapter = prefsUtil.getInt(getKey("time_love_chapter"), 1);
        isGameCompleted = prefsUtil.getBoolean(getKey("time_love_completed"), false);
        
        if (currentChapter < 1) currentChapter = 1;
        if (currentChapter > 4) currentChapter = 4;
    }

    private void saveGameData() {
        if (userId <= 0) return;
        
        prefsUtil.putInt(getKey("time_love_petal_count"), petalCount);
        prefsUtil.putInt(getKey("time_love_chapter"), currentChapter);
    }
    
    private void resetGame() {
        petalCount = 0;
        currentChapter = 1;
        isGameCompleted = false;
        isShowingEnding = false;
        
        if (userId > 0) {
            prefsUtil.putInt(getKey("time_love_petal_count"), 0);
            prefsUtil.putInt(getKey("time_love_chapter"), 1);
            prefsUtil.putBoolean(getKey("time_love_completed"), false);
        }
        
        if (btnReplay != null) btnReplay.setVisibility(View.GONE);
        if (btnClose != null) btnClose.setVisibility(View.GONE);
        
        endingOverlay.setVisibility(View.GONE);
        endingPetalView.stopAnimation();
        
        updateBackgroundImage();
        
        updateUIWithoutAnimation();
        startChapterAnimation();
        
        petalParticleView.startContinuousMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBgm();
        if (petalParticleView != null) {
            petalParticleView.stopAnimation();
        }
        if (endingPetalView != null) {
            endingPetalView.stopAnimation();
        }
        if (introRippleView != null) {
            introRippleView.stopAnimation();
        }
        if (introPetalView != null) {
            introPetalView.stopAnimation();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
}
