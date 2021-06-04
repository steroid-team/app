package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

import android.content.Context;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.view.DailyPlanningFragment;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DailyPlanningFragmentTest {

    private FragmentScenario<DailyPlanningFragment> scenario;
    @Mock Database databaseMock;

    @Mock Context context;

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Before
    public void setUp() {
        // Mock context for Broadcast Reminder
        doReturn(context).when(context).getApplicationContext();

        TodoList todoList = new TodoList("Some random title");
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any());

        Task task = new Task("Random task title");
        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).putTask(any(), any(Task.class));
        doReturn(taskFuture).when(databaseMock).updateTask(any(), anyInt(), any(Task.class));
        doReturn(taskFuture).when(databaseMock).removeTask(any(), anyInt());
        doReturn(taskFuture).when(databaseMock).setTaskDone(any(), anyInt(), anyBoolean());
        doReturn(taskFuture).when(databaseMock).removeDoneTasks(any());

        TodoListCollection collection = new TodoListCollection();
        collection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        DatabaseFactory.setCustomDatabase(databaseMock);

        File fakeFile = new File("Fake pathname");
        doReturn(fakeFile).when(context).getCacheDir();
        ViewModelFactoryInjection.setCustomTodoListRepo(context, UUID.randomUUID());
        scenario =
                FragmentScenario.launchInContainer(
                        DailyPlanningFragment.class, null, R.style.Theme_Asteroid);
    }

    @Test
    public void taskDescriptionIsCorrectlyDisplayed() {
        final String TASK_DESCRIPTION = "Buy bananas";

        TodoList todoList = new TodoList("Some random title");
        Task task = new Task(TASK_DESCRIPTION);
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any());

        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).getTask(any(), anyInt());

        onView(withId(R.id.task_description)).check(matches(withText(TASK_DESCRIPTION)));
    }

    @Test
    public void setTaskForTodayWorks() throws InterruptedException {
        final String TASK_DESCRIPTION = "Buy bananas";

        TodoList todoList = new TodoList("Some random title");
        Task task = new Task(TASK_DESCRIPTION);
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any());

        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).getTask(any(), anyInt());

        onView(withId(R.id.today_plan_button)).perform(click());
        Thread.sleep(300);
        onView(withId(R.id.today_none_button)).perform(click());
    }
}
