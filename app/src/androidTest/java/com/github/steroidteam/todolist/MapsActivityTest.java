package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;

import android.view.KeyEvent;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.github.steroidteam.todolist.view.MapsActivity;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {

    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule =
            new ActivityScenarioRule<>(MapsActivity.class);

    @Test
    public void markerIsCorrectlyPlacedAtDefaultLocationOrUserLocation() {
        waitFor(2000);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If we don't have access to the localisation the default marker is placed at Sydney
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Sydney"));
        try {
            marker.click();
            Assert.assertNotNull(marker);
        } catch (UiObjectNotFoundException e) {
            // It's the case when we have access to the localisation
            marker = device.findObject(new UiSelector().descriptionContains("I'm here"));
            try {
                marker.click();
                Assert.assertNotNull(marker);
            } catch (UiObjectNotFoundException uiObjectNotFoundException) {
                uiObjectNotFoundException.printStackTrace();
            }
        }
    }

    @Test
    public void searchViewWorksCorrectlyWithCorrectLocation() {
        String TEST_LOCATION = "Lausanne";
        onView(withId(R.id.sv_location))
                .perform(click(), typeText(TEST_LOCATION), pressKey(KeyEvent.KEYCODE_ENTER));
        waitFor(4000);
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains(TEST_LOCATION));
        Assert.assertNotNull(marker);
    }

    @Test
    public void searchViewToastMessageWithIncorrectLocation() {
        String TEST_WRONG_LOCATION = "ojoiwejfew";
        String TOAST_TEXT = "Location not found !";

        onView(withId(R.id.sv_location))
                .perform(click(), typeText(TEST_WRONG_LOCATION), pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withText(TOAST_TEXT))
                .inRoot(new ItemViewActivityTest.ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSaveButtonCorrectlyFinishActivity() {
        onView(withId(R.id.map_save_location)).perform(click());
        assertEquals(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    private void waitFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
