package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.steroidteam.todolist.view.MapsActivity.KEY_LOCATION;
import static com.github.steroidteam.todolist.view.MapsActivity.KEY_NAME_LOCATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.MapsActivity;
import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteDisplayActivityTest {

    Intent intent;
    ActivityScenario<NoteDisplayActivity> scenario;

    @Mock Database databaseMock;

    @Before
    public void stubImagePickerIntent() {
        Intents.init();

        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(new Note("Some random title"));
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        DatabaseFactory.setCustomDatabase(databaseMock);

        intent = new Intent(ApplicationProvider.getApplicationContext(), NoteDisplayActivity.class);
        intent.putExtra(NoteSelectionActivity.EXTRA_NOTE_ID, UUID.randomUUID().toString());

        scenario = ActivityScenario.launch(intent);
        scenario.onActivity(this::savePickedImage);
    }

    @After
    public void after() {
        scenario.close();
        Intents.release();
    }

    @Test
    public void openMapsActivityWorks() {
        onView(withId(R.id.location_button)).perform(click());

        Intents.intended(Matchers.allOf(IntentMatchers.hasComponent(MapsActivity.class.getName())));
    }

    @Test
    public void locationNameIsCorrectlyUpdated() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(KEY_LOCATION, new LatLng(-33.8523341, 151.2106085));
        returnIntent.putExtra(KEY_NAME_LOCATION, "Sydney");

        intending(hasComponent(MapsActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent));

        onView(withId(R.id.location_button)).perform(click());

        onView(withId(R.id.note_location)).check(matches(withText("Sydney")));
    }

    @Test
    public void filePickingWorks() {
        onView(withId(R.id.camera_button)).perform(click());
    }

    private void savePickedImage(Activity activity) {
        Bitmap bm =
                BitmapFactory.decodeResource(
                        activity.getResources(), R.drawable.note_header_test_image);
        File dir = activity.getExternalCacheDir();
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
