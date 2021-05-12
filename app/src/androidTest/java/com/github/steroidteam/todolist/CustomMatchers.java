package com.github.steroidteam.todolist;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import java.util.Objects;
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
}
