package com.github.steroidteam.todolist;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomMatchers {
    /**
     * Helper to check if the given text is the same as the to-do list title at the given position
     *
     * @param position the index of the to-do in the recycler view
     * @param expectedText the text that should be the title of the to-do
     * @return Mactcher to test the title
     */
    public static Matcher<View> atPositionCheckText(
            final int position, @NonNull final String expectedText, @NonNull final int layout_id) {
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
                View holder = view.getChildAt(position);
                TextView bodyView = holder.findViewById(layout_id);
                return bodyView.getText().toString().equals(expectedText);
            }
        };
    }

    /**
     * Helper to check if the given text is present somewhere in a RecyclerView's n-th task.
     *
     * @param position the index of the child in the recycler view
     * @param expectedSubstring the text that should be contained in the child
     * @return Matcher to execute the test
     */
    public static Matcher<View> atPositionTextContains(
            final int position, @NonNull final String expectedSubstring) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText(
                        "View holder at position "
                                + String.valueOf(position)
                                + ", expected to contain: "
                                + expectedSubstring
                                + " ");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                View taskView = view.getChildAt(position);
                TextView bodyView = taskView.findViewById(R.id.layout_task_body);
                return bodyView.getText().toString().contains(expectedSubstring);
            }
        };
    }

    public static Matcher<View> atPositionCheckBox(
            final int position, @NonNull final boolean expectedBox, @NonNull final int layout_id) {
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
                CheckBox boxView = taskView.findViewById(layout_id);
                return boxView.isChecked() == expectedBox;
            }
        };
    }

    /** Helper to check the size of a recyclerView */
    public static Matcher<View> ItemCountIs(@NonNull final int expectedCount) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("Recycler View should have " + expectedCount + " items");
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                return Objects.requireNonNull(view.getAdapter()).getItemCount() == expectedCount;
            }
        };
    }

    // Simple ViewAction to click on the button within a item of the recyclerView

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

    /**
     * Perform action of waiting for a specific view id.
     *
     * @param viewId The id of the view to wait for.
     * @param millis The timeout of until when to wait for.
     */
    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <"
                        + viewId
                        + "> during "
                        + millis
                        + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return;
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50);
                } while (System.currentTimeMillis() < endTime);

                // timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }
}
