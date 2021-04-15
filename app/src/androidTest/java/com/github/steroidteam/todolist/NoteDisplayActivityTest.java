package com.github.steroidteam.todolist;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class NoteDisplayActivityTest {
    @Rule
    public ActivityScenarioRule<NoteDisplayActivity> activityRule =
            new ActivityScenarioRule<>(NoteDisplayActivity.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Test
    public void saveTextWorks() {
        onView(withId(R.id.activity_notedisplay_save)).perform(click());
    }

}
