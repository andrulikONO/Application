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

public class SnegurAndOstrovski extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_ostrovski);

        DialogStateProvider.getInstance().setCurrentDialogIndex(9);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView ostrovskiImage = findViewById(R.id.ostrovskiImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = null; // если ее нет на экране активити
        // playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Снегурочка: — Александр Николаевич!  Я вас везде искала",
                "Островский: — Ах, это ты, внучка! Прости, совсем забыл о времени. Сидел, наблюдал за горожанами, новые сюжеты записывал. Вот, например, представляешь: молодая девушка ищет пропавшего писателя по всему городу...",
                "Снегурочка: — Так это же про нас!",
                "Островский: — Именно! Кстати, ты кольцо Любви передала?",
                "Снегурочка: — Передала! И рыбу коту отнесла, и с голубем говорила... Весь город обошла!",
                "Островский: — Вот и прекрасно, Значит, все сюжетные линии сошлись. Пойдём, внучка, мне есть что тебе рассказать"
        };

        Runnable onDialogsComplete = () -> {
            DialogStateProvider.getInstance().setDialogCompleted(true);
            startActivity(new Intent(this, CongratulationsActivity.class));
        };

        DialogManager dialogManager = new DialogManager(
                dialogs,
                dialogText,
                sngurochkaImage,
                ostrovskiImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Островский",
                null,
                onDialogsComplete,
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
