package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;

public class TodoArrayListAdapter extends RecyclerView.Adapter<TodoArrayListAdapter.TodoHolder> {

    private ArrayList<TodoList> todoListArrayList;
    private final TodoHolder.TodoCustomListener listener;

    public TodoArrayListAdapter(TodoHolder.TodoCustomListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_todo_list_item, parent, false);
        return new TodoHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
        TodoList currentTodo = todoListArrayList.get(position);
        if (currentTodo != null) {
            holder.todoTitle.setText(currentTodo.getTitle());
            holder.todoList = currentTodo;
        }
    }

    @Override
    public int getItemCount() {
        if (todoListArrayList == null) {
            return 0;
        } else {
            return todoListArrayList.size();
        }
    }

    public void setTodoListCollection(ArrayList<TodoList> todoListCollection) {
        this.todoListArrayList = todoListCollection;
        notifyDataSetChanged();
    }

    public static class TodoHolder extends RecyclerView.ViewHolder {
        private final TextView todoTitle;
        private TodoList todoList;

        public TodoHolder(@NonNull View itemView, final TodoCustomListener listener) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.layout_todo_list_text);

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(TodoHolder.this);
                    });
        }

        public TodoList getTodo() {
            return todoList;
        }

        public interface TodoCustomListener {
            void onClickCustom(TodoHolder holder);
        }
    }
}
