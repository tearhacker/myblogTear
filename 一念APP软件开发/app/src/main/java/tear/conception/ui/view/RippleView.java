package tear.conception.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class RippleView extends View {
    
    private List<Ripple> ripples;
    private Paint ripplePaint;
    private boolean isAnimating = false;
    
    private int rippleColor = 0xFF7CB342;
    private int maxRippleCount = 3;

    public RippleView(Context context) {
        super(context);
        init();
    }

    public RippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ripples = new ArrayList<>();
        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStyle(Paint.Style.STROKE);
        ripplePaint.setStrokeWidth(4f);
    }

    public void startRipple() {
        isAnimating = true;
        
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float maxRadius = Math.max(getWidth(), getHeight()) / 2f;
        
        for (int i = 0; i < maxRippleCount; i++) {
            final Ripple ripple = new Ripple();
            ripple.centerX = centerX;
            ripple.centerY = centerY;
            ripple.radius = 0;
            ripple.maxRadius = maxRadius;
            ripple.alpha = 255;
            ripple.delay = i * 200;
            
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    ripples.add(ripple);
                    animateRipple(ripple);
                }
            }, ripple.delay);
        }
        
        invalidate();
    }

    private void animateRipple(final Ripple ripple) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = (float) animation.getAnimatedValue();
                ripple.radius = ripple.maxRadius * progress;
                ripple.alpha = (int) (255 * (1 - progress));
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ripples.remove(ripple);
                if (ripples.isEmpty()) {
                    isAnimating = false;
                }
            }
        });
        animator.start();
    }

    public void stopRipple() {
        isAnimating = false;
        ripples.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        for (Ripple ripple : ripples) {
            ripplePaint.setColor(rippleColor);
            ripplePaint.setAlpha(ripple.alpha);
            canvas.drawCircle(ripple.centerX, ripple.centerY, ripple.radius, ripplePaint);
        }
    }

    public void setRippleColor(int color) {
        this.rippleColor = color;
    }

    private static class Ripple {
        float centerX;
        float centerY;
        float radius;
        float maxRadius;
        int alpha;
        long delay;
    }
}
