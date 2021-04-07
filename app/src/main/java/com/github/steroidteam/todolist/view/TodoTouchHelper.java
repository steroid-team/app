package com.github.steroidteam.todolist.view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.view.adapter.TodoCollectionAdapter;

public class TodoTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ListSelectionActivity activity;

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
}
