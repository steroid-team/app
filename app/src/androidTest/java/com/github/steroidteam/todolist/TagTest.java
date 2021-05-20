package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.ItemViewFragment;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TagTest {

    private final String TAG_1_TITLE = "Work";
    private FragmentScenario<ItemViewFragment> scenario;

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
        doReturn(taskFuture)
                .when(databaseMock)
                .updateTask(any(UUID.class), anyInt(), any(Task.class));
        doReturn(taskFuture).when(databaseMock).removeTask(any(UUID.class), anyInt());
        doReturn(taskFuture)
                .when(databaseMock)
                .setTaskDone(any(UUID.class), anyInt(), anyBoolean());

        DatabaseFactory.setCustomDatabase(databaseMock);

        CompletableFuture<List<Tag>> tagsFuture = new CompletableFuture<>();
        tagsFuture.complete(new ArrayList<>());
        doReturn(tagsFuture).when(databaseMock).getTagsFromIds(any());

        Bundle bundle = new Bundle();
        bundle.putSerializable("list_id", UUID.randomUUID());
        scenario =
                FragmentScenario.launchInContainer(
                        ItemViewFragment.class, bundle, R.style.Theme_Asteroid);
    }

    @Test
    public void tagButtonOpensTagWindow() {
        onView(withId(R.id.itemview_tag_button)).perform(click());
        onView(withId(R.id.layout_update_tags)).check(matches(isDisplayed()));
    }

    @Test
    public void createTagButtonWorks() {
        onView(withId(R.id.itemview_tag_button)).perform(click());
        onView(withParent(withId(R.id.tag_row_first))).perform(click());
        onView(withText(R.string.add_tag_suggestion)).check(matches(isDisplayed()));

        onView(withId(R.id.alert_dialog_edit_text))
                .inRoot(isDialog())
                .perform(clearText(), typeText(TAG_1_TITLE));
        // button1 = positive button
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        onView(withText(R.string.add_tag_suggestion)).check(doesNotExist());
        onView(withId(R.id.tag_row_first)).check(matches(hasChildCount(2)));
    }

    @Test
    public void closeLayoutButtonWorks() {
        onView(withId(R.id.itemview_tag_button)).perform(click());
        onView(withId(R.id.itemview_tag_save_button)).perform(click());
        onView(withId(R.id.layout_update_tags))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void getAndSetBodyWork() {
        Tag tag = new Tag("TagTitle");
        tag.setBody("newTagTitle");
        assertEquals("newTagTitle", tag.getBody());
    }

    @Test
    public void getAndSetColorWork() {
        Tag tag = new Tag("TagTitle");
        tag.setColor(Color.BLUE);
        assertEquals(Color.BLUE, tag.getColor());
    }

    @Test
    public void toStringWorks() {
        Tag tag = new Tag("TagTitle");
        assertEquals("Tag{TagTitle}", tag.toString());
    }
}
