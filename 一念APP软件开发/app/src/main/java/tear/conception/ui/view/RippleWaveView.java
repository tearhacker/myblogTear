package tear.conception.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RippleWaveView extends View {
    
    private List<Ripple> ripples;
    private Paint ripplePaint;
    private Random random;
    private boolean isAnimating = false;
    
    private int[] rippleColors = {
        0x33FFB6C1, 0x33FF69B4, 0x33FFC0CB,
        0x33FFDDE1, 0x33FFE4E1
    };

    public RippleWaveView(Context context) {
        super(context);
        init();
    }

    public RippleWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RippleWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        ripples = new ArrayList<>();
        random = new Random();
        ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ripplePaint.setStyle(Paint.Style.FILL);
    }

    public void startRippleAnimation() {
        isAnimating = true;
        ripples.clear();
        
        for (int i = 0; i < 5; i++) {
            createRipple();
        }
        
        invalidate();
    }
    
    private void createRipple() {
        Ripple ripple = new Ripple();
        ripple.centerX = getWidth() / 2f;
        ripple.centerY = getHeight() / 2f;
        ripple.currentRadius = 0;
        ripple.maxRadius = Math.max(getWidth(), getHeight()) * 0.8f;
        ripple.speed = random.nextFloat() * 3 + 2;
        ripple.alpha = 255;
        ripple.color = rippleColors[random.nextInt(rippleColors.length)];
        ripples.add(ripple);
    }

    public void stopAnimation() {
        isAnimating = false;
        ripples.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!isAnimating && ripples.isEmpty()) {
            return;
        }
        
        for (int i = ripples.size() - 1; i >= 0; i--) {
            Ripple ripple = ripples.get(i);
            
            ripple.currentRadius += ripple.speed;
            
            float progress = ripple.currentRadius / ripple.maxRadius;
            ripple.alpha = (int) (255 * (1 - progress) * 0.3f);
            
            if (ripple.alpha > 0) {
                ripplePaint.setColor(ripple.color);
                ripplePaint.setAlpha(ripple.alpha);
                
                canvas.drawCircle(ripple.centerX, ripple.centerY, ripple.currentRadius, ripplePaint);
            }
            
            if (ripple.currentRadius >= ripple.maxRadius) {
                ripples.remove(i);
                if (isAnimating) {
                    createRipple();
                }
            }
        }
        
        if (isAnimating || !ripples.isEmpty()) {
            invalidate();
        }
    }

    private static class Ripple {
        float centerX;
        float centerY;
        float currentRadius;
        float maxRadius;
        float speed;
        int alpha;
        int color;
    }
}
