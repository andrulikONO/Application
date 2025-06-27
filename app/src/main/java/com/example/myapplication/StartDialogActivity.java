package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartDialogActivity extends AppCompatActivity {

    /**
     *
     *     пример работы диалога между персонажами
     *     мб можно будет вынести в отдельный класс данный прикол, потому что
     *     каждый активи дублировать код не оч прикольно
     *
     *
     */
    private TextView dialogText;
    private ImageView sngurochkaImage, vodoprovodchikImage;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_dialog);

        dialogText = findViewById(R.id.dialogText);
        sngurochkaImage = findViewById(R.id.sngurochkaImage);
        vodoprovodchikImage = findViewById(R.id.vodoprovodchikImage);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);

        updateDialog();

        fullScreenButton.setOnClickListener(v -> {
            currentDialogIndex++;

            if (currentDialogIndex >= dialogs.length) {
                finish();
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
} 