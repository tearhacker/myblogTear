package tear.conception;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.media.MediaPlayer;

import tear.conception.ui.view.HeartParticleView;

public class DeveloperInfoActivity extends Activity {

    private LinearLayout llBack;
    private FrameLayout flAvatarContainer;
    private ImageView ivDeveloperAvatar;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private LinearLayout llSectionIdentity;
    private LinearLayout llSectionWhy;
    private LinearLayout llSectionJourney;
    private LinearLayout llSectionToHer;
    private TextView tvIdentityContent;
    private TextView tvWhyContent;
    private LinearLayout llTimeline;
    private TextView tvToHerContent;
    private TextView tvQuote;
    private TextView tvVersion;
    private HeartParticleView heartParticleView;
    private MediaPlayer bgmPlayer;

    private Handler handler = new Handler();
    private int currentTypingPosition = 0;
    private String currentTypingText = "";
    private TextView currentTypingView = null;
    private boolean isTyping = false;

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
        
        setContentView(R.layout.activity_developer_info);

        initViews();
        setupCircleAvatar();
        setupListeners();
        startBgm();
        startAnimationSequence();
    }

    private void initViews() {
        llBack = findViewById(R.id.ll_back);
        flAvatarContainer = findViewById(R.id.fl_avatar_container);
        ivDeveloperAvatar = findViewById(R.id.iv_developer_avatar);
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        llSectionIdentity = findViewById(R.id.ll_section_identity);
        llSectionWhy = findViewById(R.id.ll_section_why);
        llSectionJourney = findViewById(R.id.ll_section_journey);
        llSectionToHer = findViewById(R.id.ll_section_to_her);
        tvIdentityContent = findViewById(R.id.tv_identity_content);
        tvWhyContent = findViewById(R.id.tv_why_content);
        llTimeline = findViewById(R.id.ll_timeline);
        tvToHerContent = findViewById(R.id.tv_to_her_content);
        tvQuote = findViewById(R.id.tv_quote);
        tvVersion = findViewById(R.id.tv_version);
        heartParticleView = findViewById(R.id.heart_particle_view);
    }

    private void setupCircleAvatar() {
        Drawable drawable = ivDeveloperAvatar.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap circleBitmap = createCircleBitmap(bitmap);
            ivDeveloperAvatar.setImageBitmap(circleBitmap);
        }
    }

    private Bitmap createCircleBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);
        
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, size, size);
        
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        
        float cornerRadius = size * 0.16f;
        RectF rect = new RectF(0, 0, size, size);
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(croppedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(6f);
        borderPaint.setColor(getResources().getColor(R.color.love_heart));
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint);
        
        return output;
    }

    private void setupListeners() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        flAvatarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullscreenImage();
            }
        });

        llSectionToHer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heartParticleView.startAnimation(15);
            }
        });
    }

    private void openFullscreenImage() {
        Intent intent = new Intent(this, FullscreenImageActivity.class);
        intent.putExtra("image_res_id", R.drawable.myheada);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startBgm() {
        try {
            bgmPlayer = MediaPlayer.create(this, R.raw.tryremindyou);
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

    private void startAnimationSequence() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateIconIn();
            }
        }, 300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateTitleIn();
            }
        }, 600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSubtitleIn();
            }
        }, 800);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSectionIn(llSectionIdentity, new Runnable() {
                    @Override
                    public void run() {
                        loadIdentityContent();
                    }
                });
            }
        }, 1200);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSectionIn(llSectionWhy, new Runnable() {
                    @Override
                    public void run() {
                        loadWhyContent();
                    }
                });
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSectionIn(llSectionJourney, new Runnable() {
                    @Override
                    public void run() {
                        loadJourneyContent();
                    }
                });
            }
        }, 2800);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSectionIn(llSectionToHer, new Runnable() {
                    @Override
                    public void run() {
                        loadToHerContent();
                    }
                });
            }
        }, 4000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateQuoteIn();
            }
        }, 5500);
    }

    private void animateIconIn() {
        flAvatarContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(new OvershootInterpolator())
                .start();

        ObjectAnimator pulseAnim = ObjectAnimator.ofFloat(flAvatarContainer, "scaleX", 1f, 1.05f, 1f);
        pulseAnim.setDuration(2000);
        pulseAnim.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnim.start();

        ObjectAnimator pulseAnimY = ObjectAnimator.ofFloat(flAvatarContainer, "scaleY", 1f, 1.05f, 1f);
        pulseAnimY.setDuration(2000);
        pulseAnimY.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimY.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimY.start();
    }

    private void animateTitleIn() {
        tvTitle.animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateSubtitleIn() {
        tvSubtitle.animate()
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void animateSectionIn(final View section, final Runnable onComplete) {
        section.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator(0.8f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                })
                .start();
    }

    private void animateQuoteIn() {
        tvQuote.setText("\"不悔年轻，不弃少年\"");
        tvQuote.animate()
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void loadIdentityContent() {
        String content = "就职于广东省深圳市石岩街道毋界文化传媒有限责任公司，岗位为IT部门技术开发专员，负责公司主要网站系统业务的代码开发、项目维护等等。";
        tvIdentityContent.setText(content);
    }

    private void loadWhyContent() {
        String content = "一念本来是为了勉励个人找准自己的人生价值和意义而开发的，同时谐音\"一恋\"，因此加入了一个特别的恋爱系统。\n\n恰巧我也想追回初中那段于我而言弥足珍贵的模糊记忆，不知道是否晚了，也不知道是否此音能否抵达，我只能用我职业的方式去追寻你。";
        tvWhyContent.setText(content);
    }

    private void loadJourneyContent() {
        addTimelineItem("🎮 早期", "游戏外挂开发，专注于王者荣耀逆向安全攻防，主要利用代码或服务器实现游戏外挂透视或技能自瞄效果。持续了大概两年左右。");
        addTimelineItem("🎓 毕业", "从南昌大学共青学院毕业，通过贵人网友直接抵达深圳该公司上班。可惜技术死宅的性格，把工资谈低了。");
        addTimelineItem("💡 公益", "持续一年时间一边工作一边为外挂社区免费发光发热，别人收费我公益，别人圈钱我砸钱公益，最终反而招致祸患。低估了人性，还是沉浸在校园的温室中。");
        addTimelineItem("🚀 转型", "2026年开春后，彻底放弃了外挂，转向正向职业程序员开发，开发了多款个人版精品作品，如微信小程序亦心装修工作室系统，如这个一念APP软件。");
    }

    private void addTimelineItem(String period, String content) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_timeline, llTimeline, false);
        
        TextView tvPeriod = itemView.findViewById(R.id.tv_period);
        TextView tvContent = itemView.findViewById(R.id.tv_content);
        View viewLine = itemView.findViewById(R.id.view_line);
        
        tvPeriod.setText(period);
        tvContent.setText(content);
        
        llTimeline.addView(itemView);
        
        final View finalView = itemView;
        itemView.setAlpha(0f);
        itemView.setTranslationX(-20f);
        itemView.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void loadToHerContent() {
        tvToHerContent.setText("这里的内容，\n只属于一个人...\n\n点击此处，让心飞一会儿 💕");
        
        tvToHerContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToHerMessage();
            }
        });
    }

    private void showToHerMessage() {
        tvToHerContent.setText("欧阳颖，\n\n初中那年的阳光，\n照进了我的心里。\n\n这些年，\n我一直在用我的方式，\n追寻那份模糊的记忆。\n\n不知道是否晚了，\n不知道此音能否抵达，\n\n但我知道——\n不悔年轻，不弃少年。");
        
        tvToHerContent.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        tvToHerContent.setAlpha(1f);
                    }
                })
                .start();

        heartParticleView.startAnimation(20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (heartParticleView != null) {
            heartParticleView.startAnimation(5);
        }
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (heartParticleView != null) {
            heartParticleView.stopAnimation();
        }
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (heartParticleView != null) {
            heartParticleView.stopAnimation();
        }
        stopBgm();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
