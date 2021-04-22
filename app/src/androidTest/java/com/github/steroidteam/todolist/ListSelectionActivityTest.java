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

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.ItemViewActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.view.NoteSelectionActivity;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RunWith(MockitoJUnitRunner.class)
public class ListSelectionActivityTest {

    @Mock
    Database databaseMock;

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

        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListFuture).when(databaseMock).
                updateTodoList(any(UUID.class), any(TodoList.class));
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void openListWorks() {
        Intent intent =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario =
                ActivityScenario.launch(intent)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setDatabase(databaseMock));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            Espresso.onData(anything())
                    .inAdapterView(withId(R.id.activity_list_selection_itemlist))
                    .atPosition(0)
                    .perform(click());
            intended(hasComponent(ItemViewActivity.class.getName()));
        }
    }

    @Test
    public void openNotesWorks() {
        Intent intent =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario =
                     ActivityScenario.launch(intent)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setDatabase(databaseMock));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

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
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setDatabase(databaseMock));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

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

        Intent listSelectionActivity =
                new Intent(
                        ApplicationProvider.getApplicationContext(), ListSelectionActivity.class);

        try (ActivityScenario<ListSelectionActivity> scenario =
                ActivityScenario.launch(listSelectionActivity)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setDatabase(databaseMock));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

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
