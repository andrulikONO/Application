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

public class SnegurAndDog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_dog);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView dogImage = findViewById(R.id.dogImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = null; // если ее нет на экране активити
        // playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Снегурочка: — Добрый пёс!  Я ищу Александра Николаевича Островского. Кот сказал, что у тебя лучший нюх в городе...",
                "Пес-спасатель: — Сегодня слишком много запахов — и свежая выпечка из булочной, и краска от ремонтников, и эти новые цветы у фонтана... Я не могу выделить его след. Но знаешь кто мог бы помочь? Наш голубь!",
                "Снегурочка: — Голубь?",
                "Пес-спасатель: — Да!  он может летать высоко и видеть всё сверху. Если Островский где-то в центре — голубь его заметит",
                "Снегурочка: — Спасибо тебе большое! Я сейчас же к нему отправлюсь",
                "Пес-спасатель: — Гав! Удачи в поисках!"
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                dogImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Пес-спасатель",
                null,
                () -> startActivity(new Intent(this, SnegurAndGolub.class)),
                null
        );
    }
}
