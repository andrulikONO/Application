package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerView extends View{
    // Фиксированные размеры изображений для игрока и верхнего объекта
    private static final int PLAYER_WIDTH = 100;
    private static final int PLAYER_HEIGHT = 100;
    private static final int TOP_OBJECT_WIDTH = 250;
    private static final int TOP_OBJECT_HEIGHT = 250;

    // Изображения
    private Bitmap playerBitmap;
    private Bitmap topObjectBitmap;
    private Bitmap originalTargetBitmap; // Оригинальное изображение цели

    // Радиусы для столкновений
    private float playerRadius;
    private float topObjectRadius;

    private Paint zonePaint;
    // Главный игрок
    private float playerX, playerY;

    // Верхний объект
    private float topObjectX, topObjectY;

    // Соединительная линия
    private Paint linePaint;
    private Path linePath;
    // Цели
    private List<Target> targets = new ArrayList<>();
    private Random random = new Random();

    // Размеры View
    private int viewWidth, viewHeight;
    private int safeZoneTop = 400; // Фиксированная верхняя граница безопасной зоны
    private int forbiddenZoneBottom; // Нижняя граница безопасной зоны

    // Счет
    private int score = 0;
    private OnScoreChangedListener scoreListener;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Загрузка и масштабирование изображений для игрока и верхнего объекта
        playerBitmap = scaleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hook),
                PLAYER_WIDTH, PLAYER_HEIGHT);
        topObjectBitmap = scaleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ladia),
                TOP_OBJECT_WIDTH, TOP_OBJECT_HEIGHT);
        // Загружаем оригинальное изображение цели
        originalTargetBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fish);

        if (originalTargetBitmap == null) {
            originalTargetBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        }

        // Рассчет радиусов для столкновений
        playerRadius = Math.min(PLAYER_WIDTH, PLAYER_HEIGHT) / 2f;
        topObjectRadius = Math.min(TOP_OBJECT_WIDTH, TOP_OBJECT_HEIGHT) / 2f;

        // Настройка соединительной линии
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.argb(200, 0, 0, 0));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5f);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        linePath = new Path();

        // Нарисовка зоны
        zonePaint = new Paint();
        zonePaint.setColor(Color.argb(30, 0, 0, 255));
        zonePaint.setStyle(Paint.Style.FILL);
    }

    // Метод для масштабирования изображения
    private Bitmap scaleBitmap(Bitmap original, int newWidth, int newHeight) {
        if (original == null) {
            return Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        }

        float scaleWidth = ((float) newWidth) / original.getWidth();
        float scaleHeight = ((float) newHeight) / original.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(original, 0, 0,
                original.getWidth(), original.getHeight(),
                matrix, true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;

        // Стартовая позиция игрока - центр
        playerX = w / 2f;
        playerY = h / 2f;

        // Позиция верхнего объекта
        topObjectY = safeZoneTop - TOP_OBJECT_HEIGHT / 2f;
        topObjectX = playerX;

        forbiddenZoneBottom = safeZoneTop;

        // Создаем цели
        createTargets(10);
    }

    public void resetGame() {
        // Сброс позиции игрока
        playerX = viewWidth / 2f;
        playerY = viewHeight / 2f;

        // Сброс позиции верхнего объекта
        topObjectX = playerX;
        topObjectY = safeZoneTop - TOP_OBJECT_HEIGHT / 2f;

        // Сброс счета
        score = 0;

        // Пересоздание целей
        createTargets(10);
        updateLinePath();

        // Сброс состояния
        if (scoreListener != null) {
            scoreListener.onScoreChanged(score);
        }

        invalidate();
    }

    // Создание случайных целей со случайным размером
    void createTargets(int count) {
        targets.clear();

        for (int i = 0; i < count; i++) {
            // Генерируем случайный размер от 60 до 150
            int targetSize = 60 + random.nextInt(91); // 60 + [0-90] = [60-150]
            float targetRadius = targetSize / 2f;

            // Масштабируем изображение цели
            Bitmap scaledTarget = scaleBitmap(originalTargetBitmap, targetSize, targetSize);

            float x, y;
            int attempts = 0;
            boolean validPosition;

            do {
                validPosition = true;
                // Учитываем размер цели при позиционировании
                x = targetSize / 2f + random.nextFloat() * (viewWidth - targetSize);
                y = safeZoneTop + targetSize / 2f +
                        random.nextFloat() * (viewHeight - safeZoneTop - targetSize);

                // Проверяем, не попадает ли цель в зону верхнего объекта
                if (y < safeZoneTop) {
                    validPosition = false;
                }

                // Проверяем пересечение с другими целями
                for (Target existing : targets) {
                    float distance = (float) Math.sqrt(Math.pow(x - existing.x, 2) + Math.pow(y - existing.y, 2));
                    // Учитываем радиусы обеих целей
                    if (distance < targetRadius + existing.radius + 20) {
                        validPosition = false;
                        break;
                    }
                }

                attempts++;
            } while (!validPosition && attempts < 100);

            if (validPosition) {
                targets.add(new Target(x, y, targetRadius, scaledTarget, targetSize));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Рисуем соединительную линию
        updateLinePath();
        canvas.drawPath(linePath, linePaint);

        // Рисуем верхний объект (изображение)
        canvas.drawBitmap(topObjectBitmap,
                topObjectX - TOP_OBJECT_WIDTH / 2f,
                topObjectY - TOP_OBJECT_HEIGHT / 2f,
                null);

        // Рисуем активные цели
        for (Target target : targets) {
            if (target.isActive) {
                // Рисуем с учетом размера цели
                canvas.drawBitmap(target.bitmap,
                        target.x - target.size / 2f,
                        target.y - target.size / 2f,
                        null);
            }
        }

        // Рисуем игрока (изображение)
        canvas.drawBitmap(playerBitmap,
                playerX - PLAYER_WIDTH / 2f,
                playerY - PLAYER_HEIGHT / 2f,
                null);
    }

    // Обновление пути соединительной линии
    private void updateLinePath() {
        linePath.reset();

        float dx = topObjectX - playerX;
        float dy = topObjectY - playerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;

            float startX = topObjectX - dx * topObjectRadius;
            float startY = topObjectY - dy * topObjectRadius;

            float endX = playerX + dx * playerRadius;
            float endY = playerY + dy * playerRadius;

            linePath.moveTo(startX, startY);
            linePath.lineTo(endX, endY);
        }
    }

    public void move(float dx, float dy) {
        float newX = playerX + dx;
        float newY = playerY + dy;

        newX = Math.max(PLAYER_WIDTH / 2f, Math.min(viewWidth - PLAYER_WIDTH / 2f, newX));
        newY = Math.max(forbiddenZoneBottom + PLAYER_HEIGHT / 2f,
                Math.min(viewHeight - PLAYER_HEIGHT / 2f, newY));

        playerX = newX;
        playerY = newY;

        topObjectX = playerX;

        updateLinePath();

        checkCollisions();

        invalidate();
    }

    // Проверка столкновений с целями
    private void checkCollisions() {
        for (Target target : targets) {
            if (target.isActive) {
                float distance = (float) Math.sqrt(
                        Math.pow(playerX - target.x, 2) +
                                Math.pow(playerY - target.y, 2)
                );

                // Если расстояние меньше суммы радиусов - столкновение
                if (distance < playerRadius + target.radius) {
                    target.isActive = false;
                    score++;

                    // Обновляем счет
                    if (scoreListener != null) {
                        scoreListener.onScoreChanged(score);
                    }
                }
            }
        }
    }

    // Интерфейс для обновления счета
    public interface OnScoreChangedListener {
        void onScoreChanged(int newScore);
    }

    public void setOnScoreChangedListener(OnScoreChangedListener listener) {
        this.scoreListener = listener;
    }

    // Класс цели с добавлением размера и Bitmap
    private static class Target {
        float x, y, radius;
        boolean isActive = true;
        Bitmap bitmap;
        int size;

        Target(float x, float y, float radius, Bitmap bitmap, int size) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.bitmap = bitmap;
            this.size = size;
        }
    }
}
