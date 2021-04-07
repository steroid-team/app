package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import java.util.List;

public class TodoChildAdapter extends RecyclerView.Adapter<TodoChildAdapter.TodoChildViewHolder> {

    private List<Task> todoChildTaskList;

    TodoChildAdapter(List<Task> list) {
        this.todoChildTaskList = list;
    }

    @NonNull
    @Override
    public TodoChildAdapter.TodoChildViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_todo_list_child, parent, false);

        return new TodoChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TodoChildAdapter.TodoChildViewHolder holder, int position) {
        Task currentTask = todoChildTaskList.get(position);
        if (currentTask != null) {
            holder.taskBody.setText(currentTask.getBody());
        }
    }

    @Override
    public int getItemCount() {
        return todoChildTaskList.size();
    }

    class TodoChildViewHolder extends RecyclerView.ViewHolder {

        TextView taskBody;

        public TodoChildViewHolder(@NonNull View itemView) {
            super(itemView);
            taskBody = itemView.findViewById(R.id.layout_todo_list_child_text);
        }
    }
}
