package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
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
        Intent listSelectionActivity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario =
                ActivityScenario.launch(listSelectionActivity)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(click());
            intended(hasComponent(ItemViewActivity.class.getName()));
        }
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
}
