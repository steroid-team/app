package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListSelectionLogicTest {

    @Test
    public void renameTodoWorks() {

        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            final String TODO_DESC_2 = "Homework";

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeRight()));

            onView(withText("Rename your to-do list")).check(matches(isDisplayed()));

            onView(withId(R.id.alert_dialog_edit_text))
                    .inRoot(isDialog())
                    .perform(clearText(), typeText(TODO_DESC_2));
            closeSoftKeyboard();
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

            onView(withText("Rename your to-do list")).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, TODO_DESC_2)));
        }
    }

    public static Matcher<View> atPositionCheckText(
            final int position, @NonNull final String expectedText) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "View holder at position "
                                + position
                                + ", expected: "
                                + expectedText
                                + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View todoView = view.getChildAt(position);
                TextView bodyView = todoView.findViewById(R.id.layout_todo_list_text);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }
}
