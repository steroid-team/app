package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoteSelectionActivityTest {

    @Rule
    public ActivityScenarioRule<NoteSelectionActivity> activityRule =
            new ActivityScenarioRule<>(NoteSelectionActivity.class);

    @Test
    public void openListWorks() {
        Intents.init();

        Espresso.onData(anything())
                .inAdapterView(withId(R.id.activity_noteselection_notelist))
                .atPosition(1)
                .perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteDisplayActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void openTodoListWorks() {
        Intents.init();

        onView(withId(R.id.activity_noteselection_button)).perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(ListSelectionActivity.class.getName())));
        Intents.release();
    }
}
