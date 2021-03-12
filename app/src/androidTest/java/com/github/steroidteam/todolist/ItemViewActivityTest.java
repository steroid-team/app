package com.github.steroidteam.todolist;


import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class ItemViewActivityTest {

    @Rule
    public ActivityScenarioRule<ItemViewActivity> activityRule =
            new ActivityScenarioRule<>(ItemViewActivity.class);

    @Test
    public void checkBoxInList() {
        Intent itemViewActivity = new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(itemViewActivity)) {
            Espresso.onData(anything()).inAdapterView(withId(R.id.activity_itemview_itemlist)).atPosition(3).perform(click());
        }
    }

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

}
