package com.github.steroidteam.todolist.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

public class Utils {

    public static void hideKeyboard(@NonNull View view) {
        final InputMethodManager imm =
                (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
