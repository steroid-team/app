package com.github.steroidteam.todolist;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.github.steroidteam.todolist.view.DrawingView.BACKGROUND_COLOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.DrawingActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingTest {

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
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            scenario.onActivity(
                    activity -> {
                        activity.drawingCanvas.touchStart(100, 100);
                        activity.drawingCanvas.touchMove(100, 200);
                        assertEquals(
                                Color.BLACK,
                                activity.drawingCanvas.getBitmap().getColor(100, 150).toArgb());
                    });

            onView(withId(R.id.drawSpace)).perform(ViewActions.swipeRight());
            scenario.onActivity(
                    activity -> {
                        assertEquals(
                                Color.BLACK,
                                activity.drawingCanvas.getBitmap().getColor(200, 392).toArgb());
                    });
        }
    }

    @Test
    public void redButtonWorks() {
        Intent redActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario = ActivityScenario.launch(redActivity)) {
            onView(withId(R.id.colorRed)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(Color.RED, activity.drawingCanvas.getPaint().getColor());
                    });
        }
    }

    @Test
    public void greenButtonWorks() {
        Intent greenActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario = ActivityScenario.launch(greenActivity)) {
            onView(withId(R.id.colorGreen)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(Color.GREEN, activity.drawingCanvas.getPaint().getColor());
                    });
        }
    }

    @Test
    public void blueButtonWorks() {
        Intent blueActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario = ActivityScenario.launch(blueActivity)) {
            onView(withId(R.id.colorBlue)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(Color.BLUE, activity.drawingCanvas.getPaint().getColor());
                    });
        }
    }

    @Test
    public void blackButtonWorks() {
        Intent blackActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario = ActivityScenario.launch(blackActivity)) {
            onView(withId(R.id.colorBlack)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(Color.BLACK, activity.drawingCanvas.getPaint().getColor());
                    });
        }
    }

    @Test
    public void erasesButtonWorks() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            scenario.onActivity(
                    activity -> {
                        activity.drawingCanvas.touchStart(100, 100);
                        activity.drawingCanvas.touchMove(200, 100);
                    });
            onView(withId(R.id.erase_button)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(
                                BACKGROUND_COLOR,
                                activity.drawingCanvas.getBitmap().getColor(150, 100).toArgb());
                    });
        }
    }

    @Test
    public void colorPickerButtonWorks() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            onView(withId(R.id.colorChoose)).perform(click());
            onView(withId(R.id.colorPickerWindow)).check(matches(isDisplayed()));
            scenario.onActivity(
                    activity -> {
                        assertEquals(activity.drawingCanvas.getVisibility(), GONE);
                    });
        }
    }

    @Test
    public void backButtonWorks() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            onView(withId(R.id.backButton)).perform(click());
            Intents.intended(
                    Matchers.allOf(
                            IntentMatchers.hasComponent(ListSelectionActivity.class.getName())));
        }
    }

    @Test
    public void smallMovesDoNotDraw() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            scenario.onActivity(
                    activity -> {
                        activity.drawingCanvas.touchStart(100, 100);
                        activity.drawingCanvas.touchMove(100, 102);
                        assertEquals(
                                BACKGROUND_COLOR,
                                activity.drawingCanvas.getBitmap().getColor(100, 101).toArgb());
                    });
        }
    }

    @Test
    public void InvalidMoveDoesNotDraw() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            scenario.onActivity(
                    activity -> {
                        MotionEvent invalidEvent =
                                MotionEvent.obtain(2, 2, MotionEvent.ACTION_SCROLL, 100, 100, 5);
                        invalidEvent.setAction(MotionEvent.ACTION_SCROLL);
                        Bitmap bitmap = activity.drawingCanvas.getBitmap();
                        Bitmap before = bitmap.copy(bitmap.getConfig(), false);
                        activity.drawingCanvas.onTouchEvent(invalidEvent);
                        assertTrue(activity.drawingCanvas.getBitmap().sameAs(before));
                    });
        }
    }

    @Test
    public void ApplyColorWorks() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            onView(withId(R.id.colorChoose)).perform(click());
            onView(withId(R.id.applyColor)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertNotEquals(Color.BLACK, activity.drawingCanvas.getPaint().getColor());
                        assertEquals(activity.drawingCanvas.getVisibility(), VISIBLE);
                    });
        }
    }

    @Test
    public void CancelColorWorks() {
        Intent drawingActivity =
                new Intent(ApplicationProvider.getApplicationContext(), DrawingActivity.class);

        try (ActivityScenario<DrawingActivity> scenario =
                ActivityScenario.launch(drawingActivity)) {
            onView(withId(R.id.colorChoose)).perform(click());
            onView(withId(R.id.cancelColor)).perform(click());
            scenario.onActivity(
                    activity -> {
                        assertEquals(Color.BLACK, activity.drawingCanvas.getPaint().getColor());
                        assertEquals(activity.drawingCanvas.getVisibility(), VISIBLE);
                    });
        }
    }
}
