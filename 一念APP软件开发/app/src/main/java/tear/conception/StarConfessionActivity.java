package tear.conception;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import tear.conception.LoveLetterActivity;
import tear.conception.ui.view.StarTrailView;
import tear.conception.util.SharedPreferencesUtil;

public class StarConfessionActivity extends Activity {

    private StarTrailView starTrailView;
    private TextView tvHint;
    private TextView tvIntro;
    private FrameLayout memoryBubbleContainer;
    private LinearLayout memoryBubble;
    private TextView tvMemoryTitle;
    private TextView tvMemoryContent;
    private TextView btnNextMemory;
    private ScrollView scrollViewMemory;
    private LinearLayout buttonsContainer;
    private TextView btnAccept;
    private TextView btnWait;
    private FrameLayout resultOverlay;
    private TextView tvResultHeart;
    private TextView tvResultMessage;

    private SharedPreferencesUtil prefsUtil;
    private int currentMemoryIndex = 0;
    private int activatedStars = 0;
    private boolean isShowingMemory = false;
    private boolean isTyping = false;
    private Handler typingHandler = new Handler();
    private int charIndex = 0;
    private String currentTypingText = "";
    
    private MediaPlayer bgmPlayer;

    private static final int TOTAL_STARS = 3;
    private static final int TYPING_DELAY = 35;

