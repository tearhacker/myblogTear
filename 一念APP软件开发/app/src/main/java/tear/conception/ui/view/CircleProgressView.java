package tear.conception.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressView extends View {
    
    private Paint bgPaint;
    private Paint progressPaint;
    private RectF rectF;
    
    private int maxDays = 30;
    private int currentDays = 0;
    private float progress = 0;
    
    private int bgColor = 0xFFE8F5E9;
    private int progressColor = 0xFF7CB342;
    private float strokeWidth = 12f;
    
    private ValueAnimator progressAnimator;

    public CircleProgressView(Context context) {
        super(context);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(strokeWidth);
        bgPaint.setColor(bgColor);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        progressPaint.setColor(progressColor);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = strokeWidth / 2;
        rectF.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawArc(rectF, 0, 360, false, bgPaint);
        
        float sweepAngle = 360 * progress;
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);
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

    public void animateProgress(float targetProgress) {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        
        progressAnimator = ValueAnimator.ofFloat(progress, targetProgress);
        progressAnimator.setDuration(1000);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        progressAnimator.start();
    }

    public void setProgressColor(int color) {
        this.progressColor = color;
        progressPaint.setColor(color);
        invalidate();
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
}
