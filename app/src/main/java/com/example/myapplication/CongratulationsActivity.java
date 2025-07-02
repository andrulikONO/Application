package com.example.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import java.util.Random;

public class CongratulationsActivity extends AppCompatActivity {
    private FrameLayout fireworksContainer;
    private Random random = new Random();
    private Handler handler = new Handler();
    private int screenWidth, screenHeight;
    private static final int FIREWORK_COUNT = 100;
    private static final int PARTICLE_COUNT = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);

        TextView thankYouText = findViewById(R.id.thankYouText);
        fireworksContainer = findViewById(R.id.fireworksContainer);
        MaterialButton exitButton = findViewById(R.id.exitButton);

        // Получаем размеры экрана
        fireworksContainer.post(() -> {
            screenWidth = fireworksContainer.getWidth();
            screenHeight = fireworksContainer.getHeight();
            startFireworksShow();
        });

        // Обработчик кнопки выхода
        exitButton.setOnClickListener(v -> {
            // Закрываем все активити и выходим из приложения
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Завершаем процесс приложения
            finishAffinity();
            System.exit(0);
        });
    }

    private void startFireworksShow() {
        for (int i = 0; i < FIREWORK_COUNT; i++) {
            handler.postDelayed(() -> createFirework(), i * 500);
        }
    }

    private void createFirework() {
        // Случайная позиция для салюта
        int x = random.nextInt(screenWidth);
        int y = random.nextInt(screenHeight / 2) + screenHeight / 4;

        // Создаем "ракету"
        View rocket = new View(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(10, 10);
        params.leftMargin = x;
        params.topMargin = screenHeight;
        rocket.setLayoutParams(params);
        rocket.setBackgroundColor(getRandomColor());
        fireworksContainer.addView(rocket);

        // Анимация полета ракеты вверх
        ValueAnimator rocketAnim = ValueAnimator.ofInt(screenHeight, y);
        rocketAnim.setDuration(800);
        rocketAnim.setInterpolator(new DecelerateInterpolator());
        rocketAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) rocket.getLayoutParams();
            p.topMargin = value;
            rocket.setLayoutParams(p);
        });
        rocketAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                fireworksContainer.removeView(rocket);
                explodeFirework(x, y);
            }
        });
        rocketAnim.start();
    }

    private void explodeFirework(int x, int y) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            createParticle(x, y);
        }
    }

    private void createParticle(int startX, int startY) {
        View particle = new View(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(15, 15);
        params.leftMargin = startX;
        params.topMargin = startY;
        particle.setLayoutParams(params);

        // Создаем круглую частицу с градиентом
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColors(new int[]{getRandomColor(), Color.TRANSPARENT});
        particle.setBackground(shape);

        fireworksContainer.addView(particle);

        // Анимация разлета частицы
        float angle = random.nextFloat() * (float) Math.PI * 2;
        float distance = 100 + random.nextInt(300);
        float endX = startX + (float) Math.cos(angle) * distance;
        float endY = startY + (float) Math.sin(angle) * distance;

        ValueAnimator animX = ValueAnimator.ofFloat(startX, endX);
        ValueAnimator animY = ValueAnimator.ofFloat(startY, endY);
        ValueAnimator animAlpha = ValueAnimator.ofFloat(1f, 0f);

        animX.addUpdateListener(a -> {
            float value = (float) a.getAnimatedValue();
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) particle.getLayoutParams();
            p.leftMargin = (int) value;
            particle.setLayoutParams(p);
        });

        animY.addUpdateListener(a -> {
            float value = (float) a.getAnimatedValue();
            FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) particle.getLayoutParams();
            p.topMargin = (int) value;
            particle.setLayoutParams(p);
        });

        animAlpha.addUpdateListener(a -> {
            particle.setAlpha((float) a.getAnimatedValue());
        });

        animX.setDuration(1500);
        animY.setDuration(1500);
        animAlpha.setDuration(1500);
        animX.setInterpolator(new AccelerateInterpolator());
        animY.setInterpolator(new AccelerateInterpolator());

        animX.start();
        animY.start();
        animAlpha.start();

        // Удаляем частицу после анимации
        handler.postDelayed(() -> {
            if (particle.getParent() != null) {
                fireworksContainer.removeView(particle);
            }
        }, 1500);
    }

    private int getRandomColor() {
        return Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}