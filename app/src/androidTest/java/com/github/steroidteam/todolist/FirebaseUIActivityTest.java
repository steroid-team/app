package com.github.steroidteam.todolist;

import static android.app.Instrumentation.*;

import android.app.Activity;
import android.content.Intent;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;

import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;


import static androidx.test.espresso.intent.Intents.*;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.KickoffActivity;

import com.github.steroidteam.todolist.ui.FirebaseUIActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FirebaseUIActivityTest {

    @Rule
    public ActivityScenarioRule<FirebaseUIActivity> activityRule =
            new ActivityScenarioRule<FirebaseUIActivity>(FirebaseUIActivity.class);

    @Test
    public void checkUserLoggedOut() {
        onView(withId(R.id.loginStatus)).check(matches(withText("Logged out")));
    }

    @Test
    public void checkUserPushesLoginButton() {
        onView(withId(R.id.button2)).perform(click());
    }

    @Test
    public void testUserLoggedIn() {
        Intents.init();

        ActivityResult result = new ActivityResult(Activity.RESULT_OK, null);
        intending(toPackage(FirebaseUIActivity.class.getName())).respondWith(result);

        Intents.release();
    }

    @Test
    public void testUserLoginFailed() {
        Intents.init();

        ActivityResult result = new ActivityResult(Activity.RESULT_CANCELED, null);
        intending(toPackage(FirebaseUIActivity.class.getName())).respondWith(result);

        Intents.release();
    }
}
