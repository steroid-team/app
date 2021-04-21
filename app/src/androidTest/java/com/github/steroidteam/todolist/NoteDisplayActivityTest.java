package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.MapsActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import com.google.android.gms.maps.model.LatLng;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoteDisplayActivityTest {
    @Rule
    public ActivityScenarioRule<NoteSelectionActivity> activityRule =
            new ActivityScenarioRule<>(NoteSelectionActivity.class);

    @Test
    public void openMapsActivityWorks() {
        Intents.init();

        Espresso.onData(anything())
                .inAdapterView(withId(R.id.activity_noteselection_notelist))
                .atPosition(1)
                .perform(click());

        onView(withId(R.id.location_button)).perform(click());

        Intents.intended(Matchers.allOf(hasComponent(MapsActivity.class.getName())));
        Intents.release();
    }

    @Test
    public void locationNameIsCorrectlyUpdated() {
        Intents.init();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("location", new LatLng(-33.8523341, 151.2106085));
        returnIntent.putExtra("nameLocation", "Sydney");

        intending(hasComponent(MapsActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent));

        Espresso.onData(anything())
                .inAdapterView(withId(R.id.activity_noteselection_notelist))
                .atPosition(1)
                .perform(click());

        onView(withId(R.id.location_button)).perform(click());

        onView(withId(R.id.note_location)).check(matches(withText("Sydney")));
        Intents.release();
    }
}
