package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.NoteSelectionFragment;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoteSelectionFragmentTest {

    private FragmentScenario<NoteSelectionFragment> scenario;
    @Mock Database databaseMock;

    @Before
    public void init() {
        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        Note note = new Note("Some random title");
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
}
