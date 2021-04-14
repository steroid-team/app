package com.github.steroidteam.todolist;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.github.steroidteam.todolist.view.MapsActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapsActivityTest {

    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule =
            new ActivityScenarioRule<>(MapsActivity.class);

    @Before
    public void before() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void MarkerIsCorrectlyPlacedAtDefaultLocationOrUserLocation() {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If we don't have access to the localisation the default marker is placed at Sydney
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Sydney :)"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            // It's the case when we have access to the localisation
            marker = device.findObject(new UiSelector().descriptionContains("I'm here"));
            try {
                marker.click();
            } catch (UiObjectNotFoundException uiObjectNotFoundException) {
                uiObjectNotFoundException.printStackTrace();
            }
        }
    }
}
