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

public class SnegurAndLadia extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_ladia);

        DialogStateProvider.getInstance().setCurrentDialogIndex(4);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView ladiaImage = findViewById(R.id.ladiaImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        // MaterialButton playButton = null;  если ее нет на экране активити
        MaterialButton playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton infoButton = findViewById(R.id.infoButton);

        String[] dialogs = {
                "Снегурочка: — Добрая Ладья, скажи, ты не увозила куда-то Островского?",
                "Ладья: — Нет, дитя, я только что вернулась с рыбалки. Но знаю, водопроводчик на улице 1 Мая мог его видеть. Возьми эту рыбу в дорогу - вдруг пригодится!",
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
                ladiaImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Ладья",
                null,
                onDialogsComplete, // Используем callback для завершения
                () -> startActivity(new Intent(this, RookGameActivity.class))
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
