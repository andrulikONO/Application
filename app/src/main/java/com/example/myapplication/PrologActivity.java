package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

public class PrologActivity extends AppCompatActivity {

    private int currentStep = 0;

    private final int[] images = {
            R.drawable.page_prolog1,
            R.drawable.page_prolog2,
            R.drawable.page_prolog3
    };

    private final String[] texts = {
            "Ты стоишь на главной площади старинного русского городка. Кружевные домики с резными ставнями, брусчатые улочки, золотые купола церквей — всё вокруг словно сошло со страниц сказки. В воздухе пахнет свежей выпечкой и мёдом, а где-то вдалеке играет гармонь.",
            "Но тебе не до гуляний — ты здесь с важным заданием! Дело в том, что сегодня утром Снегурочка потеряла своего дедушку, Александра Николаевича Островского. Они должны были встретиться у театра, но писатель куда-то исчез. Теперь внучке нужно найти его до заката, иначе... иначе он уйдёт в свой творческий мир и забудет про всё на свете!",
            "К счастью, город полон подсказок. Нужно только быть внимательным, спрашивать у жителей и не бояться помогать другим. Готов отправиться в путь? Тогда вперёд — Снегурочка уже ждёт у «сковородки»!\n"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prolog);

        ImageView backgroundImage = findViewById(R.id.backgroundImage);
        TextView dialogText = findViewById(R.id.dialogText);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        backgroundImage.setImageResource(images[0]);
        dialogText.setText(texts[0]);

        nextButton.setOnClickListener(v -> {
            currentStep++;
            if (currentStep < images.length) {
                backgroundImage.setImageResource(images[currentStep]);
                dialogText.setText(texts[currentStep]);
            } else {
                Intent intent = new Intent(PrologActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}