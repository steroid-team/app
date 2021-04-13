package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListSelectionLogicTest {

    @Test
    public void todoRepoInitWithOneTodo() {

        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, "A Todo!")));
        }
    }

    @Test
    public void cancelCreateTodoWorks() {

        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            onView(withId(R.id.create_todo_button)).perform(click());

            onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

            // button2 = negative button
            onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

            onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, "A Todo!")));
        }
    }

    @Test
    public void renameTodoWorks() {

        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            final String TODO_DESC_2 = "Homework";

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeRight()));

            onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

            onView(isRoot()).perform(waitAtLeastHelper(500));

            onView(withId(R.id.alert_dialog_edit_text)).perform(typeText(TODO_DESC_2));

            onView(isRoot()).perform(waitAtLeastHelper(500));

            // button1 = positive button
            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, TODO_DESC_2)));
        }
    }

    @Test
    public void createTodoWorks() {

        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            final String TODO_DESC = "A Todo 2!";

            onView(withId(R.id.create_todo_button)).perform(click());

            onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

            onView(withId(R.id.alert_dialog_edit_text))
                    .inRoot(isDialog())
                    .perform(typeText(TODO_DESC));
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

            onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(1));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(1, TODO_DESC)));
        }
    }

    @Test
    public void deleteTodoWorks() {
        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {
            final String TODO_DESC_2 = "Homework";

            // Add a to-do
            onView(withId(R.id.create_todo_button)).perform(click());

            onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

            onView(withId(R.id.alert_dialog_edit_text))
                    .inRoot(isDialog())
                    .perform(clearText(), typeText(TODO_DESC_2));
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

            onView(isRoot()).perform(waitAtLeastHelper(500));

            onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeLeft()));

            onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
            onView(isRoot()).perform(waitAtLeastHelper(500));

            onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, TODO_DESC_2)));
        }
    }

    @Test
    public void cancelDeletionWorks() {
        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {
            final String TODO_DESC = "A Todo!";

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeLeft()));

            onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

            onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

            onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, TODO_DESC)));
        }
    }

    @Test
    public void cancelRenamingWorks() {
        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {
            final String TODO_DESC = "A Todo!";
            final String TODO_DESC_2 = "Homework";

            onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeRight()));

            onView(isRoot()).perform(waitAtLeastHelper(500));

            onView(withText(R.string.rename_todo_suggestion)).check(matches(isDisplayed()));

            onView(withId(R.id.alert_dialog_edit_text)).perform(clearText(), typeText(TODO_DESC_2));

            onView(isRoot()).perform(waitAtLeastHelper(500));

            onView(withId(android.R.id.button2)).perform(click());

            onView(withText(R.string.rename_todo_suggestion)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, TODO_DESC)));
        }
    }

    @Test
    public void cantRenameTodoWithoutText() {
        Intent activity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(activity)) {

            onView(withId(R.id.activity_list_selection_itemlist))
                    .perform(actionOnItemAtPosition(0, swipeRight()));

            onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

            onView(withId(R.id.alert_dialog_edit_text)).perform(typeText(""));

            onView(withId(android.R.id.button1)).perform(click());

            onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

            onView(withId(R.id.activity_list_selection_itemlist))
                    .check(matches(atPositionCheckText(0, "A Todo!")));
        }
    }

    /**
     * Helper to check if the given text is the same as the to-do list title at the given position
     *
     * @param position the index of the to-do in the recycler view
     * @param expectedText the text that should be the title of the to-do
     * @return Mactcher to test the title
     */
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

    /**
     * Helper to test dialog
     *
     * @param millis time to wait
     * @return viewAction that wait the given time
     */
    public static ViewAction waitAtLeastHelper(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return anyView();
            }

            @NonNull
            private Matcher<View> anyView() {
                return new IsAnything<>();
            }

            @Override
            public String getDescription() {
                return "wait for at least " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
