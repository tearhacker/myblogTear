package tear.conception.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarTrailView extends View {
    
    private List<TrailStar> stars;
    private List<BackgroundStar> bgStars;
    private Paint starPaint;
    private Paint glowPaint;
    private Paint trailPaint;
    private Paint bgStarPaint;
    private Random random;
    private boolean isAnimating = false;
    private boolean introAnimating = true;
    private float introProgress = 0f;
    private int activatedCount = 0;
    private OnStarClickListener starClickListener;
    
    private int starColor = 0xFFE6F1FF;
    private int glowColor = 0xFFFF6B9D;
    private int trailColor = 0xFFB388EB;
    private int activeColor = 0xFFFF6B9D;
    
    private ValueAnimator introAnimator;
    private ValueAnimator pulseAnimator;
    private float pulseValue = 0f;

    public interface OnStarClickListener {
        void onStarClick(int starIndex, int totalStars);
    }

    public StarTrailView(Context context) {
        super(context);
        init();
    }

    public StarTrailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarTrailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        stars = new ArrayList<>();
        bgStars = new ArrayList<>();
        random = new Random();
        
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setStyle(Paint.Style.FILL);
        
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setStyle(Paint.Style.FILL);
        
        trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trailPaint.setStyle(Paint.Style.STROKE);
        trailPaint.setStrokeWidth(2f);
        trailPaint.setStrokeCap(Paint.Cap.ROUND);
        
        bgStarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgStarPaint.setStyle(Paint.Style.FILL);
        
        startPulseAnimation();
    }

    public void setOnStarClickListener(OnStarClickListener listener) {
        this.starClickListener = listener;
    }

    public void startIntroAnimation(int starCount) {
        stars.clear();
        activatedCount = 0;
        introAnimating = true;
        introProgress = 0f;
        
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) return;
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        float startX = width * 0.15f;
        float startY = height * 0.85f;
        float endX = width * 0.85f;
        float endY = height * 0.15f;
        
        for (int i = 0; i < starCount; i++) {
            TrailStar star = new TrailStar();
            float progress = (float) i / (starCount - 1);
            
            float controlX1 = width * 0.3f;
            float controlY1 = height * 0.3f;
            float controlX2 = width * 0.7f;
            float controlY2 = height * 0.7f;
            
            star.targetX = bezierPoint(startX, controlX1, controlX2, endX, progress);
            star.targetY = bezierPoint(startY, controlY1, controlY2, endY, progress);
            star.x = startX;
            star.y = startY;
            star.size = 12 + random.nextFloat() * 8;
            star.index = i;
            star.activated = false;
            star.activationProgress = 0f;
            stars.add(star);
        }
        
        generateBackgroundStars();
        
        introAnimator = ValueAnimator.ofFloat(0f, 1f);
        introAnimator.setDuration(2000);
        introAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        introAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                introProgress = (float) animation.getAnimatedValue();
                updateStarPositions();
                invalidate();
            }
        });
        introAnimator.start();
        isAnimating = true;
    }

    private float bezierPoint(float p0, float p1, float p2, float p3, float t) {
        float oneMinusT = 1 - t;
        return oneMinusT * oneMinusT * oneMinusT * p0 +
               3 * oneMinusT * oneMinusT * t * p1 +
               3 * oneMinusT * t * t * p2 +
               t * t * t * p3;
    }

    private void generateBackgroundStars() {
        bgStars.clear();
        int width = getWidth();
        int height = getHeight();
        
        for (int i = 0; i < 50; i++) {
            BackgroundStar star = new BackgroundStar();
            star.x = random.nextFloat() * width;
            star.y = random.nextFloat() * height;
            star.size = random.nextFloat() * 3 + 1;
            star.alpha = random.nextFloat() * 0.5f + 0.3f;
            star.twinkleSpeed = random.nextFloat() * 2 + 1;
            star.twinkleOffset = random.nextFloat() * (float) Math.PI * 2;
            bgStars.add(star);
        }
    }

    private void updateStarPositions() {
        for (TrailStar star : stars) {
            float delay = star.index * 0.1f;
            float adjustedProgress = Math.max(0, Math.min(1, (introProgress - delay) / (1 - delay)));
            star.x = lerp(stars.get(0).targetX, star.targetX, adjustedProgress);
            star.y = lerp(stars.get(0).targetY, star.targetY, adjustedProgress);
        }
    }

    private float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    private void startPulseAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(0f, (float) Math.PI * 2);
        pulseAnimator.setDuration(3000);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pulseValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        pulseAnimator.start();
    }

    public void activateStar(int index) {
        if (index >= 0 && index < stars.size()) {
            TrailStar star = stars.get(index);
            if (!star.activated) {
                star.activated = true;
                activatedCount++;
                animateStarActivation(star);
            }
        }
    }

    private void animateStarActivation(final TrailStar star) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                star.activationProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    public void gatherToCenter() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        for (final TrailStar star : stars) {
            ValueAnimator xAnim = ValueAnimator.ofFloat(star.x, centerX);
            ValueAnimator yAnim = ValueAnimator.ofFloat(star.y, centerY);
            
            xAnim.setDuration(1500);
            yAnim.setDuration(1500);
            xAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            yAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            
            xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    star.x = (float) animation.getAnimatedValue();
                }
            });
            yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    star.y = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            
            xAnim.start();
            yAnim.start();
        }
    }

    public void scatterFromCenter() {
        for (final TrailStar star : stars) {
            float angle = random.nextFloat() * (float) Math.PI * 2;
            float distance = random.nextFloat() * 500 + 300;
            float targetX = getWidth() / 2 + (float) Math.cos(angle) * distance;
            float targetY = getHeight() / 2 + (float) Math.sin(angle) * distance;
            
            ValueAnimator xAnim = ValueAnimator.ofFloat(star.x, targetX);
            ValueAnimator yAnim = ValueAnimator.ofFloat(star.y, targetY);
            
            xAnim.setDuration(1000);
            yAnim.setDuration(1000);
            
            xAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    star.x = (float) animation.getAnimatedValue();
                }
            });
            yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    star.y = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            
            xAnim.start();
            yAnim.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        drawBackgroundStars(canvas);
        
        if (!stars.isEmpty() && stars.size() > 1) {
            drawTrail(canvas);
        }
        
        for (TrailStar star : stars) {
            drawStar(canvas, star);
        }
    }

    private void drawBackgroundStars(Canvas canvas) {
        for (BackgroundStar star : bgStars) {
            float twinkle = (float) Math.sin(pulseValue * star.twinkleSpeed + star.twinkleOffset);
            float alpha = star.alpha * (0.7f + twinkle * 0.3f);
            
            bgStarPaint.setColor(starColor);
            bgStarPaint.setAlpha((int) (alpha * 255));
            canvas.drawCircle(star.x, star.y, star.size, bgStarPaint);
        }
    }

    private void drawTrail(Canvas canvas) {
        trailPaint.setColor(trailColor);
        trailPaint.setAlpha(100);
        
        Path path = new Path();
        path.moveTo(stars.get(0).x, stars.get(0).y);
        
        for (int i = 1; i < stars.size(); i++) {
            TrailStar star = stars.get(i);
            path.lineTo(star.x, star.y);
        }
        
        canvas.drawPath(path, trailPaint);
    }

    private void drawStar(Canvas canvas, TrailStar star) {
        float size = star.size;
        float glowSize = size * 2;
        
        if (star.activated) {
            float pulse = (float) Math.sin(pulseValue * 2 + star.index) * 0.2f + 1f;
            size *= (1 + star.activationProgress * 0.5f) * pulse;
            glowSize = size * 3;
            
            glowPaint.setShader(new RadialGradient(
                star.x, star.y, glowSize,
                new int[]{activeColor, 0x00FF6B9D},
                null, Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(star.x, star.y, glowSize, glowPaint);
            
            starPaint.setColor(activeColor);
        } else {
            float pulse = (float) Math.sin(pulseValue + star.index * 0.5f) * 0.15f + 1f;
            size *= pulse;
            
            glowPaint.setShader(new RadialGradient(
                star.x, star.y, glowSize,
                new int[]{0x40E6F1FF, 0x00E6F1FF},
                null, Shader.TileMode.CLAMP
            ));
            canvas.drawCircle(star.x, star.y, glowSize, glowPaint);
            
            starPaint.setColor(starColor);
        }
        
        drawStarShape(canvas, star.x, star.y, size, starPaint);
    }

    private void drawStarShape(Canvas canvas, float cx, float cy, float size, Paint paint) {
        Path path = new Path();
        int points = 5;
        float outerRadius = size;
        float innerRadius = size / 2;
        
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.toRadians(i * 36 - 90);
            float radius = i % 2 == 0 ? outerRadius : innerRadius;
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            
            for (int i = 0; i < stars.size(); i++) {
                TrailStar star = stars.get(i);
                float distance = (float) Math.sqrt(
                    Math.pow(x - star.x, 2) + Math.pow(y - star.y, 2)
                );
                
                if (distance < star.size * 3) {
                    if (starClickListener != null && !star.activated) {
                        starClickListener.onStarClick(i, stars.size());
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public int getActivatedCount() {
        return activatedCount;
    }

    public int getTotalStars() {
        return stars.size();
    }

    public boolean isIntroComplete() {
        return introProgress >= 1f;
    }

    public void stopAnimation() {
        isAnimating = false;
        if (introAnimator != null) {
            introAnimator.cancel();
        }
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
        }
    }

    private static class TrailStar {
        float x;
        float y;
        float targetX;
        float targetY;
        float size;
        int index;
        boolean activated;
        float activationProgress;
    }

    private static class BackgroundStar {
        float x;
        float y;
        float size;
        float alpha;
        float twinkleSpeed;
        float twinkleOffset;
    }
}
