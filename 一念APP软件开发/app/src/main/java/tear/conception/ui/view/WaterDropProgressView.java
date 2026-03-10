package tear.conception.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaterDropProgressView extends View {
    
    private Paint bgPaint;
    private Paint waterPaint;
    private Paint wavePaint;
    private Paint dropPaint;
    private Paint textPaint;
    private Paint subTextPaint;
    private Paint glassBtnPaint;
    private Paint glassBtnStrokePaint;
    private Paint bottomTextPaint;
    private Paint glowPaint;
    private Paint percentagePaint;
    private Paint bubblePaint;
    
    private int maxDays = 30;
    private int currentDays = 0;
    private float progress = 0;
    private float waveOffset = 0;
    private boolean isSignedIn = false;
    private boolean isPressed = false;
    private float pressScale = 1f;
    private float btnAlpha = 1f;
    private float glowAlpha = 0f;
    
    private int[] milestoneColors = {
        0xFF4FC3F7,
        0xFF26C6DA,
        0xFF26A69A,
        0xFF66BB6A,
        0xFF9CCC65,
        0xFFFFCA28
    };
    
    private ValueAnimator progressAnimator;
    private ValueAnimator waveAnimator;
    private ValueAnimator pressAnimator;
    private ValueAnimator glowAnimator;
    private ValueAnimator fadeAnimator;
    private Path dropPath;
    private RectF dropRect;
    
    private String[] encouragements = {
        "开始积累",
        "立足当下",
        "坚持不懈",
        "厚积薄发",
        "持之以恒",
        "展望未来"
    };
    
    private List<Bubble> bubbles = new ArrayList<>();
    private Random random = new Random();
    
    private OnSigninClickListener signinClickListener;

    public interface OnSigninClickListener {
        void onSigninClick();
    }

    public WaterDropProgressView(Context context) {
        super(context);
        init();
    }

    public WaterDropProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaterDropProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(0x0A000000);

        waterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        waterPaint.setStyle(Paint.Style.FILL);

        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setColor(0x50FFFFFF);

        dropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dropPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);

        subTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        subTextPaint.setColor(0xEEFFFFFF);
        subTextPaint.setTextAlign(Paint.Align.CENTER);

        glassBtnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glassBtnPaint.setStyle(Paint.Style.FILL);
        glassBtnPaint.setColor(0x30FFFFFF);

        glassBtnStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glassBtnStrokePaint.setStyle(Paint.Style.STROKE);
        glassBtnStrokePaint.setStrokeWidth(2f);
        glassBtnStrokePaint.setColor(0x90FFFFFF);

        bottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bottomTextPaint.setColor(0x66000000);
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);
        bottomTextPaint.setFakeBoldText(true);

        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
        glowPaint.setColor(0x40FFFFFF);

        percentagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentagePaint.setColor(0xCCFFFFFF);
        percentagePaint.setTextAlign(Paint.Align.CENTER);
        percentagePaint.setFakeBoldText(true);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setColor(0x60FFFFFF);

        dropPath = new Path();
        dropRect = new RectF();
        
        setClickable(true);
        startWaveAnimation();
        startGlowAnimation();
    }

    private void startWaveAnimation() {
        waveAnimator = ValueAnimator.ofFloat(0, 1);
        waveAnimator.setDuration(2500);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                waveOffset = (float) animation.getAnimatedValue();
                updateBubbles();
                invalidate();
            }
        });
        waveAnimator.start();
    }

    private void startGlowAnimation() {
        glowAnimator = ValueAnimator.ofFloat(0, 1, 0);
        glowAnimator.setDuration(2000);
        glowAnimator.setRepeatCount(ValueAnimator.INFINITE);
        glowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                glowAlpha = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        glowAnimator.start();
    }

    private void updateBubbles() {
        if (bubbles.size() < 8 && progress > 0.1f && random.nextFloat() < 0.05f) {
            float size = getWidth() * (0.01f + random.nextFloat() * 0.02f);
            float x = getWidth() * 0.3f + random.nextFloat() * getWidth() * 0.4f;
            float y = getHeight() * 0.7f;
            bubbles.add(new Bubble(x, y, size));
        }
        
        for (int i = bubbles.size() - 1; i >= 0; i--) {
            Bubble bubble = bubbles.get(i);
            bubble.y -= bubble.size * 0.5f;
            bubble.alpha -= 0.01f;
            if (bubble.alpha <= 0) {
                bubbles.remove(i);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float size = Math.min(w, h);
        dropRect.set(0, 0, size, size);
        createDropPath(size);
        
        textPaint.setTextSize(size * 0.16f);
        subTextPaint.setTextSize(size * 0.06f);
        bottomTextPaint.setTextSize(size * 0.055f);
        percentagePaint.setTextSize(size * 0.08f);
    }

    private void createDropPath(float size) {
        dropPath.reset();
        float cx = size / 2;
        float cy = size / 2;
        float radius = size * 0.42f;
        
        dropPath.moveTo(cx, cy - radius * 1.3f);
        
        float controlY = cy - radius * 0.3f;
        dropPath.cubicTo(
            cx - radius * 1.2f, controlY,
            cx - radius * 1.2f, cy + radius * 0.8f,
            cx, cy + radius
        );
        dropPath.cubicTo(
            cx + radius * 1.2f, cy + radius * 0.8f,
            cx + radius * 1.2f, controlY,
            cx, cy - radius * 1.3f
        );
        dropPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        float size = Math.min(width, height);
        float cx = size / 2;
        float cy = size / 2;
        
        canvas.save();
        canvas.translate((width - size) / 2, 0);
        canvas.scale(pressScale, pressScale, cx, cy);
        
        drawGlow(canvas, size, cx, cy);
        canvas.drawPath(dropPath, bgPaint);
        
        if (progress > 0) {
            drawWater(canvas, size, cx, cy);
        }
        
        drawWaterDropDecorations(canvas, size, cx, cy);
        drawBubbles(canvas);
        
        if (isSignedIn) {
            drawSignedInContent(canvas, size, cx, cy);
        } else {
            drawSigninButton(canvas, size, cx, cy);
        }
        
        canvas.restore();
        
        drawBottomText(canvas, width, size);
    }

    private void drawGlow(Canvas canvas, float size, float cx, float cy) {
        if (glowAlpha <= 0) return;
        
        glowPaint.setAlpha((int) (glowAlpha * 50));
        float glowRadius = size * 0.45f + glowAlpha * size * 0.05f;
        canvas.drawCircle(cx, cy, glowRadius, glowPaint);
    }

    private void drawWater(Canvas canvas, float size, float cx, float cy) {
        float waterLevel = cy + size * 0.35f - (progress * size * 0.7f);
        
        canvas.save();
        canvas.clipPath(dropPath);
        
        int colorIndex = Math.min(currentDays / 10, milestoneColors.length - 1);
        int waterColor = milestoneColors[colorIndex];
        
        LinearGradient gradient = new LinearGradient(
            0, waterLevel,
            0, size,
            waterColor,
            darkenColor(waterColor, 0.25f),
            Shader.TileMode.CLAMP
        );
        waterPaint.setShader(gradient);
        
        Path waterPath = new Path();
        waterPath.moveTo(0, waterLevel);
        
        float waveAmplitude = size * 0.025f * (1 + progress * 0.6f);
        float waveLength = size * 0.3f;
        
        for (float x = 0; x <= size; x += 4) {
            float y = waterLevel + 
                (float) Math.sin((x / waveLength + waveOffset * 2) * Math.PI * 2) * waveAmplitude +
                (float) Math.sin((x / waveLength * 0.5f + waveOffset * 3) * Math.PI * 2) * waveAmplitude * 0.6f +
                (float) Math.sin((x / waveLength * 0.25f + waveOffset * 1.5) * Math.PI * 2) * waveAmplitude * 0.3f;
            waterPath.lineTo(x, y);
        }
        
        waterPath.lineTo(size, size);
        waterPath.lineTo(0, size);
        waterPath.close();
        
        canvas.drawPath(waterPath, waterPaint);
        
        wavePaint.setColor(0x40FFFFFF);
        for (float x = 0; x <= size; x += 8) {
            float y = waterLevel + 
                (float) Math.sin((x / waveLength + waveOffset * 2) * Math.PI * 2) * waveAmplitude +
                (float) Math.sin((x / waveLength * 0.5f + waveOffset * 3) * Math.PI * 2) * waveAmplitude * 0.6f +
                (float) Math.sin((x / waveLength * 0.25f + waveOffset * 1.5) * Math.PI * 2) * waveAmplitude * 0.3f;
            canvas.drawCircle(x, y + size * 0.01f, 2.5f, wavePaint);
        }
        
        canvas.restore();
    }

    private void drawWaterDropDecorations(Canvas canvas, float size, float cx, float cy) {
        float dropRadius = size * 0.018f;
        dropPaint.setColor(0x40FFFFFF);
        
        float angle1 = (float) Math.toRadians(-30 + waveOffset * 12);
        float r1 = size * 0.35f;
        canvas.drawCircle(
            cx + (float) Math.cos(angle1) * r1,
            cy - size * 0.15f + (float) Math.sin(angle1) * r1 * 0.35f,
            dropRadius,
            dropPaint
        );
        
        float angle2 = (float) Math.toRadians(200 + waveOffset * 10);
        float r2 = size * 0.3f;
        canvas.drawCircle(
            cx + (float) Math.cos(angle2) * r2,
            cy + size * 0.1f + (float) Math.sin(angle2) * r2 * 0.35f,
            dropRadius * 0.65f,
            dropPaint
        );
        
        float angle3 = (float) Math.toRadians(120 + waveOffset * 8);
        float r3 = size * 0.38f;
        canvas.drawCircle(
            cx + (float) Math.cos(angle3) * r3,
            cy - size * 0.2f + (float) Math.sin(angle3) * r3 * 0.3f,
            dropRadius * 0.5f,
            dropPaint
        );
    }

    private void drawBubbles(Canvas canvas) {
        for (Bubble bubble : bubbles) {
            bubblePaint.setAlpha((int) (bubble.alpha * 255));
            canvas.drawCircle(bubble.x, bubble.y, bubble.size, bubblePaint);
        }
    }

    private void drawSigninButton(Canvas canvas, float size, float cx, float cy) {
        float btnRadius = size * 0.16f;
        
        int alpha = (int) (btnAlpha * (isPressed ? 160 : 255));
        glassBtnPaint.setAlpha(alpha);
        glassBtnStrokePaint.setAlpha(alpha);
        
        canvas.drawCircle(cx, cy, btnRadius, glassBtnPaint);
        canvas.drawCircle(cx, cy, btnRadius, glassBtnStrokePaint);
        
        textPaint.setTextSize(size * 0.09f);
        textPaint.setAlpha(alpha);
        canvas.drawText("签到", cx, cy - size * 0.02f, textPaint);
        
        subTextPaint.setTextSize(size * 0.045f);
        subTextPaint.setAlpha(alpha);
        String daysText = currentDays + "天";
        canvas.drawText(daysText, cx, cy + btnRadius + subTextPaint.getTextSize() * 1.3f, subTextPaint);
        
        String percentage = Math.round(progress * 100) + "%";
        percentagePaint.setTextSize(size * 0.055f);
        percentagePaint.setAlpha(alpha);
        canvas.drawText(percentage, cx, cy + btnRadius + subTextPaint.getTextSize() * 2.8f, percentagePaint);
    }

    private void drawSignedInContent(Canvas canvas, float size, float cx, float cy) {
        textPaint.setTextSize(size * 0.18f);
        textPaint.setAlpha(255);
        canvas.drawText(String.valueOf(currentDays), cx, cy + textPaint.getTextSize() * 0.25f, textPaint);
        
        subTextPaint.setTextSize(size * 0.065f);
        subTextPaint.setAlpha(255);
        String encouragement = getEncouragement();
        canvas.drawText(encouragement, cx, cy + textPaint.getTextSize() * 0.25f + subTextPaint.getTextSize() * 1.4f, subTextPaint);
        
        String percentage = Math.round(progress * 100) + "%";
        percentagePaint.setTextSize(size * 0.06f);
        percentagePaint.setAlpha(255);
        canvas.drawText(percentage, cx, cy + textPaint.getTextSize() * 0.25f + subTextPaint.getTextSize() * 3.2f, percentagePaint);
    }

    private String getEncouragement() {
        int index = Math.min(currentDays / 5, encouragements.length - 1);
        return encouragements[index];
    }

    private void drawBottomText(Canvas canvas, int width, float size) {
        float textY = size + size * 0.12f;
        
        String leftText = "心若向阳";
        String rightText = "静待花开";
        
        float leftX = width * 0.25f;
        float rightX = width * 0.75f;
        
        canvas.drawText(leftText, leftX, textY, bottomTextPaint);
        canvas.drawText(rightText, rightX, textY, bottomTextPaint);
    }

    private int darkenColor(int color, float factor) {
        int r = (int) (Color.red(color) * (1 - factor));
        int g = (int) (Color.green(color) * (1 - factor));
        int b = (int) (Color.blue(color) * (1 - factor));
        return Color.rgb(r, g, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSignedIn) {
            return super.onTouchEvent(event);
        }
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isPressed = true;
                animatePress(0.9f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isPressed = false;
                animatePress(1f);
                if (event.getAction() == MotionEvent.ACTION_UP && signinClickListener != null) {
                    signinClickListener.onSigninClick();
                }
                break;
        }
        return true;
    }

    private void animatePress(float targetScale) {
        if (pressAnimator != null) {
            pressAnimator.cancel();
        }
        pressAnimator = ObjectAnimator.ofFloat(pressScale, targetScale);
        pressAnimator.setDuration(120);
        pressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pressScale = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        pressAnimator.start();
    }

    public void setProgress(float progress) {
        this.progress = Math.min(1f, Math.max(0f, progress));
        invalidate();
    }

    public void setDays(int days, int max) {
        this.currentDays = days;
        this.maxDays = max;
        animateProgress((float) days / max);
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
        invalidate();
    }

    public void animateProgress(float targetProgress) {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        
        progressAnimator = ValueAnimator.ofFloat(progress, targetProgress);
        progressAnimator.setDuration(1800);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    public void playSigninAnimation() {
        AnimatorSet set = new AnimatorSet();
        
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.12f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.12f, 1f);
        ValueAnimator fadeAnimator = ValueAnimator.ofFloat(1f, 0f);
        
        scaleX.setDuration(500);
        scaleY.setDuration(500);
        fadeAnimator.setDuration(350);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        
        set.playTogether(scaleX, scaleY);
        set.start();
        
        fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                btnAlpha = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        fadeAnimator.start();
    }

    public void setOnSigninClickListener(OnSigninClickListener listener) {
        this.signinClickListener = listener;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public int getCurrentDays() {
        return currentDays;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (waveAnimator != null) {
            waveAnimator.cancel();
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        if (glowAnimator != null) {
            glowAnimator.cancel();
        }
    }

    private static class Bubble {
        float x, y, size, alpha;
        
        Bubble(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.alpha = 1f;
        }
    }
}
