package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.TodoList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TodoArrayListAdapter extends RecyclerView.Adapter<TodoArrayListAdapter.TodoHolder> {

    private List<TodoList> todoListArrayList;
    private final TodoHolder.TodoCustomListener listener;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private Map<UUID, List<Tag>> tagsMap;

    public TodoArrayListAdapter(TodoHolder.TodoCustomListener listener) {
        this.listener = listener;
        tagsMap = new HashMap<>();
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
            // Nested layout Manager
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(
                            holder.tagListRecyclerView.getContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false);

            List<Tag> tags = new ArrayList<>();
            if (tagsMap.get(currentTodo.getId()) != null){
                tags = tagsMap.get(currentTodo.getId());
            }


            // Define how many child we need to prefetch when building the nested recyclerView
            layoutManager.setInitialPrefetchItemCount(tags.size());

            // Create a TodoChild adapter as we create a To-Do Collection Adapter
            //  in the List Selection Activity
            TagListAdapter childAdapter = new TagListAdapter(tags);
            holder.tagListRecyclerView.setLayoutManager(layoutManager);
            holder.tagListRecyclerView.setAdapter(childAdapter);
            holder.tagListRecyclerView.setRecycledViewPool(viewPool);
        }
    }

    @Override
    public int getItemCount() {
        return (todoListArrayList == null) ? 0 : todoListArrayList.size();
    }

    public void setTodoListCollection(List<TodoList> todoListCollection) {
        this.todoListArrayList = todoListCollection;
    }

    public void putTagsInMap(UUID todolistId, List<Tag> tags) {
        tagsMap.put(todolistId, tags);
    }

    public static class TodoHolder extends RecyclerView.ViewHolder {
        private final TextView todoTitle;
        private TodoList todoList;
        private final RecyclerView tagListRecyclerView;

        public TodoHolder(@NonNull View itemView, final TodoCustomListener listener) {
            super(itemView);

            todoTitle = itemView.findViewById(R.id.layout_todo_list_text);
            tagListRecyclerView = itemView.findViewById(R.id.layout_todo_list_tags);

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
