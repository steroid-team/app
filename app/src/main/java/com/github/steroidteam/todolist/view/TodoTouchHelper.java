package com.github.steroidteam.todolist.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.view.adapter.TodoCollectionAdapter;

public class TodoTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final ListSelectionActivity activity;

    public TodoTouchHelper(ListSelectionActivity activity) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.activity = activity;
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
                activity.removeTodo(
                        ((TodoCollectionAdapter.TodoHolder) viewHolder).getIdOfTodo(), position);
            } else {
                // direction == ItemTouchHelper.RIGHT
                activity.renameTodo(
                        ((TodoCollectionAdapter.TodoHolder) viewHolder).getIdOfTodo(), position);
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
            } else if (dX < 0) {
                colorToDisplay =
                        new ColorDrawable(
                                ContextCompat.getColor(itemView.getContext(), R.color.red_pastel));
                colorToDisplay.setBounds(
                        itemView.getRight() + (int) dX - 100,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom());
            } else {
                colorToDisplay = new ColorDrawable(Color.WHITE);
            }

            colorToDisplay.draw(c);
        }
    }
}
