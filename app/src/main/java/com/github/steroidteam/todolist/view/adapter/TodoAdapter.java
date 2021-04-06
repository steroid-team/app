package com.github.steroidteam.todolist.view.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskHolder> {

    private TodoList todoList = new TodoList("This should not be displayed");
    private TaskCustomListener listener;
    private Integer currentlyDisplayedUpdateLayoutPos;

    public TodoAdapter(TaskCustomListener listener) {
        this.listener = listener;
        this.currentlyDisplayedUpdateLayoutPos = null;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutNotDone =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_task_item, parent, false);
        return new TaskHolder(layoutNotDone);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {

        Task currentTask = todoList.getTask(position);

        holder.taskBody.setText(currentTask.getBody());
        holder.taskBox.setChecked(currentTask.isDone());

        if (currentTask.isDone()) {
            holder.taskBody.setTextColor(Color.LTGRAY);
            holder.taskBody.setPaintFlags(
                    holder.taskBody.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.displayDeleteButton();
        } else {
            holder.taskBody.setTextColor(Color.DKGRAY);
            holder.taskBody.setPaintFlags(0);
            holder.hideDeleteButton();
        }
    }

    @Override
    public int getItemCount() {
        return todoList.getSize();
    }

    public void setTodoList(TodoList todoList) {
        // Updates the adapter with the new todoList (the observable one)
        this.todoList = todoList;
        // Check notifyDataSetChanged() might not be the best function
        // considering performance
        notifyDataSetChanged();
    }

    public interface TaskCustomListener {
        void onClickCustom(TaskHolder holder, int position);
        // void onFocusChangedCustom(int position, boolean hasFocus);
        void onCheckedChangedCustom(int position, boolean isChecked);
    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView taskBody;
        private final CheckBox taskBox;
        private final Button taskDelete;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskBody = itemView.findViewById(R.id.layout_task_body);
            taskBox = itemView.findViewById(R.id.layout_task_checkbox);
            taskDelete = itemView.findViewById(R.id.layout_task_delete_button);

            taskBox.setOnClickListener(
                    (v) -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onCheckedChangedCustom(
                                    getAdapterPosition(), taskBox.isChecked());
                        }
                    });

            itemView.setOnClickListener(
                    (v) -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClickCustom(this, getAdapterPosition());
                        }
                    });
        }

        public void displayDeleteButton() {
            taskDelete.setVisibility(View.VISIBLE);
        }

        public void hideDeleteButton() {
            taskDelete.setVisibility(View.GONE);
        }

        public String getTaskBody() {
            return taskBody.getText().toString();
        }

        public boolean getTaskDone() {
            return taskBox.isChecked();
        }
    }
}
