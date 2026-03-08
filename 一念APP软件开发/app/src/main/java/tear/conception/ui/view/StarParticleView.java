package tear.conception.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StarParticleView extends View {
    
    private List<Star> stars;
    private Paint starPaint;
    private Random random;
    private boolean isAnimating = false;
    
    private int[] starColors = {0xFFFFD700, 0xFFFFA500, 0xFF7CB342, 0xFF87CEEB, 0xFFFF69B4};

    public StarParticleView(Context context) {
        super(context);
        init();
    }

    public StarParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StarParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        stars = new ArrayList<>();
        random = new Random();
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setStyle(Paint.Style.FILL);
    }

    public void startAnimation(int starCount) {
        stars.clear();
        isAnimating = true;
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        for (int i = 0; i < starCount; i++) {
            Star star = new Star();
            star.x = centerX;
            star.y = centerY;
            star.size = random.nextFloat() * 20 + 10;
            star.color = starColors[random.nextInt(starColors.length)];
            star.angle = random.nextFloat() * 360;
            star.speed = random.nextFloat() * 8 + 4;
            star.rotation = random.nextFloat() * 360;
            star.rotationSpeed = random.nextFloat() * 10 - 5;
            star.alpha = 1.0f;
            star.distance = 0;
            star.maxDistance = random.nextFloat() * 300 + 200;
            stars.add(star);
        }
        
        invalidate();
    }

    public void stopAnimation() {
        isAnimating = false;
        stars.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!isAnimating || stars.isEmpty()) {
            return;
        }
        
        boolean allDone = true;
        
        for (Star star : stars) {
            if (star.alpha > 0) {
                allDone = false;
                
                star.distance += star.speed;
                star.rotation += star.rotationSpeed;
                
                double radians = Math.toRadians(star.angle);
                float x = (float) (star.x + Math.cos(radians) * star.distance);
                float y = (float) (star.y + Math.sin(radians) * star.distance);
                
                float progress = star.distance / star.maxDistance;
                star.alpha = 1.0f - progress;
                
                starPaint.setColor(star.color);
                starPaint.setAlpha((int) (star.alpha * 255));
                
                canvas.save();
                canvas.translate(x, y);
                canvas.rotate(star.rotation);
                drawStar(canvas, 0, 0, star.size, star.size / 2);
                canvas.restore();
            }
        }
        
        if (!allDone) {
            invalidate();
        } else {
            isAnimating = false;
            stars.clear();
        }
    }

    private void drawStar(Canvas canvas, float cx, float cy, float outerRadius, float innerRadius) {
        Path path = new Path();
        int points = 5;
        
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
        
        canvas.drawPath(path, starPaint);
    }

    private static class Star {
        float x;
        float y;
        float size;
        int color;
        float angle;
        float speed;
        float rotation;
        float rotationSpeed;
        float alpha;
        float distance;
        float maxDistance;
    }
}
