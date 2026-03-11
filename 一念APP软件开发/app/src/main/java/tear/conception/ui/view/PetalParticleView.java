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

public class PetalParticleView extends View {
    
    private List<Petal> petals;
    private Paint petalPaint;
    private Random random;
    private boolean isAnimating = false;
    private boolean isContinuousMode = false;
    
    private int[] petalColors = {
        0xFFFFB6C1, 0xFFFF69B4, 0xFFFF1493, 
        0xFFFFC0CB, 0xFFF8BBD9, 0xFFFFE4E1,
        0xFFFFDDE1, 0xFFFFB7C5, 0xFFDE8EA0
    };

    public PetalParticleView(Context context) {
        super(context);
        init();
    }

    public PetalParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PetalParticleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        petals = new ArrayList<>();
        random = new Random();
        petalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        petalPaint.setStyle(Paint.Style.FILL);
    }

    public void startAnimation(int petalCount) {
        isContinuousMode = false;
        petals.clear();
        isAnimating = true;
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        for (int i = 0; i < petalCount; i++) {
            Petal petal = createPetal(centerX, centerY, true);
            petals.add(petal);
        }
        
        invalidate();
    }
    
    public void startContinuousMode() {
        isContinuousMode = true;
        isAnimating = true;
        petals.clear();
        
        for (int i = 0; i < 15; i++) {
            Petal petal = createFloatingPetal();
            petals.add(petal);
        }
        
        invalidate();
    }
    
    public void addPetalOnTouch(float x, float y) {
        if (!isAnimating) {
            isAnimating = true;
        }
        
        for (int i = 0; i < 3; i++) {
            Petal petal = createPetal((int)x, (int)y, false);
            petal.vx = (random.nextFloat() - 0.5f) * 6;
            petal.vy = random.nextFloat() * 2 + 1;
            petals.add(petal);
        }
        
        if (petals.size() > 100) {
            petals.subList(0, petals.size() - 80).clear();
        }
        
        invalidate();
    }
    
    private Petal createPetal(int x, int y, boolean burst) {
        Petal petal = new Petal();
        petal.x = x;
        petal.y = y;
        petal.size = random.nextFloat() * 20 + 12;
        petal.color = petalColors[random.nextInt(petalColors.length)];
        
        if (burst) {
            petal.angle = random.nextFloat() * 360;
            petal.speed = random.nextFloat() * 8 + 4;
            petal.maxDistance = random.nextFloat() * 400 + 200;
        } else {
            petal.angle = random.nextFloat() * 360;
            petal.speed = 0;
            petal.maxDistance = 0;
        }
        
        petal.rotation = random.nextFloat() * 360;
        petal.rotationSpeed = random.nextFloat() * 6 - 3;
        petal.alpha = 1.0f;
        petal.distance = 0;
        petal.vx = 0;
        petal.vy = 0;
        petal.swayOffset = random.nextFloat() * (float) Math.PI * 2;
        petal.swaySpeed = random.nextFloat() * 0.05f + 0.02f;
        petal.fallSpeed = random.nextFloat() * 1.5f + 1f;
        
        return petal;
    }
    
    private Petal createFloatingPetal() {
        Petal petal = new Petal();
        petal.x = random.nextFloat() * getWidth();
        petal.y = random.nextFloat() * getHeight() - getHeight();
        petal.size = random.nextFloat() * 18 + 10;
        petal.color = petalColors[random.nextInt(petalColors.length)];
        petal.rotation = random.nextFloat() * 360;
        petal.rotationSpeed = random.nextFloat() * 4 - 2;
        petal.alpha = random.nextFloat() * 0.5f + 0.5f;
        petal.vx = (random.nextFloat() - 0.5f) * 2;
        petal.vy = random.nextFloat() * 2 + 1;
        petal.swayOffset = random.nextFloat() * (float) Math.PI * 2;
        petal.swaySpeed = random.nextFloat() * 0.03f + 0.01f;
        petal.fallSpeed = petal.vy;
        petal.speed = 0;
        petal.distance = 0;
        petal.maxDistance = 0;
        petal.angle = 0;
        
        return petal;
    }

    public void stopAnimation() {
        isAnimating = false;
        isContinuousMode = false;
        petals.clear();
        invalidate();
    }
    
    public boolean isAnimating() {
        return isAnimating;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (!isAnimating || petals.isEmpty()) {
            return;
        }
        
        boolean hasActivePetal = false;
        
        for (int i = petals.size() - 1; i >= 0; i--) {
            Petal petal = petals.get(i);
            
            if (petal.alpha <= 0) {
                if (isContinuousMode) {
                    petals.set(i, createFloatingPetal());
                } else {
                    continue;
                }
            }
            
            hasActivePetal = true;
            
            if (petal.speed > 0) {
                petal.distance += petal.speed;
                double radians = Math.toRadians(petal.angle);
                petal.x = (float) (petal.x + Math.cos(radians) * petal.speed);
                petal.y = (float) (petal.y + Math.sin(radians) * petal.speed);
                
                float progress = petal.distance / petal.maxDistance;
                petal.alpha = 1.0f - progress;
            } else {
                petal.swayOffset += petal.swaySpeed;
                petal.x += petal.vx + (float) Math.sin(petal.swayOffset) * 1.5f;
                petal.y += petal.fallSpeed;
                
                if (petal.y > getHeight() + 50) {
                    if (isContinuousMode) {
                        petal.y = -50;
                        petal.x = random.nextFloat() * getWidth();
                        petal.alpha = random.nextFloat() * 0.5f + 0.5f;
                    } else {
                        petal.alpha = 0;
                    }
                }
                
                if (petal.x < -50) petal.x = getWidth() + 50;
                if (petal.x > getWidth() + 50) petal.x = -50;
            }
            
            petal.rotation += petal.rotationSpeed;
            
            petalPaint.setColor(petal.color);
            petalPaint.setAlpha((int) (petal.alpha * 255));
            
            canvas.save();
            canvas.translate(petal.x, petal.y);
            canvas.rotate(petal.rotation);
            drawPetal(canvas, 0, 0, petal.size);
            canvas.restore();
        }
        
        if (hasActivePetal || isContinuousMode) {
            invalidate();
        } else {
            isAnimating = false;
            petals.clear();
        }
    }

    private void drawPetal(Canvas canvas, float cx, float cy, float size) {
        Path path = new Path();
        
        float width = size;
        float height = size * 1.3f;
        
        path.moveTo(cx, cy - height / 2);
        
        path.cubicTo(
            cx + width / 2, cy - height / 3,
            cx + width / 2, cy + height / 3,
            cx, cy + height / 2
        );
        
        path.cubicTo(
            cx - width / 2, cy + height / 3,
            cx - width / 2, cy - height / 3,
            cx, cy - height / 2
        );
        
        path.close();
        
        canvas.drawPath(path, petalPaint);
    }

    private static class Petal {
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
        float vx;
        float vy;
        float swayOffset;
        float swaySpeed;
        float fallSpeed;
    }
}
