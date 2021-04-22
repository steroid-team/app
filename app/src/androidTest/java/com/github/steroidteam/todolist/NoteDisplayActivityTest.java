package com.github.steroidteam.todolist;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
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
        // FIXME : test the correct activity
        /*    Intents.init();

        Espresso.onData(anything())
                .inAdapterView(withId(R.id.activity_noteselection_notelist))
                .atPosition(1)
                .perform(click());

        onView(withId(R.id.note_header)).perform(click());

        Intents.intended(Matchers.allOf(IntentMatchers.hasComponent(MapsActivity.class.getName())));
        Intents.release();
        */
    }
}
