package com.example.myapplication;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;

public class DialogManager {
    private final String[] dialogs;
    private int currentDialogIndex = 0;
    private final TextView dialogText;
    private final ImageView leftCharacterImage;
    private final ImageView rightCharacterImage;
    private final FrameLayout dialogBox;
    private final Button fullScreenButton;
    private final MaterialButton nextButton;
    private final MaterialButton playButton;
    private final String leftCharacterName;
    private final String rightCharacterName;
    private final Runnable onDialogEnd;
    private final Runnable onNextButtonClick;
    private final Runnable onPlayButtonClick;

    /**
     *
     * @param dialogs массив строк диалогов
     * @param dialogText объект активити, где будет отображаться диалог
     * @param leftCharacterImage объект пересонаж слева
     * @param rightCharacterImage объект пересонаж справа
     * @param dialogBox обертка для отображения текста
     * @param fullScreenButton кнопка на весь дисплей
     * @param nextButton кнопка "Следующая скульптура"
     * @param playButton кнопка "Играть"
     * @param leftCharacterName имя персонажа слева
     * @param rightCharacterName имя персонажа справа
     * @param onDialogEnd обработчки события по окончанию диалога
     * @param onNextButtonClick коллбек вида () ->, обработка нажатия на кнопку "Следующая скульптура"
     * @param onPlayButtonClick коллбек вида () ->, обработка нажатия на кнопку "Играть"
     */
    public DialogManager(
            String[] dialogs,
            TextView dialogText,
            ImageView leftCharacterImage,
            ImageView rightCharacterImage,
            FrameLayout dialogBox,
            Button fullScreenButton,
            MaterialButton nextButton,
            @Nullable MaterialButton playButton,
            String leftCharacterName,
            String rightCharacterName,
            @Deprecated Runnable onDialogEnd,
            @Nullable Runnable onNextButtonClick,
            @Nullable Runnable onPlayButtonClick
    ) {
        this.dialogs = dialogs;
        this.dialogText = dialogText;
        this.leftCharacterImage = leftCharacterImage;
        this.rightCharacterImage = rightCharacterImage;
        this.dialogBox = dialogBox;
        this.fullScreenButton = fullScreenButton;
        this.nextButton = nextButton;
        this.playButton = playButton;
        this.leftCharacterName = leftCharacterName;
        this.rightCharacterName = rightCharacterName;
        this.onDialogEnd = onDialogEnd;
        this.onNextButtonClick = onNextButtonClick;
        this.onPlayButtonClick = onPlayButtonClick;
        setup();
    }

    private void setup() {
        if (nextButton != null) {
            nextButton.setVisibility(View.GONE);
            nextButton.setOnClickListener(v -> handleNextButtonClick());
        }
        if (playButton != null) {
            playButton.setVisibility(View.GONE);
            playButton.setOnClickListener(v -> handlePlayButtonClick());
        }
        if (fullScreenButton != null) {
            fullScreenButton.setOnClickListener(v -> {
                currentDialogIndex++;
                if (currentDialogIndex >= dialogs.length) {
                    showCompletionUI();
                    return;
                }
                updateDialog();
            });
        }
        updateDialog();
    }

    public void show() {
        updateDialog();
    }

    private void updateDialog() {
        if (dialogs == null || dialogs.length == 0 || currentDialogIndex >= dialogs.length) return;
        String currentDialog = dialogs[currentDialogIndex];
        if (dialogText != null) dialogText.setText(currentDialog);
        if (currentDialog.startsWith(leftCharacterName + ":")) {
            if (leftCharacterImage != null) leftCharacterImage.setAlpha(1.0f);
            if (rightCharacterImage != null) rightCharacterImage.setAlpha(0.6f);
        } else if (currentDialog.startsWith(rightCharacterName + ":")) {
            if (leftCharacterImage != null) leftCharacterImage.setAlpha(0.6f);
            if (rightCharacterImage != null) rightCharacterImage.setAlpha(1.0f);
        }
    }

    private void showCompletionUI() {
        if (leftCharacterImage != null) leftCharacterImage.setVisibility(View.GONE);
        if (rightCharacterImage != null) rightCharacterImage.setVisibility(View.GONE);
        if (dialogBox != null) dialogBox.setVisibility(View.GONE);

        if (fullScreenButton != null) {
            fullScreenButton.setEnabled(false);
            fullScreenButton.setAlpha(0f);
        }

        if (nextButton != null) {
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setAlpha(0f);
            nextButton.animate().alpha(1f).setDuration(300).start();
        }

        if (playButton != null) {
            playButton.setVisibility(View.VISIBLE);
        }
    }

    protected void handleNextButtonClick() {
        if (onNextButtonClick != null) {
            onNextButtonClick.run();
        } else if (onDialogEnd != null) {
            onDialogEnd.run();
        }
    }

    protected void handlePlayButtonClick() {
        if (onPlayButtonClick != null) {
            onPlayButtonClick.run();
        }
    }
} 