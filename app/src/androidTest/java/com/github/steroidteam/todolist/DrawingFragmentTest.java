package com.github.steroidteam.todolist;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.github.steroidteam.todolist.view.DrawingView.BACKGROUND_COLOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.notes.Note;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.github.steroidteam.todolist.view.DrawingFragment;
import com.github.steroidteam.todolist.view.MainActivity;
import com.google.firebase.auth.FirebaseUser;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DrawingFragmentTest {

    private final String FIXTURE_DEFAULT_NOTE_TITLE = "My note";
    private final String FIXTURE_DEFAULT_NOTE_CONTENT = "My note content";

    private FragmentScenario<DrawingFragment> scenario;

    @Mock Database databaseMock;

    @Before
    public void init() {
        Note note = new Note(FIXTURE_DEFAULT_NOTE_TITLE);
        note.setContent(FIXTURE_DEFAULT_NOTE_CONTENT);
        CompletableFuture<Note> noteFuture = new CompletableFuture<>();
        noteFuture.complete(note);

        List<UUID> notes = Collections.singletonList(UUID.randomUUID());
        CompletableFuture<List<UUID>> notesFuture = new CompletableFuture<>();
        notesFuture.complete(notes);

        doReturn(notesFuture).when(databaseMock).getNotesList();
        doReturn(noteFuture).when(databaseMock).getNote(any());
        doReturn(noteFuture).when(databaseMock).updateNote(any(), any());

        // Since we are using an activity, set a mocked user.
        FirebaseUser mockedUser = Mockito.mock(FirebaseUser.class);
        UserFactory.set(mockedUser);
        scenario =
                FragmentScenario.launchInContainer(
                        DrawingFragment.class, null, R.style.AsteroidTheme);
        CompletableFuture<TodoListCollection> todoListCollectionFuture = new CompletableFuture<>();
        todoListCollectionFuture.complete(new TodoListCollection());
        doReturn(todoListCollectionFuture).when(databaseMock).getTodoListCollection();

        DatabaseFactory.setCustomDatabase(databaseMock);
    }

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Test
    public void drawingWorks() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(5, 5);
                    fragment.drawingCanvas.touchMove(5, 15);
                    assertEquals(
                            Color.BLACK,
                            fragment.drawingCanvas.getBitmap().getColor(5, 10).toArgb());
                    fragment.drawingCanvas.erase();
                });

        onView(withId(R.id.drawing_space)).perform(ViewActions.swipeRight());

        scenario.onFragment(
                fragment -> {
                    boolean draw = false;
                    Bitmap bitmap = fragment.drawingCanvas.getBitmap();
                    for (int x = 0; x < bitmap.getWidth() && !draw; x++) {
                        for (int y = 0; y < bitmap.getHeight() && !draw; y++) {
                            if (bitmap.getPixel(x, y) == Color.BLACK) {
                                draw = true;
                            }
                        }
                    }
                    assertTrue(draw);
                });
    }
    /*
    @Test
    public void firstButtonWorks() {
        onView(withId(R.id.drawing_first_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            fragment.getActivity().getColor(R.color.first_drawing_button),
                            fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void secondButtonWorks() {
        onView(withId(R.id.drawing_second_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            fragment.getActivity().getColor(R.color.second_drawing_button),
                            fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void thirdButtonWorks() {
        onView(withId(R.id.drawing_third_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            fragment.getActivity().getColor(R.color.third_drawing_button),
                            fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void fourthButtonWorks() {
        onView(withId(R.id.drawing_fourth_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            fragment.getActivity().getColor(R.color.fourth_drawing_button),
                            fragment.drawingCanvas.getPaint().getColor());
                });
    }

    @Test
    public void erasesButtonWorks() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(100, 100);
                    fragment.drawingCanvas.touchMove(200, 100);
                });
        onView(withId(R.id.drawing_erase_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(
                            BACKGROUND_COLOR,
                            fragment.drawingCanvas.getBitmap().getColor(150, 100).toArgb());
                });
    }

    @Test
    public void colorPickerButtonWorks() {
        onView(withId(R.id.drawing_fifth_btn)).perform(click());
        onView(withId(R.id.drawing_color_picker)).check(matches(isDisplayed()));
        scenario.onFragment(
                fragment -> {
                    assertEquals(fragment.drawingCanvas.getVisibility(), GONE);
                });
    }

    @Test
    public void backButtonWorks() {
        onView(withId(R.id.drawing_back_btn)).perform(click());
        assertEquals(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    @Test
    public void saveButtonWithPermissionWorks() {
        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .grantRuntimePermission(
                        InstrumentationRegistry.getInstrumentation()
                                .getTargetContext()
                                .getPackageName(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

        InstrumentationRegistry.getInstrumentation()
                .getUiAutomation()
                .grantRuntimePermission(
                        InstrumentationRegistry.getInstrumentation()
                                .getTargetContext()
                                .getPackageName(),
                        Manifest.permission.READ_EXTERNAL_STORAGE);

        onView(withId(R.id.drawing_save_btn)).perform(click());
        assertEquals(Lifecycle.State.DESTROYED, activityRule.getScenario().getState());
    }

    @Test
    public void smallMovesDoNotDraw() {
        scenario.onFragment(
                fragment -> {
                    fragment.drawingCanvas.touchStart(100, 100);
                    fragment.drawingCanvas.touchMove(100, 102);
                    assertEquals(
                            BACKGROUND_COLOR,
                            fragment.drawingCanvas.getBitmap().getColor(100, 101).toArgb());
                });
    }

    @Test
    public void InvalidMoveDoesNotDraw() {
        scenario.onFragment(
                fragment -> {
                    MotionEvent invalidEvent =
                            MotionEvent.obtain(2, 2, MotionEvent.ACTION_SCROLL, 100, 100, 5);
                    invalidEvent.setAction(MotionEvent.ACTION_SCROLL);
                    Bitmap bitmap = fragment.drawingCanvas.getBitmap();
                    Bitmap before = bitmap.copy(bitmap.getConfig(), false);
                    fragment.drawingCanvas.onTouchEvent(invalidEvent);
                    assertTrue(fragment.drawingCanvas.getBitmap().sameAs(before));
                });
    }

    @Test
    public void ApplyColorWorks() {
        onView(withId(R.id.drawing_fifth_btn)).perform(click());
        onView(withId(R.id.drawing_apply_color_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertNotEquals(Color.BLACK, fragment.drawingCanvas.getPaint().getColor());
                    assertEquals(fragment.drawingCanvas.getVisibility(), VISIBLE);
                });
    }

    @Test
    public void CancelColorWorks() {
        onView(withId(R.id.drawing_fifth_btn)).perform(click());
        onView(withId(R.id.drawing_cancel_color_btn)).perform(click());
        scenario.onFragment(
                fragment -> {
                    assertEquals(Color.BLACK, fragment.drawingCanvas.getPaint().getColor());
                    assertEquals(fragment.drawingCanvas.getVisibility(), VISIBLE);
                });
    }*/
}
