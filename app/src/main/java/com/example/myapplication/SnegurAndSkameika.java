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

public class SnegurAndSkameika extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snegur_and_skameika);

        TextView dialogText = findViewById(R.id.dialogText);
        ImageView sngurochkaImage = findViewById(R.id.sngurochkaImage);
        ImageView skameikaImage = findViewById(R.id.skameikaImage);
        FrameLayout dialogBox = findViewById(R.id.dialogBox);
        Button fullScreenButton = findViewById(R.id.fullScreenButton);
        MaterialButton nextButton = findViewById(R.id.nextButton);

        MaterialButton playButton = null; // если ее нет на экране активити
        // playButton = findViewById(R.id.playButton); // если есть

        MaterialButton backButton = findViewById(R.id.backButton);

        String[] dialogs = {
                "Скамья примирения: — Эй, снежная девица! Не спеши так, подойди-ка сюда на минутку!",
                "Скамья примирения: — Ты, случаем, не та самая внучка Островского, что по всему городу его разыскивает?",
                "Снегурочка: — Да, это я! Но как вы догадались?",
                "Скамья примирения: — О, милое дитя, в нашем городе новости разносятся быстрее вороньих крыльев. Только что мирил двух купцов - те так ругались, что даже чайки с набережной разлетелись. Но даже в самой жаркой перепалке они успели упомянуть, что видели твоего дедушку.",
                "Снегурочка: — Правда? Где же он?",
                "Скамья примирения: — Как мне говорили, он торопился к каким-то темным буквам.",
                "Снегурочка: — Наверное они говорили про надпись “Любовь”",
                "Скамья примирения: — Тогда тебе прямая дорога туда",
                "Снегурочка: — Спасибо вам огромное! Вы так помогли",
                "Скамья примирения: — Пустяки, Удачи в поисках! И помни - иногда чтобы найти, нужно сначала потеряться."
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
                sngurochkaImage,
                skameikaImage,
                dialogBox,
                fullScreenButton,
                nextButton,
                playButton,
                "Снегурочка",
                "Скамья примирения",
                null,
                onDialogsComplete, // Используем callback для завершения
                null
        );

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}