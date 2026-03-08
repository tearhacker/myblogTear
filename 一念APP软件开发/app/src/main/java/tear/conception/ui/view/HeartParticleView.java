package tear.conception.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeartParticleView extends View {
    
    private List<Heart> hearts;
    private Paint heartPaint;
    private Random random;
    private boolean isAnimating = false;
    
    private int[] heartColors = {0xFFFF4081, 0xFFFF6B9D, 0xFFE91E63, 0xFFF8BBD9, 0xFFFF69B4};

    public HeartParticleView(Context context) {
        super(context);
        init();
    }

    public HeartParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        hearts = new ArrayList<>();
        random = new Random();
        heartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        heartPaint.setStyle(Paint.Style.FILL);
    }

    public void startAnimation(int heartCount) {
        hearts.clear();
        isAnimating = true;
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        for (int i = 0; i < heartCount; i++) {
            Heart heart = new Heart();
            heart.x = centerX;
            heart.y = centerY;
            heart.size = random.nextFloat() * 25 + 15;
            heart.color = heartColors[random.nextInt(heartColors.length)];
            heart.angle = random.nextFloat() * 360;
            heart.speed = random.nextFloat() * 6 + 3;
            heart.rotation = random.nextFloat() * 360;
            heart.rotationSpeed = random.nextFloat() * 8 - 4;
            heart.alpha = 1.0f;
            heart.distance = 0;
            heart.maxDistance = random.nextFloat() * 350 + 150;
            hearts.add(heart);
        }
        
        invalidate();
    }

    public void stopAnimation() {
        isAnimating = false;
        hearts.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!isAnimating || hearts.isEmpty()) {
            return;
        }
        
        boolean allDone = true;
        
        for (Heart heart : hearts) {
            if (heart.alpha > 0) {
                allDone = false;
                
                heart.distance += heart.speed;
                heart.rotation += heart.rotationSpeed;
                
                double radians = Math.toRadians(heart.angle);
                float x = (float) (heart.x + Math.cos(radians) * heart.distance);
                float y = (float) (heart.y + Math.sin(radians) * heart.distance);
                
                float progress = heart.distance / heart.maxDistance;
                heart.alpha = 1.0f - progress;
                
                heartPaint.setColor(heart.color);
                heartPaint.setAlpha((int) (heart.alpha * 255));
                
                canvas.save();
                canvas.translate(x, y);
                canvas.rotate(heart.rotation);
                drawHeart(canvas, 0, 0, heart.size);
                canvas.restore();
            }
        }
        
        if (!allDone) {
            invalidate();
        } else {
            isAnimating = false;
            hearts.clear();
        }
    }

    private void drawHeart(Canvas canvas, float cx, float cy, float size) {
        Path path = new Path();
        
        float width = size;
        float height = size;
        
        path.moveTo(cx, cy + height / 4);
        
        path.cubicTo(cx, cy, 
                     cx - width / 2, cy, 
                     cx - width / 2, cy + height / 4);
        
        path.cubicTo(cx - width / 2, cy + height / 2, 
                     cx, cy + height * 3 / 4, 
                     cx, cy + height);
        
        path.cubicTo(cx, cy + height * 3 / 4, 
                     cx + width / 2, cy + height / 2, 
                     cx + width / 2, cy + height / 4);
        
        path.cubicTo(cx + width / 2, cy, 
                     cx, cy, 
                     cx, cy + height / 4);
        
        path.close();
        
        canvas.drawPath(path, heartPaint);
    }

    private static class Heart {
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
