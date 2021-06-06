package com.github.steroidteam.todolist;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class LinearLayoutMatcher {
    private final int recyclerViewId;

    public LinearLayoutMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

        return new TypeSafeMatcher<View>() {
            Resources resources = null;
            View childView;

            public void describeTo(Description description) {
                String idDescription = Integer.toString(recyclerViewId);
                if (this.resources != null) {
                    try {
                        idDescription = this.resources.getResourceName(recyclerViewId);
                    } catch (Resources.NotFoundException var4) {
                        idDescription =
                                String.format(
                                        "%s (resource name not found)",
                                        new Object[] {Integer.valueOf(recyclerViewId)});
                    }
                }

                description.appendText("with id: " + idDescription);
            }

            public boolean matchesSafely(View view) {

                this.resources = view.getResources();

                if (childView == null) {
                    LinearLayout linearLayout = view.getRootView().findViewById(recyclerViewId);
                    if (linearLayout != null && linearLayout.getId() == recyclerViewId) {
                        childView = linearLayout.getChildAt(position);
                    } else {
                        return false;
                    }
                }

                if (targetViewId == -1) {
                    return view == childView;
                } else {
                    View targetView = childView.findViewById(targetViewId);
                    return view == targetView;
                }
            }
        };
    }
}
