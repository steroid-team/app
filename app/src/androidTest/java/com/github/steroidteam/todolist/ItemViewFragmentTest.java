package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.ItemViewFragment;
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
public class ItemViewFragmentTest {

    @Mock Database databaseMock;

    @Before
    public void setUp() {
        TodoList todoList = new TodoList("Some random title");
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        Task task = new Task("Random task title");
        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).putTask(any(UUID.class), any(Task.class));
        doReturn(taskFuture).when(databaseMock).renameTask(any(UUID.class), anyInt(), anyString());
        doReturn(taskFuture).when(databaseMock).removeTask(any(UUID.class), anyInt());

        DatabaseFactory.setCustomDatabase(databaseMock);

        Bundle bundle = new Bundle();
        bundle.putSerializable("list_id", UUID.randomUUID());
        FragmentScenario<ItemViewFragment> scenario =
                FragmentScenario.launchInContainer(
                        ItemViewFragment.class, bundle, R.style.Theme_Asteroid);
    }

    @Test
    public void createTaskWorks() {
        final String TASK_DESCRIPTION = "Buy bananas";

        TodoList todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        todoList.addTask(new Task(TASK_DESCRIPTION));
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        // The task description text field should now be empty.
        onView(withId(R.id.new_task_text)).check(matches(withText("")));

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckText(0, TASK_DESCRIPTION)));
    }

    @Test
    public void cannotCreateTaskWithoutText() {
        // Clear the text input.
        onView(withId(R.id.new_task_text)).perform(clearText());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist)).check(matches(isDisplayed()));
    }

    @Test
    public void cannotRenameTaskWithoutText() {
        final String TASK_DESCRIPTION = "Buy bananas";

        TodoList todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        todoList.addTask(new Task(TASK_DESCRIPTION));
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.layout_update_task_body)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.layout_update_task_save)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckText(0, TASK_DESCRIPTION)));

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckBox(0, false)));
    }

    @Test
    public void updateTaskWorks() {

        final String TASK_DESCRIPTION = "Buy bananas";
        final String TASK_DESCRIPTION_2 = "Buy cheese";

        TodoList todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        Task task = new Task(TASK_DESCRIPTION);
        task.setDone(true);
        todoList.addTask(task);
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0, MyViewAction.clickChildViewWithId(R.id.layout_task_checkbox)));

        onView(withId(R.id.activity_itemview_itemlist))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.layout_update_task_body)).check(matches(withText(TASK_DESCRIPTION)));
        onView(withId(R.id.layout_update_task_checkbox)).check(matches(isChecked()));

        todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        task = new Task(TASK_DESCRIPTION_2);
        task.setDone(false);
        todoList.addTask(task);
        todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        onView(withId(R.id.layout_update_task_checkbox)).perform(click());
        onView(withId(R.id.layout_update_task_body))
                .perform(clearText(), typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

        onView(withId(R.id.layout_update_task_save)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckBox(0, false)));
    }

    @Test
    public void removeTaskWorks() {
        final String TASK_DESCRIPTION = "Buy bananas";
        final String TASK_DESCRIPTION_2 = "Buy cheese";

        /* First we should return two tasks */
        TodoList todoList = new TodoList("Some random title");
        todoList.addTask(new Task(TASK_DESCRIPTION));
        todoList.addTask(new Task(TASK_DESCRIPTION_2));
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.new_task_text))
                .perform(typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

        onView(withId(R.id.new_task_btn)).perform(click());

        /* Then we should return one task */
        todoList = new TodoList("Some random title");
        todoList.addTask(new Task(TASK_DESCRIPTION_2));
        todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Try to remove the first task
        onView(withId(R.id.activity_itemview_itemlist))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0, MyViewAction.clickChildViewWithId(R.id.layout_task_checkbox)));
        onView(withId(R.id.activity_itemview_itemlist))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                MyViewAction.clickChildViewWithId(R.id.layout_task_delete_button)));

        // after deleting the first item we check that we have the second one at position 0.
        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));
    }

    @Test
    public void removeTaskWorksInUpdateLayout() {
        final String TASK_DESCRIPTION = "Buy bananas";
        final String TASK_DESCRIPTION_2 = "Buy cheese";

        TodoList todoList = new TodoList("Some random title");
        todoList.addTask(new Task(TASK_DESCRIPTION_2));
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.new_task_text))
                .perform(typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

        onView(withId(R.id.new_task_btn)).perform(click());

        // Try to remove the first task
        onView(withId(R.id.activity_itemview_itemlist))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.layout_update_task_body)).perform(closeSoftKeyboard());

        onView(withId(R.id.layout_update_task_delete)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));

        onView(withId(R.id.activity_itemview_itemlist))
                .check(matches(atPositionCheckBox(0, false)));
    }

    @Test
    public void notificationDeleteWorks() {
        final String TASK_DESCRIPTION = "Buy bananas";

        TodoList todoList = new TodoList("Some random title");
        todoList.addTask(new Task(TASK_DESCRIPTION));
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

        // Type a task description in the "new task" text field.
        onView(withId(R.id.new_task_text)).perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

        // Hit the button to create a new task.
        onView(withId(R.id.new_task_btn)).perform(click());

        onView(withId(R.id.activity_itemview_itemlist))
                .perform(actionOnItemAtPosition(0, longClick()));

        onView(withId(R.id.activity_itemview_itemlist))
                .perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                MyViewAction.clickChildViewWithId(R.id.layout_task_delete_button)));

        // FIXME : unable to check if toast appeared
        /*onView(withText("Successfully removed the task !"))
        .inRoot(new ToastMatcher())
        .check(matches(isDisplayed()));*/
    }

    public static Matcher<View> atPositionCheckText(
            final int position, @NonNull final String expectedText) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "View holder at position "
                                + String.valueOf(position)
                                + ", expected: "
                                + expectedText
                                + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View taskView = view.getChildAt(position);
                TextView bodyView = taskView.findViewById(R.id.layout_task_body);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }

    public static Matcher<View> atPositionCheckBox(
            final int position, @NonNull final boolean expectedBox) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "View holder at position "
                                + String.valueOf(position)
                                + ", expected: "
                                + expectedBox
                                + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View taskView = view.getChildAt(position);
                CheckBox boxView = taskView.findViewById(R.id.layout_task_checkbox);
                return boxView.isChecked() == expectedBox;
            }
        };
    }

    // Simple ViewAction to click on the button within a item of the recyclerView
    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }
}