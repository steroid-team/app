package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.ListSelectionFragment;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ListSelectionFragmentTest {

    private FragmentScenario<ListSelectionFragment> scenario;
    private final String TODO_1_TITLE = "Some random title";
    private final String TODO_2_TITLE = "Some random title 2";

    @Mock Database databaseMock;

    @Before
    public void init() {
        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList(TODO_1_TITLE);
        todoList.addTask(new Task("A simple task"));
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

        scenario =
                FragmentScenario.launchInContainer(
                        ListSelectionFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void cancelCreateTodoWorks() {

        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

        // button2 = negative button
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));
    }

    @Test
    public void renameTodoWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withId(R.id.alert_dialog_edit_text)).perform(typeText(TODO_2_TITLE));

        onView(isRoot()).perform(waitAtLeastHelper(500));

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList(TODO_2_TITLE);
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // button1 = positive button
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_2_TITLE)));
    }

    @Test
    public void createTodoWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList(TODO_2_TITLE);
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListFuture).when(databaseMock).putTodoList(any(TodoList.class));

        onView(withId(R.id.create_todo_button)).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(typeText(TODO_2_TITLE));
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.add_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_2_TITLE)));

        onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(1));

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(1, TODO_2_TITLE)));
    }

    @Test
    public void deleteTodoWorks() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));

        onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList(TODO_2_TITLE);
        collection.addUUID(UUID.randomUUID());
        collection.addUUID(UUID.randomUUID());

        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.complete(null);
        doReturn(future).when(databaseMock).removeTodoList(any());

        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_2_TITLE)));
    }

    @Test
    public void cancelDeletionWorks() {

        onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeLeft()));

        onView(withText(R.string.delete_todo_suggestion)).check(matches(isDisplayed()));

        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withText(R.string.delete_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));
    }

    @Test
    public void cancelRenamingWorks() {

        onView(withId(R.id.activity_list_selection_itemlist)).perform(scrollToPosition(0));

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withText(R.string.rename_todo_suggestion)).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text)).perform(clearText(), typeText(TODO_2_TITLE));

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withId(android.R.id.button2)).perform(click());

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withText(R.string.rename_todo_suggestion)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));
    }

    @Test
    public void cantRenameTodoWithoutText() {

        onView(withId(R.id.activity_list_selection_itemlist))
                .perform(actionOnItemAtPosition(0, swipeRight()));

        onView(withId(R.id.alert_dialog_edit_text)).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text)).perform(typeText(""));

        onView(withId(android.R.id.button1)).perform(click());

        onView(isRoot()).perform(waitAtLeastHelper(500));

        onView(withId(R.id.alert_dialog_edit_text)).check(doesNotExist());

        onView(withId(R.id.activity_list_selection_itemlist))
                .check(matches(atPositionCheckText(0, TODO_1_TITLE)));
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
                TextView bodyView = todoView.findViewById(R.id.layout_todo_list_text);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }

    /**
     * Helper to test dialog
     *
     * @param millis time to wait
     * @return viewAction that wait the given time
     */
    public static ViewAction waitAtLeastHelper(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return anyView();
            }

            @NonNull
            private Matcher<View> anyView() {
                return new IsAnything<>();
            }

            @Override
            public String getDescription() {
                return "wait for at least " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
