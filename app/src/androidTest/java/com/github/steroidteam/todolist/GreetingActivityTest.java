package com.github.steroidteam.todolist;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {

    private static final String TEST_NAME = "Alice";

    @Test
    public void nameIsCorrectlyDisplayed() {

        Intent greetingActivity = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        greetingActivity.putExtra(MainActivity.EXTRA_USER_NAME, TEST_NAME);

        try (ActivityScenario<GreetingActivity> scenario = ActivityScenario.launch(greetingActivity)) {
            Espresso.onView(ViewMatchers.withId(R.id.activity_greeting_text)).check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString(TEST_NAME))));
        }
    }
}
