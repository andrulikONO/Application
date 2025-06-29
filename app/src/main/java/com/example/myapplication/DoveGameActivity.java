package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoveGameActivity extends AppCompatActivity {
    private ImageView dove;
    private TextView titleText, nextText;
    private int dovePosition = 0;
    private int maxHeight;
    private boolean isFlying = false;
    private Handler handler = new Handler();
    private Runnable fallRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dove_game);

        dove = findViewById(R.id.dove);
        titleText = findViewById(R.id.titleText);
        nextText = findViewById(R.id.nextText);

        // Получаем максимальную высоту экрана
        dove.post(() -> {
            maxHeight = titleText.getBottom() - dove.getHeight();
        });

        // Обработчик нажатия на голубя
        dove.setOnClickListener(v -> {
            if (!isFlying) {
                isFlying = true;
                flyUp();
            }
        });

        // Runnable для падения голубя
        fallRunnable = new Runnable() {
            @Override
            public void run() {
                if (dovePosition > 0) {
                    dovePosition -= 20;
                    dove.setTranslationY(-dovePosition);
                    handler.postDelayed(this, 50);
                } else {
                    isFlying = false;
                }
            }
        };
    }

    private void flyUp() {
        dovePosition += 40;
        dove.setTranslationY(-dovePosition);

        if (dovePosition >= maxHeight) {
            titleText.setText("Спасибо");
            nextText.setVisibility(View.VISIBLE);
            nextText.setText("Далее");
            return;
        }

        handler.postDelayed(() -> {
            if (dovePosition < maxHeight) {
                flyUp();
            }
        }, 50);

        // Начинаем падение, если не было нажатия в течение 1 секунды
        handler.postDelayed(() -> {
            if (isFlying && dovePosition < maxHeight) {
                handler.post(fallRunnable);
            }
        }, 1000);
    }
}