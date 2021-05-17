package com.github.steroidteam.todolist.view.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.provider.CalendarContract;
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

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskHolder> {

    private TodoList todoList;
    private TaskCustomListener listener;
    private final int TASK_NO_DUE_DATE = -1;

    public TodoAdapter(TaskCustomListener listener) {
        this.listener = listener;
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
    public int getItemViewType(int position) {
        Task currentTask = todoList.getTask(position);
        Date dueDate = currentTask.getDueDate();
        if (dueDate == null) {
            // No date
            return TASK_NO_DUE_DATE;
        }
        Date currDate = new Date();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(currDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);
        int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - calendar2.get(Calendar.DAY_OF_YEAR);
        // Today
        if (diffDay < 1) {
            return 0;
        }
        // This week
        if (diffDay < 7) {
            return 1;
        }
        // Later
        return 2;
        //return super.getItemViewType(position);
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
        void onItemClick(TaskHolder holder, final int position);

        void onItemDelete(final int position);

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

            taskDelete.setOnClickListener(v -> listener.onItemDelete(getAdapterPosition()));

            itemView.setOnClickListener(
                    (v) -> {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(this, getAdapterPosition());
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

    public class TaskHolderToday extends TaskHolder {

        public TaskHolderToday(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    /**

    public class TaskHolderWeek extends TaskHolder {

    }

    public class TaskHolderLater extends TaskHolder {

    }

    public class TaskHolderNoDate extends TaskHolder {

    }
     **/
}
