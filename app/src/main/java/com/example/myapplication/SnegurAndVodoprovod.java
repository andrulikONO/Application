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

public class SnegurAndVodoprovod extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_vodoprovod);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView vodoprovodchikImage = findViewById(R.id.vodoprovodchikImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = null; // если ее нет на экране активити
        // playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Снегурочка: — Добрый день!  Вы не видели Александра Николаевича Островского? Мне сказали, он мог быть здесь...",
                "Водопроводчик: — Островский? Да, действительно был! Спрашивал, где тут у нас самый мудрый кот обитает. Я ему сказал — в Сусанинском сквере, конечно! Тот усатый философ все про всех знает.",
                "Снегурочка: — Спасибо большое!",
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                vodoprovodchikImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Водопроводчик",
                null,
                () -> startActivity(new Intent(this, SnegurAndCat.class)),
                null
        );
    }
}