package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.DrawingActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingTest {

    @Rule
    public ActivityScenarioRule<DrawingActivity> activityRule =
            new ActivityScenarioRule<>(DrawingActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void drawingWorks() {
        onView(withId(R.id.drawSpace)).perform(ViewActions.swipeRight());
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            scenario.onActivity(
                    activity -> {
                        activity.drawingCanvas.touchMove(0, 0);
                        activity.drawingCanvas.touchMove(0, 1000);
                        activity.drawingCanvas.touchMove(1000, 0);
                        activity.drawingCanvas.touchMove(1000, 1000);
                    });
        }
    }

    @Test
    public void redButtonWorks() {
        onView(withId(R.id.colorRed)).perform(ViewActions.click());
    }

    @Test
    public void greenButtonWorks() {
        onView(withId(R.id.colorGreen)).perform(ViewActions.click());
    }

    @Test
    public void blueButtonWorks() {
        onView(withId(R.id.colorBlue)).perform(ViewActions.click());
    }

    @Test
    public void blackButtonWorks() {
        onView(withId(R.id.colorBlack)).perform(ViewActions.click());
    }
}