    private String[] memoryTitles;
    private String[] memoryContents;
    private String[] buttonTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_confession);

        prefsUtil = SharedPreferencesUtil.getInstance(this);

        initStrings();
        initViews();
        setupListeners();
        startBgm();
        startIntroSequence();
    }

    private void initStrings() {
        memoryTitles = new String[]{
            getString(R.string.star_trail_memory_1_title),
            getString(R.string.star_trail_memory_2_title),
            getString(R.string.star_trail_memory_3_title)
        };
        memoryContents = new String[]{
            getString(R.string.star_trail_memory_1),
            getString(R.string.star_trail_memory_2),
            getString(R.string.star_trail_memory_3)
        };
        buttonTexts = new String[]{
            getString(R.string.star_trail_btn_next),
            getString(R.string.star_trail_btn_last_star),
            getString(R.string.star_trail_btn_ready)
        };
    }

    private void initViews() {
        starTrailView = findViewById(R.id.star_trail_view);
        tvHint = findViewById(R.id.tv_hint);
        tvIntro = findViewById(R.id.tv_intro);
        memoryBubbleContainer = findViewById(R.id.memory_bubble_container);
        memoryBubble = findViewById(R.id.memory_bubble);
        tvMemoryTitle = findViewById(R.id.tv_memory_title);
        tvMemoryContent = findViewById(R.id.tv_memory_content);
        btnNextMemory = findViewById(R.id.btn_next_memory);
        scrollViewMemory = (ScrollView) findViewById(R.id.tv_memory_content).getParent();
        buttonsContainer = findViewById(R.id.buttons_container);
        btnAccept = findViewById(R.id.btn_accept);
        btnWait = findViewById(R.id.btn_wait);
        resultOverlay = findViewById(R.id.result_overlay);
        tvResultHeart = findViewById(R.id.tv_result_heart);
        tvResultMessage = findViewById(R.id.tv_result_message);
    }

    private void setupListeners() {
        starTrailView.setOnStarClickListener(new StarTrailView.OnStarClickListener() {
            @Override
            public void onStarClick(int starIndex, int totalStars) {
                if (isShowingMemory) return;
                if (starIndex == activatedStars) {
                    handleStarClick(starIndex);
                }
            }
        });

        btnNextMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTyping) {
                    skipTyping();
                } else {
                    hideMemoryBubble();
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcceptClicked();
            }
        });

        btnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWaitClicked();
            }
        });
    }

    private void startIntroSequence() {
        starTrailView.postDelayed(new Runnable() {
            @Override
            public void run() {
                starTrailView.startIntroAnimation(TOTAL_STARS);
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showIntroText();
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showHint();
            }
        }, 4500);
    }

    private void showIntroText() {
        tvIntro.animate()
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvIntro.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .start();
            }
        }, 4000);
    }

    private void showHint() {
        tvHint.animate()
            .alpha(1f)
            .setDuration(500)
            .start();

        startHintPulseAnimation();
    }

    private void startHintPulseAnimation() {
        ObjectAnimator pulseAnim = ObjectAnimator.ofFloat(tvHint, "alpha", 1f, 0.4f, 1f);
        pulseAnim.setDuration(2000);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnim.start();
    }

    private void handleStarClick(int starIndex) {
        starTrailView.activateStar(starIndex);
        activatedStars++;
        currentMemoryIndex = starIndex;

        tvHint.animate().alpha(0f).setDuration(200).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showMemoryBubble(currentMemoryIndex);
            }
        }, 600);
    }

    private void showMemoryBubble(final int index) {
        isShowingMemory = true;

        tvMemoryTitle.setText(memoryTitles[index]);
        tvMemoryContent.setText("");
        btnNextMemory.setText("点击跳过");
        btnNextMemory.setEnabled(false);

        memoryBubbleContainer.setVisibility(View.VISIBLE);
        memoryBubbleContainer.setAlpha(0f);
        memoryBubbleContainer.setScaleX(0.8f);
        memoryBubbleContainer.setScaleY(0.8f);

        memoryBubbleContainer.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(new OvershootInterpolator())
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startTypingEffect(memoryContents[index]);
                }
            })
            .start();
    }

    private void startTypingEffect(final String text) {
        isTyping = true;
        currentTypingText = text;
        charIndex = 0;
        tvMemoryContent.setText("");

        final SpannableStringBuilder builder = new SpannableStringBuilder();
        final int memoryIndex = currentMemoryIndex;

        Runnable typingRunnable = new Runnable() {
            @Override
            public void run() {
                if (charIndex < text.length()) {
                    builder.append(text.charAt(charIndex));
                    tvMemoryContent.setText(builder);
                    
                    if (scrollViewMemory != null) {
                        scrollViewMemory.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollViewMemory.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                    
                    charIndex++;
                    typingHandler.postDelayed(this, TYPING_DELAY);
                } else {
                    isTyping = false;
                    btnNextMemory.setText(buttonTexts[memoryIndex]);
                    btnNextMemory.setEnabled(true);
                }
            }
        };
        typingHandler.postDelayed(typingRunnable, 300);
    }

    private void skipTyping() {
        typingHandler.removeCallbacksAndMessages(null);
        tvMemoryContent.setText(currentTypingText);
        isTyping = false;
        btnNextMemory.setText(buttonTexts[currentMemoryIndex]);
        btnNextMemory.setEnabled(true);
    }

    private void hideMemoryBubble() {
        typingHandler.removeCallbacksAndMessages(null);
        
        memoryBubbleContainer.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(300)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    memoryBubbleContainer.setVisibility(View.GONE);
                    isShowingMemory = false;

                    if (activatedStars >= TOTAL_STARS) {
                        showFinalButtons();
                    } else {
                        tvHint.animate().alpha(1f).setDuration(300).start();
                    }
                }
            })
            .start();
    }

    private void showFinalButtons() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                starTrailView.gatherToCenter();
            }
        }, 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonsContainer.setVisibility(View.VISIBLE);
                buttonsContainer.setAlpha(0f);
                buttonsContainer.setTranslationY(100f);

                buttonsContainer.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
            }
        }, 1800);
    }

    private void onAcceptClicked() {
        prefsUtil.putBoolean("star_confession_accepted", true);
        prefsUtil.putLong("star_confession_time", System.currentTimeMillis());

        showResult(true);
    }

    private void onWaitClicked() {
        prefsUtil.putBoolean("star_confession_waited", true);
        prefsUtil.putLong("star_confession_time", System.currentTimeMillis());

        showResult(false);
    }

    private void showResult(boolean accepted) {
        resultOverlay.setVisibility(View.VISIBLE);
        resultOverlay.setAlpha(0f);

        resultOverlay.animate()
            .alpha(1f)
            .setDuration(500)
            .start();

        if (accepted) {
            tvResultMessage.setText(R.string.star_trail_accept_message);
            tvResultHeart.setScaleX(0f);
            tvResultHeart.setScaleY(0f);

            tvResultHeart.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();

            starTrailView.scatterFromCenter();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoveLetterOption();
                }
            }, 3000);
        } else {
            tvResultMessage.setText(R.string.star_trail_wait_message);
            tvResultHeart.setAlpha(0.5f);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishWithResult(false);
                }
            }, 2500);
        }
    }

    private void showLoveLetterOption() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("💌 写一封情书吧")
                .setMessage("让AI帮你写一封专属情书，记录这份心意？")
                .setPositiveButton("写情书", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        Intent intent = new Intent(StarConfessionActivity.this, LoveLetterActivity.class);
                        startActivity(intent);
                        finishWithResult(true);
                    }
                })
                .setNegativeButton("下次吧", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        finishWithResult(true);
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void finishWithResult(boolean accepted) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("accepted", accepted);
        setResult(RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
    }
    
    private void startBgm() {
        try {
            bgmPlayer = MediaPlayer.create(this, R.raw.yihuistarlove1);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
                bgmPlayer.setVolume(0.6f, 0.6f);
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
                bgmPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typingHandler.removeCallbacksAndMessages(null);
        stopBgm();
        if (starTrailView != null) {
            starTrailView.stopAnimation();
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
}
