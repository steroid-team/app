package com.github.steroidteam.todolist.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final SwipeListener swipeListener;

    public SwipeTouchHelper(SwipeListener swipeListener) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (direction == ItemTouchHelper.LEFT) {
                swipeListener.onSwipeLeft(viewHolder, position);
            } else {
                // direction == ItemTouchHelper.RIGHT
                swipeListener.onSwipeRight(viewHolder, position);
            }
        }
    }

    @Override
    public void onChildDraw(
            Canvas c,
            RecyclerView recyclerView,
            RecyclerView.ViewHolder holder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, holder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = holder.itemView;

            ColorDrawable colorToDisplay;
            Drawable deleteIcon =
                    ContextCompat.getDrawable(
                            itemView.getContext(), R.drawable.ic_baseline_delete_24_icon);
            Drawable renameIcon =
                    ContextCompat.getDrawable(
                            itemView.getContext(), R.drawable.ic_baseline_create_24_icon);

            // Margin that keeps the icon vertically centered.
            int verticalIconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
            // Margin that keeps the icon easily visible after some swiping.
            int horizontalIconMargin = 60; // px

            if (dX > 0) {
                colorToDisplay =
                        new ColorDrawable(
                                ContextCompat.getColor(
                                        itemView.getContext(), R.color.green_pastel));

                colorToDisplay.setBounds(
                        itemView.getLeft(),
                        itemView.getTop(),
                        (int) dX + 100,
                        itemView.getBottom());

                renameIcon.setBounds(
                        itemView.getLeft() + horizontalIconMargin,
                        itemView.getTop() + verticalIconMargin,
                        itemView.getLeft() + horizontalIconMargin + renameIcon.getIntrinsicWidth(),
                        itemView.getBottom() - verticalIconMargin);
            } else if (dX < 0) {
                colorToDisplay =
                        new ColorDrawable(
                                ContextCompat.getColor(itemView.getContext(), R.color.red_pastel));
                colorToDisplay.setBounds(
                        itemView.getRight() + (int) dX - 100,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom());

                deleteIcon.setBounds(
                        itemView.getRight() - horizontalIconMargin - deleteIcon.getIntrinsicWidth(),
                        itemView.getTop() + verticalIconMargin,
                        itemView.getRight() - horizontalIconMargin,
                        itemView.getBottom() - verticalIconMargin);
            } else {
                colorToDisplay = new ColorDrawable(Color.WHITE);
            }

            colorToDisplay.draw(c);
            deleteIcon.draw(c);
            renameIcon.draw(c);
        }
    }

    public interface SwipeListener {

        void onSwipeLeft(final RecyclerView.ViewHolder viewHolder, final int position);

        void onSwipeRight(final RecyclerView.ViewHolder viewHolder, final int position);
    }
}
