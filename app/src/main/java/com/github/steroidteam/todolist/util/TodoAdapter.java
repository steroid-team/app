package com.github.steroidteam.todolist.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskHolder> {

    private TodoList todoList = new TodoList("This should not be displayed");
    private TaskCustomListener listener;
    private Boolean deleteButtonIsVisible;

    public TodoAdapter(TaskCustomListener listener) {
        this.deleteButtonIsVisible = false;
        this.listener = listener;
    }

    public void switchDeleteButton() {
        deleteButtonIsVisible = !this.deleteButtonIsVisible;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_task_item, parent, false);
        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        Task currentTask = todoList.getTask(position);
        holder.taskBody.setText(currentTask.getBody());
        // Need to add the getChecked in Task class
        // holder.taskBox.set(currentTask.getChecked())

        if(this.deleteButtonIsVisible) {
            holder.taskDelete.setVisibility(View.VISIBLE);
        }else {
            holder.taskDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClickCustom(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.getSize();
    }

    public void setTodoList(TodoList todoList) {
        //Updates the adapter with the new todoList (the observable one)
        this.todoList = todoList;
        //Check notifyDataSetChanged() might not be the best function
        //considering performance
        notifyDataSetChanged();
    }

    public interface TaskCustomListener {

        void onLongClickCustom(int position);
    }

    class TaskHolder extends RecyclerView.ViewHolder {

        private TextView taskBody;
        private CheckBox taskBox;
        private Button taskDelete;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskBody = itemView.findViewById(R.id.layout_task_body);
            taskBox = itemView.findViewById(R.id.layout_task_checkbox);
            taskDelete = itemView.findViewById(R.id.layout_task_delete_button);
        }
    }
}
