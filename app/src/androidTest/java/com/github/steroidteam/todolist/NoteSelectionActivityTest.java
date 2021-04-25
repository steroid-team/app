package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
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
public class NoteSelectionActivityTest {

    Intent intent;

    @Mock Database databaseMock;

    @Before
    public void before() {
        Intents.init();

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

        intent =
                new Intent(
                        ApplicationProvider.getApplicationContext(), NoteSelectionActivity.class);
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void openListWorks() {
        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_noteselection_notelist))
                    .atPosition(1)
                    .perform(click());

            Intents.intended(
                    Matchers.allOf(
                            IntentMatchers.hasComponent(NoteDisplayActivity.class.getName())));
        }
    }

    @Test
    public void openTodoListWorks() {
        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.activity_noteselection_button)).perform(click());

            Intents.intended(
                    Matchers.allOf(
                            IntentMatchers.hasComponent(ListSelectionActivity.class.getName())));
        }
    }
}
