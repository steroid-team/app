package com.github.steroidteam.todolist;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static androidx.test.espresso.web.sugar.Web.onWebView;
import static androidx.test.espresso.web.webdriver.DriverAtoms.clearElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.findElement;
import static androidx.test.espresso.web.webdriver.DriverAtoms.getText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.web.webdriver.Locator;
import androidx.test.platform.app.InstrumentationRegistry;
import com.github.steroidteam.todolist.customviewactions.RichEditorGetHtml;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.util.Utils;
import com.github.steroidteam.todolist.view.NoteDisplayFragment;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteDisplayFragmentTest {
    private FragmentScenario<NoteDisplayFragment> scenario;
    private final String FIXTURE_DEFAULT_NOTE_TITLE = "My note";
    private final String FIXTURE_DEFAULT_NOTE_CONTENT =
            "Lorem ipsum:"
                    + "<br>"
                    + "<ul>"
                    + "<li>dolor</li>"
                    + "<li>sit</li>"
                    + "</ul>"
                    + "<div>amet, <b>consectetur</b> adipiscing <strike>elyt</strike> elit.</div>"
                    + "<div><ol>"
                    + "<li><i>Duis</i></li>"
                    + "<li>eu</li>"
                    + "<li>velit</li>"
                    + "</ol>"
                    + "<div>porttitor, <u>varius quam quis</u>, suscipit erat.</div>"
                    + "</div>";

    @Mock Database databaseMock;

    @Mock Context context;

    @Before
    public void init() throws FileNotFoundException {

        // Mock context for Broadcast Reminder
        doReturn(context).when(context).getApplicationContext();

        Note note = new Note(FIXTURE_DEFAULT_NOTE_TITLE);
        note.setContent(FIXTURE_DEFAULT_NOTE_CONTENT);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        List<UUID> notes = Collections.singletonList(UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        CompletableFuture<Void> voidFuture = new CompletableFuture<>();
        voidFuture.complete(null);

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any());
        doReturn(noteFuture).when(databaseMock).updateNote(any(), any());
        doReturn(voidFuture)
                .when(databaseMock)
                .setHeaderNote(any(UUID.class), anyString(), any(UUID.class));

        DatabaseFactory.setCustomDatabase(databaseMock);

        File fakeFile = new File("Fake pathname");
        doReturn(fakeFile).when(context).getCacheDir();
        ViewModelFactoryInjection.setCustomNoteRepo(context, UUID.randomUUID());

        scenario =
                FragmentScenario.launchInContainer(
                        NoteDisplayFragment.class, null, R.style.AsteroidTheme);
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

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.location_button)).perform(click());

        // Check that we are now in the map view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_map));
    }

    @Test
    public void openDrawingViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.editor_action_drawing_btn)).perform(scrollTo(), click());

        // Check that we are now in the drawing view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_drawing));
    }

    @Test
    public void openAudioViewWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        // Scroll to the button in the toolbar (as it can be hidden if the screen is too narrow),
        // and click it.
        onView(withId(R.id.audio_button)).perform(click());

        // Check that we are now in the map view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_audio));
    }

    @Test
    public void noteIsRenderedProperly() {
        onView(withId(R.id.note_title)).check(matches(withText(FIXTURE_DEFAULT_NOTE_TITLE)));
        RichEditorGetHtml getHtml = new RichEditorGetHtml();
        onView(withId(R.id.notedisplay_text_editor)).perform(getHtml);
        MatcherAssert.assertThat(getHtml.contents, equalTo(FIXTURE_DEFAULT_NOTE_CONTENT));
    }

    @Test
    public void saveNoteWorks() {
        final String FIXTURE_MODIFIED_NOTE_CONTENT = "Some text";

        // Clear the text field.
        onWebView().withElement(findElement(Locator.ID, "editor")).perform(clearElement());

        // Tap the text field so it has the keyboard's focus, type the new contents of the note
        // and close the keyboard.
        onView(withId(R.id.notedisplay_text_editor))
                .perform(click())
                .perform(typeText(FIXTURE_MODIFIED_NOTE_CONTENT))
                .perform(closeSoftKeyboard());

        // Make sure that the note now contains the new body.
        onWebView()
                .withElement(findElement(Locator.ID, "editor"))
                .check(webMatches(getText(), containsString(FIXTURE_MODIFIED_NOTE_CONTENT)));

        // Makes the rich editor lost focus
        onView(withId(R.id.note_header)).perform(click());

        // Make sure that the note is updated in the database.
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(databaseMock, times(2)).updateNote(any(), captor.capture());
        Note updatedNote = captor.getValue();
        assertThat(updatedNote.getTitle(), equalTo(FIXTURE_DEFAULT_NOTE_TITLE));
        assertThat(updatedNote.getContent(), equalTo(FIXTURE_MODIFIED_NOTE_CONTENT));
    }

    @Test
    public void addImageDialogWorks() {
        onView(withId(R.id.camera_button)).perform(click());
        onView(withText("How do you want to add an image ?")).check(matches(isDisplayed()));
        onView(withText("Take a photo")).perform(click());
    }

    @Test
    public void updateImageCaptureHeaderWorks() throws FileNotFoundException {
        Intents.init();
        Intents.intending(IntentMatchers.hasAction(ACTION_IMAGE_CAPTURE))
                .respondWith(new Instrumentation.ActivityResult(RESULT_OK, new Intent()));

        onView(withId(R.id.camera_button)).perform(click());
        onView(withText("Take a photo")).perform(click());
        intended(IntentMatchers.hasAction(ACTION_IMAGE_CAPTURE));

        onView(withId(R.id.note_header))
                .check(
                        matches(
                                new BoundedMatcher<View, ConstraintLayout>(ConstraintLayout.class) {

                                    @Override
                                    public void describeTo(Description description) {
                                        description.appendText("with background : ");
                                    }

                                    @Override
                                    protected boolean matchesSafely(ConstraintLayout item) {
                                        return (Integer) item.getTag()
                                                != R.drawable.rounded_corner_just_bottom_bg;
                                    }
                                }));
        Intents.release();
    }

    /*
     * Ce test est maudit. Et les dieux google et SO nous ont abandonnés :/
     *
    @Test
    public void updateImageFilePickerHeaderWorks() {
        Intents.init();

        Uri uri = Uri.parse("android.resource://"+InstrumentationRegistry.getInstrumentation().getContext().getPackageName()+"/"+R.drawable.asteroid_banner);
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> parcels = new ArrayList<>();
        Intent resultData = new Intent();
        parcels.add(uri);
        bundle.putParcelableArrayList(ACTION_GET_CONTENT, parcels); // Is this what the file pick would return ?
        resultData.putExtras(bundle);

        Intents.intending(IntentMatchers.hasAction(ACTION_GET_CONTENT)).respondWith(new Instrumentation.ActivityResult(RESULT_OK, resultData));

        onView(withId(R.id.camera_button)).perform(click());
        onView(withText("Pick a file")).perform(click());
        intended(IntentMatchers.hasAction(ACTION_GET_CONTENT));
        onView(withId(R.id.note_header)).check(matches(new BoundedMatcher<View, ConstraintLayout>(ConstraintLayout.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with background : ");
            }

            @Override
            protected boolean matchesSafely(ConstraintLayout item) {
                return (Integer)item.getTag() != R.drawable.rounded_corner_just_bottom_bg;
            }
        }));
        Intents.release();
    } */

    @Test
    public void dip2pxWorks() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        float scale = context.getResources().getDisplayMetrics().density;
        float dpInput = 10;

        int pxExpected = (int) (dpInput * scale + 0.5f);

        assertEquals(pxExpected, Utils.dip2px(context, dpInput));
    }
}
