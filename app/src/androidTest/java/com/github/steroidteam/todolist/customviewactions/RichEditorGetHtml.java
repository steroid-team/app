package com.github.steroidteam.todolist.customviewactions;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import jp.wasabeef.richeditor.RichEditor;
import org.hamcrest.Matcher;

public class RichEditorGetHtml implements ViewAction {
    public String contents;

    @Override
    public Matcher<View> getConstraints() {
        return isAssignableFrom(RichEditor.class);
    }

    public String getDescription() {
        return "Get the HTML contents of a RichEditor";
    }

    @Override
    public void perform(UiController uiController, View view) {
        RichEditor richEditor = (RichEditor) view;
        contents = richEditor.getHtml();
    }
}
