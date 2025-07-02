package com.example.myapplication;

/**
 * крутой класс
 */
public class DialogStateProvider {
    private static DialogStateProvider instance;
    private int currentDialogIndex = 0;
    private boolean dialogCompleted = false;

    private DialogStateProvider() {}

    public static DialogStateProvider getInstance() {
        if (instance == null) {
            instance = new DialogStateProvider();
        }
        return instance;
    }

    public int getCurrentDialogIndex() {
        return currentDialogIndex;
    }

    public void setCurrentDialogIndex(int index) {
        this.currentDialogIndex = index;
    }

    public boolean isDialogCompleted() {
        return dialogCompleted;
    }

    public void setDialogCompleted(boolean completed) {
        this.dialogCompleted = completed;
    }
} 