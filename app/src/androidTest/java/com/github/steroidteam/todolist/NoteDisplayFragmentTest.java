package com.github.steroidteam.todolist;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.NoteDisplayFragment;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class NoteDisplayFragmentTest {
    private FragmentScenario<NoteDisplayFragment> scenario;
    private static String FIXTURE_DEFAULT_NOTE_TITLE = "My note";
    private static String FIXTURE_DEFAULT_NOTE_CONTENT = "Some content";

    @Mock Database databaseMock;

    @Before
    public void init() {
        Note note = new Note(FIXTURE_DEFAULT_NOTE_TITLE);
        note.setContent(FIXTURE_DEFAULT_NOTE_CONTENT);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabase(databaseMock);

        Bundle bundle = new Bundle();
        bundle.putString(NoteSelectionFragment.NOTE_ID_KEY, UUID.randomUUID().toString());
        scenario =
                FragmentScenario.launchInContainer(
                        NoteDisplayFragment.class, bundle, R.style.Theme_Asteroid);
    }

    @Test
    public void openMapsViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        onView(withId(R.id.location_button)).perform(click());

        // Check that we are now in the note display view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_map));
    }

    @Test
    public void noteIsRenderedProperly() {
        onView(withId(R.id.note_title)).check(matches(withText(FIXTURE_DEFAULT_NOTE_TITLE)));
        onView(withId(R.id.activity_notedisplay_edittext))
                .check(matches(withText(FIXTURE_DEFAULT_NOTE_CONTENT)));
    }
}
