package com.github.steroidteam.todolist;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String TEST_NAME = "Alice";

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void intentIsFiredWhenUserClicksOnButton() {

        //Note: You have to launch the emulator before running the test.

        Intents.init();

        Espresso.onView(ViewMatchers.withId(R.id.activity_main_user_input)).perform(clearText(), typeText(TEST_NAME));
        closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.activity_main_enter_button)).perform(click());

        Intents.intended(Matchers.allOf(IntentMatchers.hasComponent(GreetingActivity.class.getName()), IntentMatchers.hasExtra(MainActivity.EXTRA_USER_NAME, TEST_NAME)));

        Intents.release();
    }

}