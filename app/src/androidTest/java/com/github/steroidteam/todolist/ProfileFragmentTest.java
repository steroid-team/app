package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import androidx.fragment.app.testing.FragmentScenario;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfileFragmentTest {

    private String name;
    private String email;

    private static ArgumentCaptor<OnCompleteListener> testOnCompleteListener =
            ArgumentCaptor.forClass(OnCompleteListener.class);
    private static ArgumentCaptor<OnSuccessListener> testOnSuccessListener =
            ArgumentCaptor.forClass(OnSuccessListener.class);
    private static ArgumentCaptor<OnFailureListener> testOnFailureListener =
            ArgumentCaptor.forClass(OnFailureListener.class);
    private FragmentScenario<ProfileFragment> scenario;

    @Mock FirebaseUser user;

    @Mock Task<Void> voidTask;

    @Before
    public void init() {
        // setupTask(voidTask);

        UserFactory.set(user);

        name = "Jean Paul";
        email = "jeanpaul@epfl.ch";

        doReturn(name).when(user).getDisplayName();
        doReturn(email).when(user).getEmail();

        doReturn(voidTask).when(user).updateEmail(any());

        scenario =
                FragmentScenario.launchInContainer(
                        ProfileFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void checkInfoIsCorrectlyDisplayed() {
        onView(withId(R.id.profile_name_text)).check(matches(withText(name)));
        onView(withId(R.id.profile_mail_text)).check(matches(withText(email)));
    }
}
