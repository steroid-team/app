package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.ItemViewActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListSelectionActivityTest {

    @Rule
    public ActivityScenarioRule<ListSelectionActivity> activityRule =
            new ActivityScenarioRule<>(ListSelectionActivity.class);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void closeIntents() {
        Intents.release();
    }

    @Test
    public void openListWorks() {
        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, click()));

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(ItemViewActivity.class.getName())));
    }

    @Test
    public void openNotesWorks() {

        onView(withId(R.id.notes_button)).perform(click());

        intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteSelectionActivity.class.getName())));
    }
}
