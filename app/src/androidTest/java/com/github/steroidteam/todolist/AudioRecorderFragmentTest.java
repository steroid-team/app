package com.github.steroidteam.todolist;

import android.os.Bundle;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import androidx.fragment.app.testing.FragmentScenario;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.AudioRecorderFragment;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RunWith(MockitoJUnitRunner.class)
public class AudioRecorderFragmentTest {

    @Mock
    Database databaseMock;

    @Before
    public void init() {
        Note note = new Note("some title");
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        File audioFile = new File("some audio file");
        CompletableFuture<File> fileFuture = new CompletableFuture<>();
        fileFuture.complete(audioFile);

        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(fileFuture).when(databaseMock).getAudioMemo(any(UUID.class), anyString());

        Bundle bundle = new Bundle();
        bundle.putString(NoteSelectionFragment.NOTE_ID_KEY, UUID.randomUUID().toString());
        FragmentScenario<AudioRecorderFragment> scenario =
                FragmentScenario.launchInContainer(
                        AudioRecorderFragment.class, bundle, R.style.Theme_Asteroid);
    }

    @Test
    public void toastAndTextWhenRecordingShowsCorrectly() {
        onView(withId(R.id.record_button)).perform(click());
        onView(withText(R.string.is_recording))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        onView(withId(R.id.record_text)).check(matches(withText(R.string.stop_record_button)));
        // Wait to avoid error with Toast messages that is still displayed
        waitFor(3000);
    }

    @Test
    public void toastWhenStopRecordingShowsCorrectly() {
        onView(withId(R.id.record_button)).perform(click());
        // Wait to avoid error that the first Toast message is still displayed
        waitFor(3000);
        onView(withId(R.id.record_button)).perform(click());
        onView(withText(R.string.stop_recording))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        onView(withId(R.id.record_text)).check(matches(withText(R.string.record_button)));
    }

    @Test
    public void textWhenPlayShowsCorrectly() {
        onView(withId(R.id.play_text)).check(matches(withText(R.string.play_button)));
        onView(withId(R.id.play_button)).perform(click(), click());
        // Test that after the second click the text displays correctly "Play"
        onView(withId(R.id.play_text)).check(matches(withText(R.string.play_button)));
    }

    @Test
    public void textWhenPauseShowsCorrectly() {
        onView(withId(R.id.play_button)).perform(click());
        onView(withId(R.id.play_text)).check(matches(withText(R.string.pause_button)));
    }

    private void waitFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
