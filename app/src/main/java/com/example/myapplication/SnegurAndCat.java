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

public class SnegurAndCat extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_cat);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView snegurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView catImage = findViewById(R.id.catImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = findViewById(R.id.playButton);

        MaterialButton infoButton = findViewById(R.id.infoButton);
        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Снегурочка: — Добрый день, уважаемый Кот!  Не могли бы вы помочь мне? Я ищу Александра Николаевича Островского. Водопроводчик сказал, что вы могли его видеть...",
                "Кот: — Мяу-у...  Возможно, видел. Возможно, нет... А что это у тебя там пахнет так аппетитно?",
                "Снегурочка: — Это вам!",
                "Кот: — Мррр... Неплохо, неплохо,  Теперь о твоем Островском... Честно говоря, мне лень что-то вспоминать. Но! У пса нюх хороший. Он точно след учует. Беги к нему, пока я... э-э-э... обдумываю твой вопрос.",
        };

        Runnable onDialogsComplete = () -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("move_to_next", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                snegurochkaImage,
                catImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Кот",
                null,
                onDialogsComplete, // Используем callback для завершения
                () -> startActivity(new Intent(this, KotGameActivity.class))
        );

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}