package com.github.steroidteam.todolist;

import android.content.Intent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RunWith(MockitoJUnitRunner.class)
public class NoteSelectionActivityTest {

    @Rule
    public ActivityScenarioRule<NoteSelectionActivity> activityRule =
            new ActivityScenarioRule<>(new Intent(
                    ApplicationProvider.getApplicationContext(), NoteSelectionActivity.class));

    @Mock
    Database databaseMock;

    @Before
    public void before() {
        Intents.init();

        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        Note note = new Note("Some random title");
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));

        ActivityScenario<NoteSelectionActivity> scenario = activityRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> activity.setDatabase(databaseMock));
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @After
    public void after() {
        Intents.release();
        ActivityScenario<NoteSelectionActivity> scenario = activityRule.getScenario();
        scenario.close();
    }

    @Test
    public void openListWorks() {
        Espresso.onData(anything())
                .inAdapterView(withId(R.id.activity_noteselection_notelist))
                .atPosition(1)
                .perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(NoteDisplayActivity.class.getName())));
    }

    @Test
    public void openTodoListWorks() {
        onView(withId(R.id.activity_noteselection_button)).perform(click());

        Intents.intended(
                Matchers.allOf(IntentMatchers.hasComponent(ListSelectionActivity.class.getName())));
    }
}
