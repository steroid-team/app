package com.github.steroidteam.todolist.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

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

        holder.itemView.setOnClickListener(v -> listener.onClickCustom(position));
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

    public Integer getCurrentlyDisplayedUpdateLayoutPos() {
        return this.currentlyDisplayedUpdateLayoutPos;
    }

    public void setCurrentlyDisplayedUpdateLayoutPos(Integer currentlyDisplayedUpdateLayoutPos) {
        this.currentlyDisplayedUpdateLayoutPos = currentlyDisplayedUpdateLayoutPos;
    }

    public interface TaskCustomListener {
        void onClickCustom(int position);
    }

    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView taskBody;
        private final CheckBox taskBox;
        private final Button taskDelete;
        private final EditText inputText;
        private final Button btn_save;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskBody = itemView.findViewById(R.id.layout_task_body);
            taskBox = itemView.findViewById(R.id.layout_task_checkbox);
            taskDelete = itemView.findViewById(R.id.layout_task_delete_button);
            inputText = itemView.findViewById(R.id.layout_task_edit_text);
            btn_save = itemView.findViewById(R.id.layout_task_save_modif);
        }

        public void displayUpdateLayout() {
            inputText.setText(taskBody.getText().toString());

            taskDelete.setVisibility(View.VISIBLE);
            inputText.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.VISIBLE);
            taskBody.setVisibility(View.GONE);
        }

        public void closeUpdateLayout() {
            taskDelete.setVisibility(View.GONE);
            inputText.setVisibility(View.GONE);
            btn_save.setVisibility(View.GONE);
            taskBody.setVisibility(View.VISIBLE);
        }

        public String getUserInput() {
            return inputText.getText().toString();
        }
    }
}
