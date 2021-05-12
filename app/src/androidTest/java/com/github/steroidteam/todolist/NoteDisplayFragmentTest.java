package com.github.steroidteam.todolist;

import static androidx.core.os.BundleKt.bundleOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.view.NoteDisplayFragment;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import kotlin.Pair;

@RunWith(MockitoJUnitRunner.class)
public class NoteDisplayFragmentTest {
    private FragmentScenario<NoteDisplayFragment> scenario;

    @Mock Database databaseMock;

    @Before
    public void init() {
        Note note = new Note("My note");
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
    public void filePickingWorks() {
        scenario.recreate();
        scenario.onFragment(
                fragment -> {
                    savePickedImage(fragment);
                    Uri result = setStubResult(fragment);
                    ActivityResultRegistry testRegistry = new ActivityResultRegistry() {
                        @Override
                        public <I, O> void onLaunch(int requestCode, @NonNull ActivityResultContract<I, O> contract, I input, @Nullable ActivityOptionsCompat options) {
                            dispatchResult(requestCode, result);
                        }
                    };
                    Bundle bundle = new Bundle();
                    bundle = bundleOf(new Pair<>("registry", testRegistry));
                    bundle.putString(NoteSelectionFragment.NOTE_ID_KEY, UUID.randomUUID().toString());
                    FragmentScenario<NoteDisplayFragment> resultScenario = FragmentScenario.launchInContainer(NoteDisplayFragment.class, bundle, R.style.Theme_Asteroid);
                    resultScenario.onFragment(newFrag -> {
                        onView(withId(R.id.camera_button)).perform(click());
                    });
                });
    }

    private void savePickedImage(Fragment fragment) {
        Bitmap bm =
                BitmapFactory.decodeResource(
                        fragment.getResources(), R.drawable.note_header_test_image);
        File dir = fragment.getActivity().getExternalCacheDir();
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

    private Uri setStubResult(Fragment fragment) {
        File dir = fragment.getActivity().getExternalCacheDir();
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        return Uri.fromFile(file);
    }
}
