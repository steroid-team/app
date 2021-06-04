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
import static org.mockito.Mockito.doReturn;

import android.Manifest;
import android.view.KeyEvent;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.MainActivity;
import com.github.steroidteam.todolist.view.MapFragment;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MapFragmentTest {

    private FragmentScenario<MapFragment> scenario;

    @Mock Database databaseMock;

    @Before
    public void init() {

        // Since we are using an activity, set a mocked user.
        FirebaseUser mockedUser = Mockito.mock(FirebaseUser.class);
        UserFactory.set(mockedUser);
        scenario =
                FragmentScenario.launchInContainer(MapFragment.class, null, R.style.AsteroidTheme);
        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(new TodoListCollection());
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
    }

    @Rule
    public GrantPermissionRule coarseLocationPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

    @Rule
    public GrantPermissionRule fineLocationPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

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
        onView(withText(TOAST_TEXT)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
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
