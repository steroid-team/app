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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
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
import androidx.test.rule.GrantPermissionRule;
import com.github.steroidteam.todolist.broadcast.ReminderDateBroadcast;
import com.github.steroidteam.todolist.broadcast.ReminderLocationBroadcast;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.ItemViewFragment;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

@RunWith(MockitoJUnitRunner.class)
public class ItemViewFragmentTest {

    private FragmentScenario<ItemViewFragment> scenario;
    @Mock Database databaseMock;

    @Rule
    public GrantPermissionRule coarseLocationPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

    @Rule
    public GrantPermissionRule fineLocationPermissionRule =
            GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

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
        doReturn(taskFuture)
                .when(databaseMock)
                .updateTask(any(UUID.class), anyInt(), any(Task.class));
        doReturn(taskFuture).when(databaseMock).removeTask(any(UUID.class), anyInt());
        doReturn(taskFuture)
                .when(databaseMock)
                .setTaskDone(any(UUID.class), anyInt(), anyBoolean());

        CompletableFuture<List<Tag>> tagsFuture = new CompletableFuture<>();
        tagsFuture.complete(new ArrayList<>());
        doReturn(tagsFuture).when(databaseMock).getTagsFromIds(any());

        DatabaseFactory.setCustomDatabase(databaseMock);

        Bundle bundle = new Bundle();
        bundle.putSerializable("list_id", UUID.randomUUID());
        scenario =
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
        Task task = new Task(TASK_DESCRIPTION);
        todoList.addTask(task);
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).getTask(any(UUID.class), anyInt());

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
    public void datesAreRemovedFromTaskBodyUponCreation() {
        HashMap<String, String> fixtureDates = new HashMap<>();
        fixtureDates.put("Call Sammy today at noon", " today at noon");
        fixtureDates.put("Deliver at 8 PM the package", " at 8 PM");
        fixtureDates.put(
                "The day after tomorrow at 13:00 buy 3 avocados",
                "The day after " + "tomorrow at 13:00 ");

        PrettyTimeParser timeParser = new PrettyTimeParser();

        for (Map.Entry<String, String> entry : fixtureDates.entrySet()) {
            String originalTaskDescription = entry.getKey();

            // Start the view with an empty list.
            TodoList todoList = new TodoList("Some random title");
            CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
            todoListFuture.complete(todoList);
            doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));

            // Type the task description in the "new task" text field.
            onView(withId(R.id.new_task_text))
                    .perform(typeText(originalTaskDescription), closeSoftKeyboard());

            // Hit the button to create a new task.
            onView(withId(R.id.new_task_btn)).perform(click());

            Task task = new Task(originalTaskDescription.replace(entry.getValue(), ""));
            task.setDueDate(timeParser.parse(entry.getValue()).get(0));

            verify(databaseMock).putTask(any(UUID.class), eq(task));
        }
    }

    @Test
    public void updateTaskWorks() {

        final String TASK_DESCRIPTION = "Buy bananas";
        final String TASK_DESCRIPTION_2 = "Buy cheese";

        TodoList todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        Task originalTask = new Task(TASK_DESCRIPTION);
        todoList.addTask(originalTask);
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(originalTask);
        doReturn(taskFuture).when(databaseMock).getTask(any(UUID.class), anyInt());

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

        // Make sure that the database was called to update the task, and that the updated task
        // is now set as "done".
        Task updatedTask = new Task(originalTask.getBody());
        updatedTask.setDone(true);
        verify(databaseMock).updateTask(any(UUID.class), anyInt(), eq(updatedTask));

        onView(withId(R.id.layout_update_task_body)).check(matches(withText(TASK_DESCRIPTION)));
        onView(withId(R.id.layout_update_task_checkbox)).check(matches(isChecked()));

        todoList = new TodoList("Some random title");
        /* We should return a new todoList with this task description once we've created it */
        originalTask = new Task(TASK_DESCRIPTION_2);
        originalTask.setDone(false);
        todoList.addTask(originalTask);
        todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        taskFuture = new CompletableFuture<>();
        taskFuture.complete(originalTask);
        doReturn(taskFuture).when(databaseMock).getTask(any(UUID.class), anyInt());

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
        Task task = new Task(TASK_DESCRIPTION_2);
        todoList.addTask(task);
        todoListFuture = new CompletableFuture<>();
        todoListFuture.complete(todoList);
        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        CompletableFuture<Task> taskFuture = new CompletableFuture<>();
        taskFuture.complete(task);
        doReturn(taskFuture).when(databaseMock).getTask(any(UUID.class), anyInt());

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

    @Test
    public void notificationReminderWorks() {
        String testDescription = "This is a test";
        int twoSecondsInMillis = 2 * 1000;
        Date date = new Date(System.currentTimeMillis() + twoSecondsInMillis);

        scenario.onFragment(
                fragment -> {
                    ReminderDateBroadcast.createNotificationChannel(fragment.getActivity());
                    ReminderDateBroadcast.createNotification(
                            date, testDescription, fragment.getActivity());
                });

        /**
         * Have to wait a little bit more than the 2 seconds because for this small value it takes
         * in fact like 3 or 4 seconds to display the notification *
         */
        try {
            Thread.sleep(3 * twoSecondsInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scenario.onFragment(
                fragment -> {
                    boolean isDisplayed = false;
                    NotificationManager notificationManager =
                            (NotificationManager)
                                    fragment.getContext()
                                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    StatusBarNotification[] notifications =
                            notificationManager.getActiveNotifications();
                    for (StatusBarNotification notification : notifications) {
                        if (notification.getId() == ReminderDateBroadcast.REMINDER_DATE_ID) {
                            isDisplayed = true;
                        }
                    }
                    assertEquals(true, isDisplayed);
                });
    }

    @Test
    public void notificationLocationReminderWorks() {
        scenario.onFragment(
                fragment -> {
                    ReminderDateBroadcast.createNotificationChannel(fragment.getActivity());
                    LocationManager locationManager =
                            (LocationManager)
                                    fragment.getActivity()
                                            .getSystemService(Context.LOCATION_SERVICE);

                    // Location of Sydney
                    Location loc = new Location(LocationManager.GPS_PROVIDER);
                    loc.setLatitude(-33.8523341);
                    loc.setLongitude(151.2106085);
                    loc.setAccuracy(1.0f);
                    loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                    loc.setTime(System.currentTimeMillis());
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    assertEquals(
                                            ReminderLocationBroadcast.createLocationNotification(
                                                    location, fragment.getActivity()),
                                            true);
                                }

                                @Override
                                public void onProviderDisabled(@NonNull String provider) {}

                                @Override
                                public void onProviderEnabled(@NonNull String provider) {}
                            });
                    mockGps(loc, locationManager);
                });

        // FIXME : unable to check if toast appeared
        /*
        onView(withText("The reminder has been set !"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
        */
    }

    public void mockGps(Location location, LocationManager mLocationManager)
            throws SecurityException {
        location.setProvider(LocationManager.GPS_PROVIDER);
        try {
            // @throws IllegalArgumentException if a provider with the given name already exists
            mLocationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    0,
                    5);
        } catch (IllegalArgumentException ignored) {
        }

        try {
            // @throws IllegalArgumentException if no provider with the given name exists
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        } catch (IllegalArgumentException ignored) {
            mLocationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    0,
                    5);
        }

        try {
            // @throws IllegalArgumentException if no provider with the given name exists
            mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
        } catch (IllegalArgumentException ignored) {
            mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            mLocationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    0,
                    5);
            mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
            mLocationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, location);
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
}
