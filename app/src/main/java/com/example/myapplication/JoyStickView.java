package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
public class JoyStickView extends View {
    private Paint circlePaint;
    private Paint handlePaint;
    private PointF center = new PointF();
    private PointF handlePosition = new PointF();
    private float outerRadius;
    private float handleRadius;
    private JoyStickListener listener;
    private long lastUpdateTime;
    private static final long MIN_UPDATE_INTERVAL = 16;

    public interface JoyStickListener {
        void onMove(float angle, float strength); // angle: 0-360°, strength: 0-1
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.GRAY);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.RED);
        handlePaint.setStyle(Paint.Style.FILL);
    }

    public float getCurrentStrength() {
        // Рассчитываем текущую силу (расстояние от центра)
        float distance = (float) Math.sqrt(
                Math.pow(handlePosition.x - center.x, 2) +
                        Math.pow(handlePosition.y - center.y, 2)
        );
        return Math.min(1, distance / outerRadius);
    }

    public void resetPosition() {
        handlePosition.set(center);
        invalidate();
    }
    public float getCurrentAngle() {
        float dx = handlePosition.x - center.x;
        float dy = handlePosition.y - center.y;
        float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        return angle < 0 ? angle + 360 : angle;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        center.x = w / 2f;
        center.y = h / 2f;
        handlePosition.set(center);
        outerRadius = Math.min(w, h) / 3f;
        handleRadius = outerRadius / 3f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, outerRadius, circlePaint);
        canvas.drawCircle(handlePosition.x, handlePosition.y, handleRadius, handlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime > MIN_UPDATE_INTERVAL) {
                    lastUpdateTime = currentTime;

                    // Ограничиваем движение ручки внутри внешнего круга
                    float distance = (float) Math.sqrt(Math.pow(x - center.x, 2) + Math.pow(y - center.y, 2));
                    if (distance > outerRadius) {
                        float ratio = outerRadius / distance;
                        x = center.x + (x - center.x) * ratio;
                        y = center.y + (y - center.y) * ratio;
                    }
                    handlePosition.set(x, y);
                    invalidate();

                    // Рассчет угла и силы
                    float dx = x - center.x;
                    float dy = y - center.y;
                    float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                    if (angle < 0) angle += 360;
                    float strength = Math.min(1, distance / outerRadius);

                    if (listener != null) {
                        listener.onMove(angle, strength);
                    }
                }

                return true;

            case MotionEvent.ACTION_UP:
                handlePosition.set(center);
                invalidate();
                if (listener != null) listener.onMove(0, 0);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setListener(JoyStickListener listener) {
        this.listener = listener;
    }
}
