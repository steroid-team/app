package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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

        // Hit the button to create a new task.
        onView(withId(R.id.list_selection_item1)).perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(ItemViewActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void openNotesWorks() {
        Intents.init();

        onView(withId(R.id.notes_button2)).perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteSelectionActivity.class.getName())));
        Intents.release();
    }
}
