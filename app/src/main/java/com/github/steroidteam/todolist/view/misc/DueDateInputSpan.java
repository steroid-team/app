package com.github.steroidteam.todolist.view.misc;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import com.github.steroidteam.todolist.R;
import java.util.Date;

public class DueDateInputSpan extends ForegroundColorSpan {
    private final Date date;

    private static int fetchPrimaryColour(Context context) {
        TypedValue primaryColor = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, primaryColor, true);
        return primaryColor.data;
    }

    public DueDateInputSpan(Context context, Date date) {
        super(DueDateInputSpan.fetchPrimaryColour(context));
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
