package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.ItemViewActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListSelectionActivityTest {

    @Rule
    public ActivityScenarioRule<ListSelectionActivity> activityRule =
            new ActivityScenarioRule<>(ListSelectionActivity.class);

    @Test
    public void openListWorks() {
        Intents.init();
        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, click()));

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(ItemViewActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void openNotesWorks() {
        Intents.init();

        onView(withId(R.id.notes_button)).perform(click());

        intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteSelectionActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void createTodoWorks() {

        final String TODO_DESC = "A Todo!";

        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(TODO_DESC));
        closeSoftKeyboard();
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_DESC)));
    }

    @Test
    public void renameTodoWorks() {
        final String TODO_DESC_2 = "Homework";

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(TODO_DESC_2));
        closeSoftKeyboard();
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_DESC_2)));
    }

    @Test
    public void deleteTodoWorks() {
        final String TODO_DESC = "A Todo!";
        final String TODO_DESC_2 = "Homework";

        // Add a to-do
        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(TODO_DESC_2));
        closeSoftKeyboard();
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_DESC_2)));
    }

    @Test
    public void cancelDeletionWorks() {
        final String TODO_DESC = "A Todo!";

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_DESC)));
    }

    @Test
    public void cancelRenamingWorks() {
        final String TODO_DESC = "A Todo!";
        final String TODO_DESC_2 = "Homework";
        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withText("Rename your to-do list")).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text)).perform(clearText(), typeText(TODO_DESC_2));
        closeSoftKeyboard();
        onView(withId(android.R.id.button2)).perform(click());

        onView(withText("Rename your to-do list")).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_DESC)));
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
