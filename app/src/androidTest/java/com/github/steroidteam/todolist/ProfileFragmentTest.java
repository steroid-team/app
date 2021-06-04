package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import androidx.fragment.app.testing.FragmentScenario;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.ProfileFragment;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfileFragmentTest {

    private String name;
    private String email;

    private FragmentScenario<ProfileFragment> scenario;

    @Mock FirebaseUser user;

    @Before
    public void init() {

        UserFactory.set(user);

        name = "Jean Paul";
        email = "jeanpaul@epfl.ch";

        doReturn(name).when(user).getDisplayName();
        doReturn(email).when(user).getEmail();

        doReturn(Tasks.forResult(null)).when(user).updateEmail(any());
        doReturn(Tasks.forResult(null)).when(user).updateProfile(any());
        doReturn(Tasks.forResult(null)).when(user).updatePassword(any());
        doReturn(Tasks.forResult(null)).when(user).reauthenticate(any());

        scenario =
                FragmentScenario.launchInContainer(
                        ProfileFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void checkInfoIsCorrectlyDisplayed() {
        onView(withId(R.id.profile_name_text)).check(matches(withText(name)));
        onView(withId(R.id.profile_mail_text)).check(matches(withText(email)));
    }

    @Test
    public void displayWorks() {
        onView(withId(R.id.profile_name_edit_btn)).perform(click());
        onView(withId(R.id.profile_name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_name_edit_save)).perform(click());
        onView(withId(R.id.profile_name_edit_text)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkUpdateName() {

        String newName = "Pierre Paul";

        onView(withId(R.id.profile_name_edit_btn)).perform(click());
        onView(withId(R.id.profile_name_edit_text)).perform(clearText(), typeText(newName));

        closeSoftKeyboard();
        doReturn(newName).when(user).getDisplayName();
        onView(withId(R.id.profile_name_edit_save)).perform(click());

        onView(withId(R.id.profile_name_text)).check(matches(withText(newName)));
    }

    @Test
    public void checkUpdateEmail() {

        String newMail = "pierrepaul@epfl.ch";

        onView(withId(R.id.profile_mail_edit_btn)).perform(click());
        onView(withId(R.id.alert_dialog_email)).perform(clearText(), typeText(email));
        onView(withId(R.id.alert_dialog_password)).perform(clearText(), typeText("PASSWORD"));
        closeSoftKeyboard();

        doReturn(newMail).when(user).getEmail();
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.profile_mail_edit_text)).perform(clearText(), typeText(newMail));
        onView(withId(R.id.profile_mail_edit_save)).perform(click());

        onView(withId(R.id.profile_mail_text)).check(matches(withText(newMail)));
    }

    @Test
    public void checkUpdatePwd() {
        String newPass = "123";
        onView(withId(R.id.profile_pwd_edit_btn)).perform(click());
        onView(withId(R.id.alert_dialog_email)).perform(clearText(), typeText(email));
        onView(withId(R.id.alert_dialog_password)).perform(clearText(), typeText("PASSWORD"));
        closeSoftKeyboard();

        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.profile_pwd_edit_text)).perform(clearText(), typeText(newPass));
        onView(withId(R.id.profile_pwd_edit_save)).perform(click());

        verify(user).updatePassword(newPass);
    }
}
