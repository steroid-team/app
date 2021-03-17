package com.github.steroidteam.todolist;

import android.app.Activity;
import static android.app.Instrumentation.*;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.*;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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

//    @Test
//    public void checkUserLogin() {
//        Intents.init();
//
//        Intent resultData = new Intent();
//        ActivityResult result =
//                new ActivityResult(Activity.RESULT_OK, resultData);
//        intending(toPackage(FirebaseUIActivity.class.getName())).respondWith(result);
//
//        onView(withId(R.id.loginStatus)).check(matches(withText("Logged out")));
//
//        Intents.release();
//    }
}
