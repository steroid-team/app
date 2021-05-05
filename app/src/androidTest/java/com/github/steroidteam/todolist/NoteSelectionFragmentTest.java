package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteSelectionFragmentTest {

    private final String NOTE_TITLE_1 = "Some random title";
    private final String NOTE_TITLE_2 = "ANOTHER TITLE";

    private FragmentScenario<NoteSelectionFragment> scenario;
    @Mock Database databaseMock;

    @Before
    public void init() {
        List<UUID> notes = Collections.singletonList(UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        Note note = new Note(NOTE_TITLE_1);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList("Some random title");
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        doReturn(future).when(databaseMock).removeNote(any());

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        DatabaseFactory.setCustomDatabase(databaseMock);

        scenario =
                FragmentScenario.launchInContainer(
                        NoteSelectionFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void openListWorks() {
        // Set a test NavController in the fragment to check the navigation flow.
        TestNavHostController navController =
                new TestNavHostController(ApplicationProvider.getApplicationContext());

        scenario.onFragment(
                fragment -> {
                    navController.setGraph(R.navigation.mobile_navigation);
                    Navigation.setViewNavController(fragment.requireView(), navController);
                });

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(actionOnItemAtPosition(0, click()));

        // Check that we are now in the note display view.
        assertThat(navController.getCurrentDestination().getId(), equalTo(R.id.nav_note_display));
    }

    @Test
    public void cannotRenameNoteWithoutText() {
        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        // because there shouldn't be a call to the database as we change nothing
        onView(withId(R.id.alert_dialog_edit_text)).inRoot(isDialog()).perform(clearText());
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1)));
    }

    @Test
    public void cancelRenamingWorks() {
        // SAME AS ABOVE BUT WITH CANCEL BUTTON EVEN IF THERE IS A NEW TITLE

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        // Pressing the ngative button the title shouldn't change,
        // because there shouldn't be a call to the database as we change nothing (cancel renaming)
        // button2 = negative button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1)));
    }

    @Test
    public void renamingNoteWorks() {
        // SAME AS ABOVE BUT WITH CANCEL BUTTON EVEN IF THERE IS A NEW TITLE

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        // there should be a call to the database as we change the title
        // button1 = positive button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_2)));
    }

    @Test
    public void cancelCreateNoteWorks() {

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(1)));
    }

    @Test
    public void createNoteWorks() {

        onView(withId(R.id.create_note_button)).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(matches(isDisplayed()));

        Note note = new Note(NOTE_TITLE_2);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        // Change the title of the note that will be returned:
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));
        doReturn(noteFuture).when(databaseMock).putNote(any(UUID.class), any(Note.class));

        // Return 2 Notes
        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button1 = positive button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(NOTE_TITLE_2));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.new_note_btn_description)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(2)));

        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_2)));
    }

    @Test
    public void cancelDeleteNoteWorks() {

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                ItemViewFragmentTest.MyViewAction.clickChildViewWithId(
                                        R.id.layout_note_delete_btn)));

        onView(withText(R.string.delete_note_suggestion)).check(matches(isDisplayed()));

        // Return zero note (as if the button doesn't work and indeed delete note)
        List<UUID> notes = Collections.emptyList();
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.delete_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(1)));
        onView(withId(R.id.activity_noteselection_recycler))
                .check(matches(atPositionCheckText(0, NOTE_TITLE_1)));
    }

    @Test
    public void deleteNoteWorks() {

        onView(withId(R.id.activity_noteselection_recycler))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                ItemViewFragmentTest.MyViewAction.clickChildViewWithId(
                                        R.id.layout_note_delete_btn)));

        onView(withText(R.string.delete_note_suggestion)).check(matches(isDisplayed()));

        // Return zero note (as if the button doesn't work and indeed delete note)
        List<UUID> notes = Collections.emptyList();
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);
        doReturn(notesFuture).when(databaseMock).getNotesList();

        // button1 = positive button
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.delete_note_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_noteselection_recycler)).check(matches(ItemCountIs(0)));
    }

    /**
     * Helper to check if the given text is the same as the to-do list title at the given position
     *
     * @param position the index of the to-do in the recycler view
     * @param expectedText the text that should be the title of the to-do
     * @return Mactcher to test the title
     */
    public static Matcher<View> atPositionCheckText(
            final int position, @NonNull final String expectedText) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "View holder at position "
                                + position
                                + ", expected: "
                                + expectedText
                                + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View todoView = view.getChildAt(position);
                TextView bodyView = todoView.findViewById(R.id.layout_note_title);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }

    /** Helper to check the size of a recyclerView */
    public static Matcher<View> ItemCountIs(@NonNull final int expectedCount) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Recycler View should have " + expectedCount + " items");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                return view.getAdapter().getItemCount() == expectedCount;
            }
        };
    }
}
