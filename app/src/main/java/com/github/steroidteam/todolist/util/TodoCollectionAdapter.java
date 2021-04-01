package com.github.steroidteam.todolist.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.steroidteam.todolist.ListSelectionActivity;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TodoCollectionAdapter extends RecyclerView.Adapter<TodoCollectionAdapter.TodoHolder> {

    private ArrayList<TodoList> todoListCollection;
    private TodoHolder.TodoCustomListener listener;
    private Integer currentlyDisplayedUpdateLayoutPos;

    public TodoCollectionAdapter(TodoHolder.TodoCustomListener listener) {
        this.currentlyDisplayedUpdateLayoutPos = null;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_todo_list_item, parent, false);
        return new TodoHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoHolder holder, int position) {
        TodoList currentTodo = todoListCollection.get(position);
        if(currentTodo!=null) {
            holder.todoTitle.setText(currentTodo.getTitle());
            holder.idOfTodo = currentTodo.getId();
        }
    }

    @Override
    public int getItemCount() {
        if(todoListCollection==null) {
            return 0;
        }else{
            return todoListCollection.size();
        }
    }

    public void setTodoListCollection(ArrayList<TodoList> todoListCollection) {
        this.todoListCollection = todoListCollection;
        notifyDataSetChanged();
    }

    public Integer getCurrentlyDisplayedUpdateLayoutPos() {
        return this.currentlyDisplayedUpdateLayoutPos;
    }

    public void setCurrentlyDisplayedUpdateLayoutPos(Integer currentlyDisplayedUpdateLayoutPos) {
        this.currentlyDisplayedUpdateLayoutPos = currentlyDisplayedUpdateLayoutPos;
    }

    public static class TodoHolder extends RecyclerView.ViewHolder {

        private final TextView todoTitle;
        private final EditText todoEditTitle;
        private final Button todoSaveBtn;
        private final Button todoDeleteBtn;
        private UUID idOfTodo;

        public TodoHolder(@NonNull View itemView, final TodoCustomListener listener) {
            super(itemView);
            todoTitle = itemView.findViewById(R.id.layout_todo_list_text);
            todoEditTitle = itemView.findViewById(R.id.layout_todo_list_edit_text);
            todoSaveBtn = itemView.findViewById(R.id.layout_todo_list_save_modif);
            todoDeleteBtn = itemView.findViewById(R.id.layout_todo_list_delete_button);

            itemView.setOnLongClickListener((View view) -> {
                    listener.onLongClickCustom(TodoHolder.this);
                    return true;
            });

            itemView.setOnClickListener((View view) -> {
                listener.onClickCustom(TodoHolder.this);
            });
        }

        public void displayUpdateLayout() {
            todoEditTitle.setText(todoTitle.getText().toString());

            todoDeleteBtn.setVisibility(View.VISIBLE);
            todoEditTitle.setVisibility(View.VISIBLE);
            todoSaveBtn.setVisibility(View.VISIBLE);
            todoTitle.setVisibility(View.GONE);
        }

        public void closeUpdateLayout() {
            todoDeleteBtn.setVisibility(View.GONE);
            todoEditTitle.setVisibility(View.GONE);
            todoSaveBtn.setVisibility(View.GONE);
            todoTitle.setVisibility(View.VISIBLE);
        }

        public String getUserInput() {
            return todoEditTitle.getText().toString();
        }

        public UUID getIdOfTodo() {
            return idOfTodo;
        }

        public interface TodoCustomListener {
            void onClickCustom(TodoHolder holder);
            void onLongClickCustom(TodoHolder holder);
        }
    }
}
