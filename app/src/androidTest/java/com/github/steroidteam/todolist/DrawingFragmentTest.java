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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.DrawingFragment;
import com.github.steroidteam.todolist.view.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawingFragmentTest {
    private FragmentScenario<DrawingFragment> scenario;

    @Before
    public void init() {
        scenario =
                FragmentScenario.launchInContainer(
                        DrawingFragment.class, null, R.style.Theme_Asteroid);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void drawingWorks() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(5, 5);
                    fragment.drawingCanvas.touchMove(5, 15);
                    assertEquals(
                            Color.BLACK,
                            fragment.drawingCanvas.getBitmap().getColor(5, 10).toArgb());
                    fragment.drawingCanvas.erase();
                });

        onView(withId(R.id.drawSpace)).perform(ViewActions.swipeRight());

        scenario.onFragment(
                fragment -> {
                    boolean draw = false;
                    Bitmap bitmap = fragment.drawingCanvas.getBitmap();
                    for (int x = 0; x < bitmap.getWidth() && !draw; x++) {
                        for (int y = 0; y < bitmap.getHeight() && !draw; y++) {
                            if (bitmap.getPixel(x, y) == Color.BLACK) {
                                draw = true;
                            }
                        }
                    }
                    assertTrue(draw);
                });
    }

    @Test
    public void redButtonWorks() {
        onView(withId(R.id.colorRed)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.RED, fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void greenButtonWorks() {
        onView(withId(R.id.colorGreen)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.GREEN, fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void blueButtonWorks() {
        onView(withId(R.id.colorBlue)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.BLUE, fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void blackButtonWorks() {
        onView(withId(R.id.colorBlack)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.BLACK, fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void erasesButtonWorks() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(100, 100);
                    fragment.drawingCanvas.touchMove(200, 100);
                });
        onView(withId(R.id.erase_button)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            BACKGROUND_COLOR,
                            fragment.drawingCanvas.getBitmap().getColor(150, 100).toArgb());
                });
    }

    @Test
    public void colorPickerButtonWorks() {
        onView(withId(R.id.colorChoose)).perform(click());
        onView(withId(R.id.colorPickerWindow)).check(matches(isDisplayed()));
        scenario.onFragment(
                fragment -> {
                    assertEquals(fragment.drawingCanvas.getVisibility(), GONE);
                });
    }

    @Test
    public void backButtonWorks() {
        onView(withId(R.id.backButton)).perform(click());
        assertEquals(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    @Test
    public void smallMovesDoNotDraw() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(100, 100);
                    fragment.drawingCanvas.touchMove(100, 102);
                    assertEquals(
                            BACKGROUND_COLOR,
                            fragment.drawingCanvas.getBitmap().getColor(100, 101).toArgb());
                });
    }

    @Test
    public void InvalidMoveDoesNotDraw() {
        scenario.onFragment(
                fragment -> {
                    MotionEvent invalidEvent =
                            MotionEvent.obtain(2, 2, MotionEvent.ACTION_SCROLL, 100, 100, 5);
                    invalidEvent.setAction(MotionEvent.ACTION_SCROLL);
                    Bitmap bitmap = fragment.drawingCanvas.getBitmap();
                    Bitmap before = bitmap.copy(bitmap.getConfig(), false);
                    fragment.drawingCanvas.onTouchEvent(invalidEvent);
                    assertTrue(fragment.drawingCanvas.getBitmap().sameAs(before));
                });
    }

    @Test
    public void ApplyColorWorks() {
        onView(withId(R.id.colorChoose)).perform(click());
        onView(withId(R.id.applyColor)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertNotEquals(Color.BLACK, fragment.drawingCanvas.getPaint().getColor());
                    assertEquals(fragment.drawingCanvas.getVisibility(), VISIBLE);
                });
    }

    @Test
    public void CancelColorWorks() {
        onView(withId(R.id.colorChoose)).perform(click());
        onView(withId(R.id.cancelColor)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.BLACK, fragment.drawingCanvas.getPaint().getColor());
                    assertEquals(fragment.drawingCanvas.getVisibility(), VISIBLE);
                });
    }
}