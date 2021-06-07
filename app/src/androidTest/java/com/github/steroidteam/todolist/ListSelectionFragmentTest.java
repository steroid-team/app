package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.github.steroidteam.todolist.CustomMatchers.ItemCountIs;
import static com.github.steroidteam.todolist.CustomMatchers.atPositionCheckText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import androidx.fragment.app.testing.FragmentScenario;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.ListSelectionFragment;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ListSelectionFragmentTest {

    private final int TODO_TITLE_LAYOUT_ID = R.id.layout_todo_list_text;

    private final String TODO_1_TITLE = "Some random title";
    private final String TODO_2_TITLE = "Some random title 2";

    private FragmentScenario<ListSelectionFragment> scenario;
    @Mock Database databaseMock;
    @Mock Context context;

    @Before
    public void init() {

        // Mock context for Broadcast Reminder
        doReturn(context).when(context).getApplicationContext();

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList(TODO_1_TITLE);
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        doReturn(future).when(databaseMock).removeTodoList(any(UUID.class));

        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListFuture).when(databaseMock).putTodoList(any(TodoList.class));
        doReturn(todoListFuture)
                .when(databaseMock)
                .updateTodoList(any(UUID.class), any(TodoList.class));
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        DatabaseFactory.setCustomDatabase(databaseMock);

        File fakeFile = new File("Fake pathname");
        doReturn(fakeFile).when(context).getCacheDir();
        ViewModelFactoryInjection.setCustomTodoListRepo(context, UUID.randomUUID());

        scenario =
                FragmentScenario.launchInContainer(
                        ListSelectionFragment.class, null, R.style.AsteroidTheme);
    }

    @Test
    public void cancelCreateTodoWorks() {

        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

        TodoList todo = new TodoList(TODO_2_TITLE);
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todo);

        // Change the title of the to-do that will be returned:
        doReturn(todoListCompletableFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture).when(databaseMock).putTodoList(any(TodoList.class));

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

        // Check that the title didn't change
        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE, TODO_TITLE_LAYOUT_ID)));

        // Check that it doesn't add a to-do
        onView(withId(R.id.activity_list_selection_itemlist)).check(matches(ItemCountIs(1)));
    }

    @Test
    public void renameTodoWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text))
                .perform(typeText(TODO_2_TITLE), closeSoftKeyboard());

        TodoList todo = new TodoList(TODO_2_TITLE);
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todo);

        // Change the title of the note that will be returned:
        doReturn(todoListCompletableFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture)
                .when(databaseMock)
                .updateTodoList(any(UUID.class), any(TodoList.class));

        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_2_TITLE, TODO_TITLE_LAYOUT_ID)));
    }

    @Test
    public void createTodoWorks() {

        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

        TodoList todo = new TodoList(TODO_2_TITLE);
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todo);

        // Change the title of the to-do that will be returned:
        doReturn(todoListCompletableFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture).when(databaseMock).putTodoList(any(TodoList.class));

        // Return 2 to-do
        TodoListCollection collection = new TodoListCollection();
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        // button1 = positive button
        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(TODO_2_TITLE), closeSoftKeyboard());
        // button1 = positive button
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

        // Check that the title didn't change
        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_2_TITLE, TODO_TITLE_LAYOUT_ID)));

        // Check that it doesn't add a to-do
        onView(withId(R.id.activity_list_selection_itemlist)).check(matches(ItemCountIs(2)));
    }

    @Test
    public void deleteTodoWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

        // Return zero to-do
        List<UUID> todo = Collections.emptyList();
        CompletableFuture<List<UUID>> todoFuture = new CompletableFuture<>();
        todoFuture.complete(todo);
        doReturn(todoFuture).when(databaseMock).getTodoListCollection();

        // button1 = positive button
        onView(withText(R.string.confirm)).perform(click());

        onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

        // removeTodo in the local and remote database
        verify(databaseMock, times(2)).removeTodoList(any(UUID.class));
    }

    @Test
    public void cancelDeletionWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

        // Return zero to-do (as if the button doesn't work and indeed delete note)
        List<UUID> todo = Collections.emptyList();
        CompletableFuture<List<UUID>> todoFuture = new CompletableFuture<>();
        todoFuture.complete(todo);
        doReturn(todoFuture).when(databaseMock).getTodoListCollection();

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist)).check(matches(ItemCountIs(1)));
        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE, TODO_TITLE_LAYOUT_ID)));
    }

    @Test
    public void cancelRenamingWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withText(R.string.rename_todo_suggestion)).check(matches(isDisplayed()));

        TodoList todo = new TodoList(TODO_2_TITLE);
        CompletableFuture<TodoList> todoListCompletableFuture = new CompletableFuture<>();
        todoListCompletableFuture.complete(todo);

        // Change the title of the note that will be returned:
        doReturn(todoListCompletableFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCompletableFuture)
                .when(databaseMock)
                .updateTodoList(any(UUID.class), any(TodoList.class));

        onView(withId(R.id.alert_dialog_edit_text))
                .perform(clearText(), typeText(TODO_2_TITLE), closeSoftKeyboard());

        onView(withId(android.R.id.button2)).perform(click());

        onView(withText(R.string.rename_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE, TODO_TITLE_LAYOUT_ID)));
    }

    @Test
    public void cantRenameTodoWithoutText() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

        TodoList todo = new TodoList(TODO_2_TITLE);
        CompletableFuture<TodoList> todoFuture = new CompletableFuture<>();
        todoFuture.complete(todo);

        // Change the title of the to-do that will be returned:
        doReturn(todoFuture).when(databaseMock).getTodoList(any(UUID.class));

        onView(withId(R.id.alert_dialog_edit_text)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE, TODO_TITLE_LAYOUT_ID)));
    }
}
