package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 *
 * ДЛЯ ТЕСТА
 *
 */
public class SnegurAndVodoprovod extends AppCompatActivity {

    private TextView dialogText;
    private ImageView sngurochkaImage, vodoprovodchikImage;
    private FrameLayout dialogBox;
    private Button fullScreenButton;
    private MaterialButton nextButton;
    private int currentDialogIndex = 0;
    private final String[] dialogs = {
            "Снегурочка: Привет, я Снегурочка!",
            "Сантехник: Здравствуй, я Сантехник!",
            "Снегурочка: Поможешь с трубами?",
            "Сантехник: Конечно, покажи где протекает!",
            "Снегурочка: Это в моем ледяном дворце!",
            "Сантехник: Тогда пошли скорее!"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_vodoprovod);

        dialogText = findViewById(R.id.dialogText);
        sngurochkaImage = findViewById(R.id.sngurochkaImage);
        vodoprovodchikImage = findViewById(R.id.vodoprovodchikImage);
        dialogBox = findViewById(R.id.dialogBox);
        fullScreenButton = findViewById(R.id.fullScreenButton);
        nextButton = findViewById(R.id.nextButton);

        // ТЕСТОВАЯ ОБРАБОТКА КНОПОК
        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton infoButton = findViewById(R.id.infoButton);
        
        backButton.setOnClickListener(v -> {
            android.util.Log.d("SnegurAndVodoprovod", "Кнопка НАЗАД нажата");
            android.widget.Toast.makeText(this, "Кнопка НАЗАД работает!", android.widget.Toast.LENGTH_SHORT).show();
            finish();
        });
        
        infoButton.setOnClickListener(v -> {
            android.util.Log.d("SnegurAndVodoprovod", "Кнопка ИНФО нажата");
            android.widget.Toast.makeText(this, "Кнопка ИНФО работает!", android.widget.Toast.LENGTH_SHORT).show();
        });

        nextButton.setOnClickListener(v -> finish());
        nextButton.setVisibility(View.GONE);

        updateDialog();

        fullScreenButton.setOnClickListener(v -> {
            currentDialogIndex++;

            if (currentDialogIndex >= dialogs.length) {
                showCompletionUI();
                return;
            }

            updateDialog();
        });
    }

    private void updateDialog() {
        String currentDialog = dialogs[currentDialogIndex];
        dialogText.setText(currentDialog);

        if (currentDialog.startsWith("Снегурочка:")) {
            sngurochkaImage.setAlpha(1.0f);
            vodoprovodchikImage.setAlpha(0.6f);
        } else {
            sngurochkaImage.setAlpha(0.6f);
            vodoprovodchikImage.setAlpha(1.0f);
        }
    }

    private void showCompletionUI() {

        sngurochkaImage.setVisibility(View.GONE);
        vodoprovodchikImage.setVisibility(View.GONE);
        dialogBox.setVisibility(View.GONE);

        fullScreenButton.setEnabled(false);
        fullScreenButton.setAlpha(0f);
        

        nextButton.setVisibility(View.VISIBLE);
        nextButton.setAlpha(0f);
        nextButton.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
                
    }
}