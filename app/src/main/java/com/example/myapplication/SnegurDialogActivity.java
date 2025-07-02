package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SnegurDialogActivity extends AppCompatActivity {

    DialogManager dialogManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_dialog);
        DialogStateProvider.getInstance().setCurrentDialogIndex(0);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView noneImage = findViewById(R.id.noneImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);
        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Рассказчик: Снегурочка стояла у \"сковородки\", переминаясь с ноги на ногу. \"Где же дедушка?\" - волновалась она. Решив начать поиски, она отправилась к ювелиру."
        };

        // Создаем callback для завершения диалогов
        Runnable onDialogsComplete = () -> {
            DialogStateProvider.getInstance().setDialogCompleted(true);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("move_to_next", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        };

        dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                noneImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                null, // playButton
                "Рассказчик",
                "",
                null,
                onDialogsComplete, // Используем наш callback вместо прямого перехода
                null
        );

        backButton.setOnClickListener(v -> {
            DialogStateProvider.getInstance().setDialogCompleted(false);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("move_to_next", false);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}