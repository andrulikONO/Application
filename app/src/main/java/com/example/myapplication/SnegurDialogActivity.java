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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_dialog);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView noneImage = findViewById(R.id.noneImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = null; // если ее нет на экране активити
        // playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Рассказчик: Снегурочка стояла у \"сковородки\", переминаясь с ноги на ногу. \"Где же дедушка?\" - волновалась она. Решив начать поиски, она отправилась к ювелиру."
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                noneImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Рассказчик",
                "",
                null,
                () -> startActivity(new Intent(this, SnegurAndJeweler.class)),
                null
        );
    }
} 