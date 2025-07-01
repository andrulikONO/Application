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

public class SnegurAndGolub extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_golub);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView golubImage = findViewById(R.id.golubImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        // MaterialButton playButton = null; // если ее нет на экране активити
        MaterialButton playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton infoButton = findViewById(R.id.infoButton);

        String[] dialogs = {
                "Снегурочка: — Добрый голубь!  — Пёс сказал, что ты можешь помочь мне найти Александра Николаевича Островского. Он куда-то пропал...",
                "Голубь: — Островского, говоришь? Подожди-ка...",
                "Голубь: — Видел его! Сидит на скамейке у своего бюста на проспекте Мира. В блокноте что-то записывает, очень сосредоточен!",
                "Снегурочка: — Спасибо тебе огромное, дорогой голубь! Я так тебе благодарна!",
                "Голубь: — Курлы!  Всегда рад помочь. Теперь беги скорее!"
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                golubImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Голубь",
                null,
                () -> startActivity(new Intent(this, SnegurAndOstrovski.class)),
                null // TODO сделать навигацию на игру, когда ее сделают вообще
        );
    }
}
