package com.github.steroidteam.todolist;


import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class ItemViewActivityTest {

    @Test
    public void checkBoxInList() {
        Intent itemViewActivity = new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(itemViewActivity)) {
            Espresso.onData(anything()).inAdapterView(withId(R.id.activity_itemview_itemlist)).atPosition(3).perform(click());
        }
    }

}
