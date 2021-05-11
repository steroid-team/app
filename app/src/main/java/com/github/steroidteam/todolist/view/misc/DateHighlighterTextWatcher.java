package com.github.steroidteam.todolist.view.misc;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import java.util.List;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

public class DateHighlighterTextWatcher implements TextWatcher {
    private final PrettyTimeParser timeParser;
    private final Context context;

    public DateHighlighterTextWatcher(Context context, PrettyTimeParser timeParser) {
        this.context = context;
        this.timeParser = timeParser;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        List<DateGroup> parsedDates = timeParser.parseSyntax(editable.toString());

        // Remove any potentially existing spans (just in case a date was recognized before while
        // typing). We don't use editable.clearSpans(), as it makes the text field behave
        // unexpectedly.
        for (DueDateInputSpan span :
                editable.getSpans(0, editable.length(), DueDateInputSpan.class)) {
            editable.removeSpan(span);
        }

        // The parser tends to identify isolated numbers (like "1" or "23") as times. To avoid
        // sentences like "Buy 3 avocados" to be matched, we simply remove those cases.
        // This does not break cases like "Buy 3 avocados at 12", since the time is parsed as "at
        // 12".
        for (DateGroup dg : parsedDates) {
            if (dg.getText().matches("\\d{1,2}")) parsedDates.remove(dg);
        }

        if (parsedDates.size() == 0) return;

        // Multiple dates might appear in the text. We assume that the target one is the last one.
        DateGroup lastDate = parsedDates.get(parsedDates.size() - 1);

        int startPosition = lastDate.getPosition() - 1;
        editable.setSpan(
                new DueDateInputSpan(context, lastDate.getDates().get(0)),
                startPosition,
                startPosition + lastDate.getText().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
