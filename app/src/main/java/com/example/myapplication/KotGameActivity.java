package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class KotGameActivity extends AppCompatActivity {
    // Игровые элементы
    private ImageView player;
    private Button leftButton, rightButton;
    private FrameLayout gameContainer;
    private TextView scoreText;

    // Настройки движения
    private final int PLAYER_SPEED = 20;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private Handler movementHandler = new Handler(Looper.getMainLooper());
    private boolean isMovementActive = false;

    // Падающие объекты
    private final List<FallingObject> fallingObjects = new ArrayList<>();
    private final Handler gameLoopHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final int FALLING_SPEED = 10;
    private final int MIN_FALLING_INTERVAL = 500;
    private final int MAX_FALLING_INTERVAL = 1500;
    private static final int MAX_OBJECTS = 15;

    // Система очков
    private int score = 0;
    private static final int WIN_SCORE = 30;
    private boolean gameActive = true;

    // Идентификаторы объектов
    private static final int GOOD_OBJECT = 1;
    private static final int BAD_OBJECT = 2;
    private int badObjectImageId;

    // Кэш изображений
    private SparseArray<Bitmap> imageCache = new SparseArray<>();

    // Пул для повторного использования View
    private final List<ImageView> viewPool = new ArrayList<>();

    // Фиксированный размер рыбок (50dp)
    private int fixedObjectSizePx;

    // Класс для хранения информации об объекте
    private static class FallingObject {
        ImageView view;
        int type;
        float x;
        float y;
        float radius;

        FallingObject(ImageView view, int type, float x, float y, float radius) {
            this.view = view;
            this.type = type;
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kot_game);

        player = findViewById(R.id.player);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        gameContainer = findViewById(R.id.gameContainer);
        scoreText = findViewById(R.id.scoreText);

        // Запоминаем ID плохого объекта
        badObjectImageId = R.drawable.skeletonfish;

        // Фиксированный размер объектов (50dp)
        fixedObjectSizePx = (int) (50 * getResources().getDisplayMetrics().density);

        // Предварительная загрузка изображений
        preloadImages();

        updateScore();
        setupMovement();
        startGameLoop();
    }

    // Предзагрузка изображений в кэш
    private void preloadImages() {
        int[] imageIds = {
                R.drawable.fish,
                R.drawable.goldfish,
                badObjectImageId
        };

        for (int id : imageIds) {
            getCachedBitmap(id, fixedObjectSizePx);
        }
    }

    private void setupMovement() {
        leftButton.setOnTouchListener((v, event) -> {
            handleTouchEvent(event, true);
            return true;
        });

        rightButton.setOnTouchListener((v, event) -> {
            handleTouchEvent(event, false);
            return true;
        });
    }

    private void handleTouchEvent(MotionEvent event, boolean isLeftButton) {
        if (!gameActive) return;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isLeftButton) isMovingLeft = true;
                else isMovingRight = true;
                startMovement();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isLeftButton) isMovingLeft = false;
                else isMovingRight = false;
                stopMovementIfNeeded();
                break;
        }
    }

    private void startMovement() {
        if (!isMovementActive && gameActive) {
            isMovementActive = true;
            movementHandler.post(moveRunnable);
        }
    }

    private void stopMovementIfNeeded() {
        if (!isMovingLeft && !isMovingRight) {
            isMovementActive = false;
            movementHandler.removeCallbacks(moveRunnable);
        }
    }

    private final Runnable moveRunnable = new Runnable() {
        @Override
        public void run() {
            movePlayer();
            if (isMovementActive && gameActive) {
                movementHandler.postDelayed(this, 16);
            }
        }
    };

    private void movePlayer() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player.getLayoutParams();
        int containerWidth = gameContainer.getWidth();

        if (isMovingLeft && isMovingRight) {
            // Ничего не делаем при одновременном нажатии
        } else if (isMovingLeft) {
            params.leftMargin = Math.max(0 - (containerWidth - player.getWidth()/ 2) /2 , params.leftMargin - PLAYER_SPEED);
        } else if (isMovingRight) {
            int maxRight = (containerWidth - player.getWidth() / 2) / 2;
            params.leftMargin = Math.min(maxRight, params.leftMargin + PLAYER_SPEED);
        }

        player.setLayoutParams(params);
        checkCollisions();
    }

    // Запуск основного игрового цикла
    private void startGameLoop() {
        // Запускаем генерацию объектов
        gameLoopHandler.postDelayed(generateRunnable, 1000);

        // Запускаем игровой цикл
        gameLoopHandler.post(gameLoopRunnable);
    }

    private final Runnable generateRunnable = new Runnable() {
        @Override
        public void run() {
            if (gameActive && fallingObjects.size() < MAX_OBJECTS) {
                generateFallingObject();
                int delay = random.nextInt(MAX_FALLING_INTERVAL - MIN_FALLING_INTERVAL) + MIN_FALLING_INTERVAL;
                gameLoopHandler.postDelayed(this, delay);
            } else {
                gameLoopHandler.postDelayed(this, 100);
            }
        }
    };

    private final Runnable gameLoopRunnable = new Runnable() {
        private long lastUpdateTime = System.currentTimeMillis();

        @Override
        public void run() {
            if (gameActive) {
                long currentTime = System.currentTimeMillis();
                float deltaTime = (currentTime - lastUpdateTime) / 1000f;
                lastUpdateTime = currentTime;

                updateFallingObjects(deltaTime);
                checkCollisions();
                gameLoopHandler.postDelayed(this, 33); // 30 FPS
            }
        }
    };

    private void generateFallingObject() {
        // Выбираем тип объекта: хороший или плохой (20% шанс плохого)
        int objectType = (random.nextInt(5) == 0) ? BAD_OBJECT : GOOD_OBJECT;
        int imageResId;

        if (objectType == BAD_OBJECT) {
            imageResId = badObjectImageId; // Плохой объект
        } else {
            // Случайный хороший объект
            int[] goodImages = {R.drawable.fish, R.drawable.goldfish};
            imageResId = goodImages[random.nextInt(goodImages.length)];
        }

        // Фиксированный размер
        float radius = fixedObjectSizePx * 0.4f; // Радиус для столкновений

        // Создаем объект
        ImageView fallingObjectView = getReusableView();
        fallingObjectView.setImageBitmap(getCachedBitmap(imageResId, fixedObjectSizePx));

        // Начальная позиция (по всей ширине экрана)
        float x = random.nextInt(gameContainer.getWidth() - fixedObjectSizePx);
        float y = -fixedObjectSizePx;

        fallingObjectView.setX(x);
        fallingObjectView.setY(y);
        fallingObjectView.setVisibility(View.VISIBLE);

        // Установка фиксированного размера
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(fixedObjectSizePx, fixedObjectSizePx);
        fallingObjectView.setLayoutParams(params);

        if (fallingObjectView.getParent() == null) {
            gameContainer.addView(fallingObjectView);
        }

        // Сохраняем объект
        FallingObject fallingObject = new FallingObject(fallingObjectView, objectType, x, y, radius);
        fallingObjects.add(fallingObject);
    }

    private ImageView getReusableView() {
        // Ищем неиспользуемое View в пуле
        for (ImageView view : viewPool) {
            if (view.getParent() == null) {
                return view;
            }
        }

        // Создаем новое View если нет свободных
        ImageView newView = new ImageView(this);
        viewPool.add(newView);
        return newView;
    }

    private Bitmap getCachedBitmap(int resId, int size) {
        // Специальный ключ для размера
        int key = resId * 1000 + size;

        Bitmap cached = imageCache.get(key);
        if (cached != null && !cached.isRecycled()) {
            return cached;
        }

        // Оптимизированная загрузка
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);

        // Рассчет коэффициента уменьшения
        int inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId, options);
        imageCache.put(key, bitmap);
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void updateFallingObjects(float deltaTime) {
        Iterator<FallingObject> iterator = fallingObjects.iterator();
        while (iterator.hasNext()) {
            FallingObject obj = iterator.next();

            // Обновляем позицию
            obj.y += FALLING_SPEED * deltaTime * 60;

            // Проверяем выход за пределы экрана
            if (obj.y > gameContainer.getHeight()) {
                obj.view.setVisibility(View.INVISIBLE);
                iterator.remove();
                continue;
            }

            // Обновляем положение на экране
            obj.view.setY(obj.y);
        }
    }

    private void checkCollisions() {
        if (!gameActive) return;

        // Получаем координаты игрока
        float playerX = player.getX() + player.getWidth() / 2f;
        float playerY = player.getY() + player.getHeight() / 2f;
        float playerRadius = player.getWidth() * 0.4f; // Уменьшенная зона столкновения

        Iterator<FallingObject> iterator = fallingObjects.iterator();
        while (iterator.hasNext()) {
            FallingObject obj = iterator.next();

            float objX = obj.x + obj.view.getWidth() / 2f;
            float objY = obj.y + obj.view.getHeight() / 2f;

            // Вычисляем расстояние между центрами
            float dx = playerX - objX;
            float dy = playerY - objY;
            float distanceSquared = dx * dx + dy * dy;

            // Проверяем пересечение (квадрат расстояния для оптимизации)
            float minDistance = playerRadius + obj.radius;
            if (distanceSquared < minDistance * minDistance) {
                if (obj.type == GOOD_OBJECT) {
                    increaseScore();
                } else {
                    gameOver();
                }
                obj.view.setVisibility(View.INVISIBLE);
                iterator.remove();
            }
        }
    }

    private void increaseScore() {
        score++;
        updateScore();

        // Проверка победы
        if (score >= WIN_SCORE) {
            gameActive = false;
            showWinDialog();
        }
    }

    private void updateScore() {
        scoreText.setText("Очки: " + score);
    }

    private void gameOver() {
        gameActive = false;

        // Останавливаем все движения
        isMovementActive = false;
        movementHandler.removeCallbacksAndMessages(null);
        gameLoopHandler.removeCallbacksAndMessages(null);

        // Создаем диалог проигрыша
        new AlertDialog.Builder(this)
                .setTitle("Кот отравился!")
                .setMessage("Вы столкнулись с опасным объектом!")
                .setPositiveButton("Новая игра", (dialog, which) -> restartGame())
                .setCancelable(false)
                .show();
    }

    private void showWinDialog() {
        // Останавливаем все движения
        isMovementActive = false;
        movementHandler.removeCallbacksAndMessages(null);
        gameLoopHandler.removeCallbacksAndMessages(null);

        // Создаем диалог победы
        new AlertDialog.Builder(this)
                .setTitle("Поздравляем!")
                .setMessage("Вы успешно собрали 30 объектов!")
                .setPositiveButton("Новая игра", (dialog, which) -> restartGame())
                .setNegativeButton("Вернуться", (dialog, which) -> startActivity(new Intent(this, SnegurAndCat.class)))
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        // Сбрасываем игру
        gameActive = true;
        score = 0;
        updateScore();

        // Очищаем все падающие объекты
        for (FallingObject obj : fallingObjects) {
            obj.view.setVisibility(View.INVISIBLE);
        }
        fallingObjects.clear();

        // Возвращаем игрока в центр
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) player.getLayoutParams();
        params.leftMargin = gameContainer.getWidth() / 2 - player.getWidth() / 2;
        player.setLayoutParams(params);

        // Запускаем игровой цикл
        startGameLoop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movementHandler.removeCallbacksAndMessages(null);
        gameLoopHandler.removeCallbacksAndMessages(null);

        // Очищаем кэш изображений
        for (int i = 0; i < imageCache.size(); i++) {
            Bitmap bitmap = imageCache.valueAt(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        imageCache.clear();

        // Очищаем объекты
        for (FallingObject obj : fallingObjects) {
            obj.view.setVisibility(View.INVISIBLE);
        }
        fallingObjects.clear();

        // Очищаем пул
        for (ImageView view : viewPool) {
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        }
        viewPool.clear();
    }
}
