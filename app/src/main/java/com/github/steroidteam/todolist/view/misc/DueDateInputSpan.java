package com.github.steroidteam.todolist.view.misc;

import android.content.Context;
import android.text.style.ForegroundColorSpan;
import androidx.core.content.ContextCompat;
import com.github.steroidteam.todolist.R;
import java.util.Date;

public class DueDateInputSpan extends ForegroundColorSpan {
    private final Date date;

    public DueDateInputSpan(Context context, Date date) {
        super(ContextCompat.getColor(context, R.color.dark_blue));
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
