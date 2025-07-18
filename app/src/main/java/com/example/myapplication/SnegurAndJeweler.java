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
import com.example.myapplication.DialogStateProvider;

public class SnegurAndJeweler extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_jeweler);

        DialogStateProvider.getInstance().setCurrentDialogIndex(1);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView jewelerImage = findViewById(R.id.jewelerImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        // MaterialButton playButton = null;  если ее нет на экране активити
        MaterialButton playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton infoButton = findViewById(R.id.infoButton);

        String[] dialogs = {
                "Снегурочка: — Добрый день! Вы не видели Александра Николаевича Островского? Мы должны были встретиться...",
                "Ювелир: — А, Снегурочка! Как раз вовремя. Вот кольцо — его приносили на реставрацию. Будь добра, отнеси его к \"Любви\".",
                "Снегурочка: — Но я ищу дедушку... Он пропал!",
                "Ювелир: — Понимаю твоё беспокойство. Давай так — ты отнесёшь кольцо, а я тем временем спрошу у своих клиентов. Ко мне весь город заходит! Кто-то да видел Островского.",
                "Снегурочка: — Вы правы, А куда именно идти?",
                "Ювелир: — По улице Чайковского до больших красных букв. А я пока составлю список, где мог быть твой дедушка. Заходи на обратном пути — вдруг будут новости!",
                "Снегурочка: — Спасибо вам большое!"
        };

        Runnable onDialogsComplete = () -> {
            DialogStateProvider.getInstance().setDialogCompleted(true);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("move_to_next", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                jewelerImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Ювелир",
                null,
                onDialogsComplete, // Используем callback для завершения
                () -> startActivity(new Intent(this, PuzzleGameActivity.class))
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
