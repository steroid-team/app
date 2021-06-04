package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.LoginActivity;
import com.github.steroidteam.todolist.view.MainActivity;
import com.google.firebase.auth.FirebaseUser;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock Database databaseMock;

    @Rule
    public ActivityScenarioRule<MainActivity> testRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @Before
    public void initDatabase() {
        // NOTE PART
        List<UUID> notes = Collections.singletonList(UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        Note note = new Note("NOTE_TITLE_1");
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any(UUID.class));

        // TO-DO LIST PART
        TodoListCollection collection = new TodoListCollection();
        TodoList todoList = new TodoList("TODO_1_TITLE");
        collection.addUUID(UUID.randomUUID());
        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        CompletableFuture<TodoList> todoListFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(collection);
        todoListFuture.complete(todoList);

        doReturn(todoListFuture).when(databaseMock).getTodoList(any(UUID.class));
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        // SET THE MOCK DATABASE
        DatabaseFactory.setCustomDatabase(databaseMock);
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }


    @Test
    public void redirectsToLoginActivityWhenNullUser() {
        // We start the main activity with no logged in user, triggering a redirect to the
        // LoginActivity.
        // Technically this should not even happen in practice, but we have it covered to be safe
        // against NullPointerExceptions.
        UserFactory.set(null);

        Intent mainActivity =
                new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(mainActivity)) {
            intended(hasComponent(LoginActivity.class.getName()));
        }
    }

    @Test
    public void navDrawerWorks() {
        FirebaseUser mockedUser = Mockito.mock(FirebaseUser.class);
        UserFactory.set(mockedUser);

        Intent mainActivity =
                new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(mainActivity)) {
            // List selection is displayed by default.
            onView(withId(R.id.fragment_list_selection)).check(matches(isDisplayed()));

            // Open the drawer and select "Notes".
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
            onView(withId(R.id.nav_note_selection)).perform(click());
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

            // Note selection should be displayed now.
            onView(withId(R.id.fragment_note_selection)).check(matches(isDisplayed()));

            // Open the drawer again and select "Todos".
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
            onView(withId(R.id.nav_list_selection)).perform(click());
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

            // List selection should be displayed now.
            onView(withId(R.id.fragment_list_selection)).check(matches(isDisplayed()));

            // Open the drawer again and select "User".
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
            onView(withId(R.id.nav_profile)).perform(click());
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

            // List selection should be displayed now.
            onView(withId(R.id.fragment_profile)).check(matches(isDisplayed()));

            // Open the drawer again and select "User".
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
            onView(withId(R.id.nav_credits)).perform(click());
            onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

            // List selection should be displayed now.
            onView(withId(R.id.fragment_credits)).check(matches(isDisplayed()));
        }
    }

    /*
    @Test
    public void navDrawerHeaderShowsUserInfo() {
        final String FIXTURE_USER_NAME = "John Doe";
        final String FIXTURE_USER_EMAIL = "j.doe@example.com";

        FirebaseUser mockedUser = Mockito.mock(FirebaseUser.class);
        when(mockedUser.getDisplayName()).thenReturn(FIXTURE_USER_NAME);
        when(mockedUser.getEmail()).thenReturn(FIXTURE_USER_EMAIL);
        UserFactory.set(mockedUser);

        Intent mainActivity =
                new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(mainActivity)) {

            // Open the drawer.
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

            // Check that the displayed user name & email match those of the mocked user.
            onView(withId(R.id.nav_user_name)).check(matches(withText(FIXTURE_USER_NAME)));
            onView(withId(R.id.nav_user_email)).check(matches(withText(FIXTURE_USER_EMAIL)));
        }
    }
    */
}
