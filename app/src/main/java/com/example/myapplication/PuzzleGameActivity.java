package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class PuzzleGameActivity extends AppCompatActivity{
    private GridLayout gridLayout;
    private ArrayList<Bitmap> pieces = new ArrayList<>();
    private ArrayList<Integer> originalPositions = new ArrayList<>(); // Храним исходные позиции
    private int pieceWidth, pieceHeight;
    private final int ROWS = 4;
    private final int COLS = 3;
    private float startX, startY;
    private ImageView selectedPiece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);
        gridLayout = findViewById(R.id.gridLayout);

        // Получаем размеры экрана
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Рассчитываем размеры фрагментов
        pieceWidth = (int) (screenWidth * 0.8) / COLS;
        pieceHeight = (int) (screenHeight * 0.6) / ROWS;

        setupPuzzle();
    }

    private void setupPuzzle() {
        // Загружаем и масштабируем изображение
        Bitmap original = BitmapFactory.decodeResource(getResources(), R.drawable.puzzleall);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                original,
                pieceWidth * COLS,
                pieceHeight * ROWS,
                true
        );

        // Разрезаем изображение на части
        pieces.clear();
        originalPositions.clear();

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                pieces.add(Bitmap.createBitmap(
                        scaledBitmap,
                        x * pieceWidth,
                        y * pieceHeight,
                        pieceWidth,
                        pieceHeight
                ));
                originalPositions.add(y * COLS + x); // Сохраняем исходные позиции
            }
        }

        // Создаем перемешанный список позиций
        ArrayList<Integer> shuffledIndices = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices);

        // Создаем перемешанные списки
        ArrayList<Bitmap> shuffledPieces = new ArrayList<>();
        ArrayList<Integer> shuffledPositions = new ArrayList<>();

        for (int i = 0; i < shuffledIndices.size(); i++) {
            int index = shuffledIndices.get(i);
            shuffledPieces.add(pieces.get(index));
            shuffledPositions.add(originalPositions.get(index));
        }

        pieces = shuffledPieces;
        originalPositions = shuffledPositions;

        createPuzzleBoard();
    }

    private void createPuzzleBoard() {
        gridLayout.removeAllViews();
        gridLayout.setRowCount(ROWS);
        gridLayout.setColumnCount(COLS);

        for (int i = 0; i < pieces.size(); i++) {
            ImageView piece = new ImageView(this);
            piece.setImageBitmap(pieces.get(i));
            piece.setTag(originalPositions.get(i)); // Сохраняем исходную позицию

            // Устанавливаем обработчик свайпов
            piece.setOnTouchListener(new PieceTouchListener());

            // Устанавливаем размеры
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = pieceWidth;
            params.height = pieceHeight;
            params.setMargins(2, 2, 2, 2);
            piece.setLayoutParams(params);

            gridLayout.addView(piece);
        }
    }

    private class PieceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    selectedPiece = (ImageView) v;
                    v.setAlpha(0.7f); // Подсветка выбранного элемента
                    return true;

                case MotionEvent.ACTION_UP:
                    v.setAlpha(1.0f); // Возвращаем прозрачность

                    float endX = event.getRawX();
                    float endY = event.getRawY();
                    float deltaX = endX - startX;
                    float deltaY = endY - startY;

                    if (Math.abs(deltaX) > 50 || Math.abs(deltaY) > 50) {
                        int direction = 0;

                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            direction = deltaX > 0 ? 1 : 3; // right : left
                        } else {
                            direction = deltaY > 0 ? 2 : 4; // down : up
                        }

                        int position = gridLayout.indexOfChild(selectedPiece);
                        int targetPosition = -1;

                        switch (direction) {
                            case 1: // Right
                                if (position % COLS < COLS - 1)
                                    targetPosition = position + 1;
                                break;
                            case 2: // Down
                                if (position / COLS < ROWS - 1)
                                    targetPosition = position + COLS;
                                break;
                            case 3: // Left
                                if (position % COLS > 0)
                                    targetPosition = position - 1;
                                break;
                            case 4: // Up
                                if (position / COLS > 0)
                                    targetPosition = position - COLS;
                                break;
                        }

                        if (targetPosition != -1) {
                            swapPieces(position, targetPosition);

                            // Проверяем решение
                            if (isPuzzleSolved()) {
                                showGameOverDialog();
                            }
                        }
                    }
                    return true;
            }
            return false;
        }
    }

    private void swapPieces(int pos1, int pos2) {
        ImageView piece1 = (ImageView) gridLayout.getChildAt(pos1);
        ImageView piece2 = (ImageView) gridLayout.getChildAt(pos2);

        // Меняем изображения местами
        Bitmap tempBitmap = ((android.graphics.drawable.BitmapDrawable) piece1.getDrawable()).getBitmap();
        piece1.setImageBitmap(((android.graphics.drawable.BitmapDrawable) piece2.getDrawable()).getBitmap());
        piece2.setImageBitmap(tempBitmap);

        // Меняем теги местами
        Object tempTag = piece1.getTag();
        piece1.setTag(piece2.getTag());
        piece2.setTag(tempTag);
    }

    private boolean isPuzzleSolved() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView piece = (ImageView) gridLayout.getChildAt(i);
            int currentPosition = (int) piece.getTag();
            if (currentPosition != i) {
                return false;
            }
        }
        return true;
    }

    private void showGameOverDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Пазл собран!")
                .setMessage("Хотите сыграть еще раз?")
                .setPositiveButton("Рестарт", (dialog, which) -> restartGame())
                .setNegativeButton("Выход",  (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        setupPuzzle(); // Полностью пересоздаем пазл
    }
}
