package com.github.steroidteam.todolist;


import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.steroidteam.todolist.view.ItemViewActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ItemViewActivityTest {

    @Rule
    public ActivityScenarioRule<ItemViewActivity> activityRule =
            new ActivityScenarioRule<>(ItemViewActivity.class);

    @Test
    public void createTaskWorks() {
        final String TASK_DESCRIPTION = "Buy bananas";

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text))
                .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn))
                .perform(click());

        // The task description text field should now be empty.
        onView(withId(R.id.new_task_text))
                .check(matches(withText("")));

        // TODO: Check that the ListView actually contains a new item with the tested description.
    }

    @Test
    public void cannotCreateTaskWithoutText() {
        // Clear the text input.
        onView(withId(R.id.new_task_text)).
                perform(clearText());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn))
                .perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(isDisplayed()));
    }

    @Test
    public void updateTaskWorks() {
        Intent itemViewActivity = new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(itemViewActivity)) {

            final String TASK_DESCRIPTION = "Buy bananas";
            final String TASK_DESCRIPTION_2 = "Buy cheese";

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn))
                    .perform(click());

            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

            onView(withId(R.id.new_task_btn))
                    .perform(click());

            // Try to remove the first task
            onView(withId(R.id.activity_itemview_itemlist)).perform(actionOnItemAtPosition(0, longClick()));

            onView(withId(R.id.activity_itemview_itemlist)).perform(actionOnItemAtPosition(0, typeText(" !")));
            onView(withId(R.id.activity_itemview_itemlist)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(
                            0, MyViewAction.clickChildViewWithId(R.id.layout_task_save_modif)));


            onView(withId(R.id.activity_itemview_itemlist)).check(matches(atPositionCheckText(0, TASK_DESCRIPTION + " !")));
            onView(withId(R.id.activity_itemview_itemlist)).check(matches(atPositionCheckText(1, TASK_DESCRIPTION_2)));
        }
    }

    @Test
    public void removeTaskWorks() {
        Intent itemViewActivity = new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(itemViewActivity)) {

            final String TASK_DESCRIPTION = "Buy bananas";
            final String TASK_DESCRIPTION_2 = "Buy cheese";

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn))
                    .perform(click());

            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

            onView(withId(R.id.new_task_btn))
                    .perform(click());

            // Try to remove the first task
            onView(withId(R.id.activity_itemview_itemlist)).perform(actionOnItemAtPosition(0, longClick()));

            onView(withId(R.id.activity_itemview_itemlist)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(
                            0, MyViewAction.clickChildViewWithId(R.id.layout_task_delete_button)));

            //after deleting the first item we check that we have the second one at position 0.
            onView(withId(R.id.activity_itemview_itemlist)).check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));
        }
    }

    @Test
    public void notificationDeleteWorks() {
        Intent itemViewActivity = new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(itemViewActivity)) {
            final String TASK_DESCRIPTION = "Buy bananas";
            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn))
                    .perform(click());

            onView(withId(R.id.activity_itemview_itemlist)).perform(actionOnItemAtPosition(0, longClick()));

            onView(withId(R.id.activity_itemview_itemlist)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(
                            0, MyViewAction.clickChildViewWithId(R.id.layout_task_delete_button)));

            onView(withText("Successfully removed the task !")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        }
    }
    public static Matcher<View> atPositionCheckText(final int position, @NonNull final String expectedText) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("View holder at position " + String.valueOf(position) + ", expected: " + expectedText + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View taskView = view.getChildAt(position);
                TextView bodyView = taskView.findViewById(R.id.layout_task_body);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }

    // Simple ViewAction to click on the button within a item of the recyclerView
    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }

    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }

    }

}
