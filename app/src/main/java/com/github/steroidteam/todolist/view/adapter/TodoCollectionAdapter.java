package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

public class TodoCollectionAdapter extends RecyclerView.Adapter<TodoCollectionAdapter.TodoHolder> {

    private ArrayList<TodoList> todoListCollection;
    private TodoHolder.TodoCustomListener listener;

    // ViewPool to share view between task and to-do list
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public TodoCollectionAdapter(TodoHolder.TodoCustomListener listener) {
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
        TodoList currentTodo = todoListCollection.get(position);
        if (currentTodo != null) {
            holder.todoTitle.setText(currentTodo.getTitle());
            holder.idOfTodo = currentTodo.getId();

            // Nested layout Manager
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(
                            holder.taskListRecyclerView.getContext(),
                            LinearLayoutManager.VERTICAL,
                            false);

            // Define how many child we need to prefetch when building the nested recyclerView
            layoutManager.setInitialPrefetchItemCount(currentTodo.getImportantTask().size());

            // Create a TodoChild adapter as we create a Todo Collection Adapter
            //  in the List Selection Activity
            TodoChildAdapter childAdapter = new TodoChildAdapter(currentTodo.getImportantTask());
            holder.taskListRecyclerView.setLayoutManager(layoutManager);
            holder.taskListRecyclerView.setAdapter(childAdapter);
            holder.taskListRecyclerView.setRecycledViewPool(viewPool);
        }
    }

    @Override
    public int getItemCount() {
        if (todoListCollection == null) {
            return 0;
        } else {
            return todoListCollection.size();
        }
    }

    public void setTodoListCollection(ArrayList<TodoList> todoListCollection) {
        this.todoListCollection = todoListCollection;
        notifyDataSetChanged();
    }

    public static class TodoHolder extends RecyclerView.ViewHolder {

        private final RecyclerView taskListRecyclerView;

        private final TextView todoTitle;
        private UUID idOfTodo;

        public TodoHolder(@NonNull View itemView, final TodoCustomListener listener) {
            super(itemView);

            taskListRecyclerView = itemView.findViewById(R.id.layout_todo_list_recycler_view);

            todoTitle = itemView.findViewById(R.id.layout_todo_list_text);

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(TodoHolder.this);
                    });
        }

        public UUID getIdOfTodo() {
            return idOfTodo;
        }

        public interface TodoCustomListener {
            void onClickCustom(TodoHolder holder);
        }
    }
}
