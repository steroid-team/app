package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.endsWith;

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

        onView(withId(R.id.notes_button2)).perform(click());

        intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteSelectionActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void cannotRenameTodoListWithoutText() {
        Intent listSelectionActivity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario =
                ActivityScenario.launch(listSelectionActivity)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(longClick());

            // User input
            onView(withText("Please enter a new name")).check(matches(isDisplayed()));

            onView(withClassName(endsWith("EditText"))).perform(clearText());

            onView(withText("Confirm")).inRoot(isDialog()).perform(click());

            // make sure dialog is gone
            onView(withText("Please enter a new name")).check(doesNotExist());
        }
    }

    @Test
    public void renameTodoListWorks() {
        final String TODO_LIST_NAME = "Sweng Test";

        Intent listSelectionActivity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario =
                ActivityScenario.launch(listSelectionActivity)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(longClick());

            onView(withText("Please enter a new name")).check(matches(isDisplayed()));

            onView(withClassName(endsWith("EditText")))
                    .perform(replaceText(TODO_LIST_NAME), closeSoftKeyboard());

            onView(withText("Confirm")).inRoot(isDialog()).perform(click());
        }
    }
}
