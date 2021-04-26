package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.ItemViewActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ListSelectionActivityTest {

    Intent intent;

    @Mock Database databaseMock;

    @Before
    public void before() {
        Intents.init();

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList("Some random title");
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        List<UUID> notes = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListFuture)
                .when(databaseMock)
                .updateTodoList(any(UUID.class), any(TodoList.class));
        doReturn(notesFuture).when(databaseMock).getNotesList();

        DatabaseFactory.setCustomDatabase(databaseMock);

        intent =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void openListWorks() {
        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(click());
            intended(hasComponent(ItemViewActivity.class.getName()));
        }
    }

    @Test
    public void openNotesWorks() {
        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.notes_button2)).perform(click());
            intended(hasComponent(NoteSelectionActivity.class.getName()));
        }
    }

    @Test
    public void cannotRenameTodoListWithoutText() {
        Intent listSelectionActivity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario =
                ActivityScenario.launch(listSelectionActivity)) {
            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(longClick());

            // User input
            onView(withText("Please enter a new name")).check(matches(isDisplayed()));

            onView(withClassName(endsWith("EditText"))).perform(clearText());

            onView(withText("Confirm")).inRoot(isDialog()).perform(click());

            // make sure dialog is gone
            onView(withText("Please enter a new name")).check(doesNotExist());
        }
    }

    @Test
    public void renameTodoListWorks() {
        final String TODO_LIST_NAME = "Sweng Test";

        try (ActivityScenario<ListSelectionActivity> scenario = ActivityScenario.launch(intent)) {

            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(longClick());

            onView(withText("Please enter a new name")).check(matches(isDisplayed()));

            onView(withClassName(endsWith("EditText")))
                    .perform(replaceText(TODO_LIST_NAME), closeSoftKeyboard());

            onView(withText("Confirm")).inRoot(isDialog()).perform(click());
        }
    }
}
