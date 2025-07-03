package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoveGameActivity extends AppCompatActivity {
    private ImageView dove;
    private TextView titleText;
    private LinearLayout buttonContainer;
    private Button playAgainButton;
    private Button nextSculptureButton;
    private TextView nextText;

    private int dovePosition = 0;
    private int maxHeight;
    private boolean isGameCompleted = false;
    private Handler handler = new Handler();
    private Runnable flyRunnable;
    private Runnable fallRunnable;

    private int requiredTaps = 10;
    private int currentTaps = 0;
    private boolean isNeg = false;
    private int tapProgress = 0; // Прогресс от каждого нажатия

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dove_game);

        // Инициализация View элементов
        dove = findViewById(R.id.dove);
        titleText = findViewById(R.id.titleText);
        nextText = findViewById(R.id.nextText);
        buttonContainer = findViewById(R.id.buttonContainer);
        playAgainButton = findViewById(R.id.restartButton);
        nextSculptureButton = findViewById(R.id.nextSculptureButton);

        buttonContainer.setVisibility(View.GONE);
        nextText.setVisibility(View.VISIBLE);
        nextText.setText("Нажмите на голубя " + requiredTaps + " раз");

        dove.post(() -> {
            maxHeight = 440; // 90% высоты
        });

        // Обработчик нажатия на голубя
        dove.setOnClickListener(v -> {
            if (!isGameCompleted) {
                currentTaps++;
                tapProgress += maxHeight / requiredTaps; // Каждое нажатие добавляет часть высоты
                nextText.setText("Нажатий: " + currentTaps + " из " + requiredTaps);

                // Удаляем предыдущие анимации
                handler.removeCallbacks(flyRunnable);
                handler.removeCallbacks(fallRunnable);

                // Запускаем новую анимацию полета
                flyRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (currentTaps < requiredTaps) {
                            dovePosition += 15; // Плавное увеличение позиции
                            dove.setTranslationY(-dovePosition);
                            handler.postDelayed(this, 30);
                        } else if (currentTaps >= requiredTaps) {
                            completeGame();
                        } else if (!isNeg) {
                            startFalling();
                        }
                    }
                };

                handler.post(flyRunnable);

                // Запланировать падение через 1 секунду бездействия
                handler.postDelayed(() -> {
                    if (!isGameCompleted && dovePosition > 0) {
                        startFalling();
                    }
                }, 1000);
            }
        });

        // Runnable для падения голубя
        fallRunnable = new Runnable() {
            @Override
            public void run() {
                if (dovePosition > 0 && !isGameCompleted) {
                    dovePosition = Math.max(0, dovePosition - 8); // Плавное падение
                    dove.setTranslationY(-dovePosition);
                    handler.postDelayed(this, 30);
                }
            }
        };

        playAgainButton.setOnClickListener(v -> resetGame());
        nextSculptureButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("move_to_next", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void startFalling() {
        isNeg = true;
        handler.removeCallbacks(flyRunnable);
        handler.post(fallRunnable);
    }

    private void completeGame() {
        isGameCompleted = true;
        titleText.setText("Победа!");
        nextText.setText("Голубь взлетел!");
        buttonContainer.setVisibility(View.VISIBLE);
        handler.removeCallbacks(flyRunnable);
        handler.removeCallbacks(fallRunnable);

        // Анимация полного взлета
        flyRunnable = new Runnable() {
            @Override
            public void run() {
                if (dovePosition < maxHeight) {
                    dovePosition += 20;
                    dove.setTranslationY(-dovePosition);
                    handler.postDelayed(this, 30);
                }
            }
        };
        handler.post(flyRunnable);
    }

    private void resetGame() {
        isGameCompleted = false;
        dovePosition = 0;
        currentTaps = 0;
        tapProgress = 0;
        dove.setTranslationY(0);
        titleText.setText("Помогите голубю взлететь");
        nextText.setText("Нажмите на голубя " + requiredTaps + " раз");
        buttonContainer.setVisibility(View.GONE);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}