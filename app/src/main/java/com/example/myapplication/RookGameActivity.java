package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;
public class RookGameActivity extends AppCompatActivity{
    private PlayerView playerView;
    private JoyStickView joyStick;
    private TextView scoreText;

    // Текущее состояние джойстика
    private float currentAngle = 0;
    private float currentStrength = 0;
    private static final float PLAYER_SPEED = 8f;

    // Игровой цикл
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            updateGame();
            handler.postDelayed(this, 16); // ~60 FPS
        }
    };

    private long lastUpdateTime = 0;
    private boolean gameWon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rook_game);

        playerView = findViewById(R.id.playerView);
        joyStick = findViewById(R.id.joystick);
        scoreText = findViewById(R.id.scoreText);

        // Устанавливаем слушатель джойстика
        joyStick.setListener((angle, strength) -> {
            currentAngle = angle;
            currentStrength = strength;
        });

        // Устанавливаем слушатель изменения счета
        playerView.setOnScoreChangedListener(score -> {
            scoreText.setText("Счет: " + score);

            // Если достигли 30 очков - победа
            if (score >= 30 && !gameWon) {
                gameWon = true;
                showWinDialog();
                return;
            }

            // Если все цели собраны, создаем новые (до победы)
            if (score % 10 == 0 && !gameWon) {
                playerView.postDelayed(() -> {
                    playerView.createTargets(10);
                    playerView.invalidate();
                }, 500);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameWon) {
            lastUpdateTime = System.currentTimeMillis();
            handler.post(gameLoop);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(gameLoop);
    }

    private void updateGame() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = lastUpdateTime > 0 ? (currentTime - lastUpdateTime) / 1000f : 0.016f;
        lastUpdateTime = currentTime;

        if (currentStrength > 0 && !gameWon) {
            // Рассчитываем смещение
            double angleRad = Math.toRadians(currentAngle);
            float dx = (float) (Math.cos(angleRad) * currentStrength * PLAYER_SPEED * deltaTime * 60);
            float dy = (float) (Math.sin(angleRad) * currentStrength * PLAYER_SPEED * deltaTime * 60);

            // Двигаем игрока
            playerView.move(dx, dy);
        }
    }

    // Показать диалоговое окно победы
    private void showWinDialog() {
        // Останавливаем игровой цикл
        handler.removeCallbacks(gameLoop);

        // Создаем диалоговое окно
        new AlertDialog.Builder(this)
                .setTitle("Победа!")
                .setMessage("Вы набрали 30 очков!\nВы успешно завершили уровень и можете идти дальше.")
                .setPositiveButton("Новая игра", (dialog, which) -> restartGame())
                .setNegativeButton("Вернуться", (dialog, which) -> finish())
                .setCancelable(false)
                .setOnDismissListener(dialog -> {
                    if (!gameWon) {
                        // Если диалог закрыли без выбора, продолжаем игру
                        lastUpdateTime = System.currentTimeMillis();
                        handler.post(gameLoop);
                    }
                })
                .show();
    }

    // Перезапуск игры
    private void restartGame() {
        gameWon = false;
        playerView.resetGame();
        joyStick.resetPosition();
        scoreText.setText("Счет: 0");

        // Запускаем игровой цикл
        lastUpdateTime = System.currentTimeMillis();
        handler.post(gameLoop);
    }
}
