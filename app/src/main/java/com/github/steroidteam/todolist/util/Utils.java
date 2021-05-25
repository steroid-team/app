package com.github.steroidteam.todolist.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import java.util.Objects;

public class Utils {

    public static void hideKeyboard(@NonNull View view) {
        final InputMethodManager imm =
                (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void checkNonNullArgs(Object... objects) {
        for (Object o : objects) Objects.requireNonNull(o);
    }
}
