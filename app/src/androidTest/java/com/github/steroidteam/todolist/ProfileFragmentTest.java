package com.github.steroidteam.todolist;

import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.steroidteam.todolist.CustomMatchers.atPositionCheckBox;
import static com.github.steroidteam.todolist.CustomMatchers.atPositionCheckText;
import static com.github.steroidteam.todolist.CustomMatchers.atPositionTextContains;
import static com.github.steroidteam.todolist.CustomMatchers.clickChildViewWithId;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import com.github.steroidteam.todolist.view.ProfileFragment;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ProfileFragmentTest {

    private FragmentScenario<ProfileFragment> scenario;

    @Mock
    FirebaseUser user;

    @Before
    public void init() {

        String name="Jean Paul";
        String email="jeanpaul@epfl.ch";

        doReturn(name).when(user).getDisplayName();
        doReturn(email).when(user).getEmail();

        scenario =
                FragmentScenario.launchInContainer(
                        ProfileFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void oui() {
        onView(withId(R.id.profile_name_text)).check(ViewAssertions.matches(isDisplayed()));
    }
}
