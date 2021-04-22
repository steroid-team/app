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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.ItemViewActivity;
import com.github.steroidteam.todolist.view.ListSelectionActivity;
import com.github.steroidteam.todolist.viewmodel.ItemViewModel;
import java.util.UUID;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ItemViewActivityTest {

    @Mock TodoRepository repository;

    MutableLiveData<TodoList> liveData;
    TodoList todoList;

    @Before
    public void setUp() {
        todoList = new TodoList("Some random title");
        liveData = new MutableLiveData<>();

        doReturn(liveData).when(repository).getTodoList();

        /* Stub remove */
        doAnswer(
                        invocation -> {
                            Integer index = invocation.getArgument(0);

                            todoList.removeTask(index);
                            liveData.setValue(todoList);

                            return null;
                        })
                .when(repository)
                .removeTask(anyInt());

        /* Stub put */
        doAnswer(
                        invocation -> {
                            Task task = invocation.getArgument(0);

                            todoList.addTask(task);
                            liveData.setValue(todoList);

                            return null;
                        })
                .when(repository)
                .putTask(any(Task.class));

        /* Stub rename */
        doAnswer(
                        invocation -> {
                            Integer index = invocation.getArgument(0);
                            String newTitle = invocation.getArgument(1);

                            todoList.renameTask(index, newTitle);
                            liveData.setValue(todoList);

                            return null;
                        })
                .when(repository)
                .renameTask(anyInt(), anyString());
    }

    @Test
    public void createTaskWorks() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {
            final String TASK_DESCRIPTION = "Buy bananas";

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            // The task description text field should now be empty.
            onView(withId(R.id.new_task_text)).check(matches(withText("")));

            onView(withId(R.id.activity_itemview_itemlist))
                    .check(matches(atPositionCheckText(0, TASK_DESCRIPTION)));
        }
    }

    @Test
    public void cannotCreateTaskWithoutText() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            // Clear the text input.
            onView(withId(R.id.new_task_text)).perform(clearText());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            onView(withId(R.id.activity_itemview_itemlist)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void cannotRenameTaskWithoutText() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {
            final String TASK_DESCRIPTION = "Buy bananas";

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

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
    }

    @Test
    public void updateTaskWorks() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            final String TASK_DESCRIPTION = "Buy bananas";
            final String TASK_DESCRIPTION_2 = "Buy cheese";

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(
                                    0,
                                    MyViewAction.clickChildViewWithId(R.id.layout_task_checkbox)));

            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            onView(withId(R.id.layout_update_task_body)).check(matches(withText(TASK_DESCRIPTION)));
            onView(withId(R.id.layout_update_task_checkbox)).check(matches(isChecked()));

            onView(withId(R.id.layout_update_task_checkbox)).perform(click());
            onView(withId(R.id.layout_update_task_body))
                    .perform(clearText(), typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

            onView(withId(R.id.layout_update_task_save)).perform(click());

            onView(withId(R.id.activity_itemview_itemlist))
                    .check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));

            onView(withId(R.id.activity_itemview_itemlist))
                    .check(matches(atPositionCheckBox(0, false)));
        }
    }

    @Test
    public void removeTaskWorks() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            final String TASK_DESCRIPTION = "Buy bananas";
            final String TASK_DESCRIPTION_2 = "Buy cheese";

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION_2), closeSoftKeyboard());

            onView(withId(R.id.new_task_btn)).perform(click());

            // Try to remove the first task
            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(
                                    0,
                                    MyViewAction.clickChildViewWithId(R.id.layout_task_checkbox)));
            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(
                                    0,
                                    MyViewAction.clickChildViewWithId(
                                            R.id.layout_task_delete_button)));

            // after deleting the first item we check that we have the second one at position 0.
            onView(withId(R.id.activity_itemview_itemlist))
                    .check(matches(atPositionCheckText(0, TASK_DESCRIPTION_2)));
        }
    }

    @Test
    public void removeTaskWorksInUpdateLayout() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {
            final String TASK_DESCRIPTION = "Buy bananas";
            final String TASK_DESCRIPTION_2 = "Buy cheese";

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

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
    }

    @Test
    public void notificationDeleteWorks() {
        Intent intent =
                new Intent(ApplicationProvider.getApplicationContext(), ItemViewActivity.class);
        intent.putExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST, UUID.randomUUID().toString());

        try (ActivityScenario<ItemViewActivity> scenario = ActivityScenario.launch(intent)) {
            final String TASK_DESCRIPTION = "Buy bananas";

            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.onActivity(activity -> activity.setViewModel(new ItemViewModel(repository)));
            scenario.moveToState(Lifecycle.State.STARTED);
            scenario.moveToState(Lifecycle.State.RESUMED);

            // Type a task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(TASK_DESCRIPTION), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(actionOnItemAtPosition(0, longClick()));

            onView(withId(R.id.activity_itemview_itemlist))
                    .perform(
                            RecyclerViewActions.actionOnItemAtPosition(
                                    0,
                                    MyViewAction.clickChildViewWithId(
                                            R.id.layout_task_delete_button)));

            // FIXME : unable to check if toast appeared
            /*onView(withText("Successfully removed the task !"))
            .inRoot(new ToastMatcher())
            .check(matches(isDisplayed()));*/
        }
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

    public static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }
    }
}
