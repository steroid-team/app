package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParentTaskAdapter extends RecyclerView.Adapter<ParentTaskAdapter.ParentTaskHolder> {

    private List<String> dateCategoryList;
    private TodoList todoList;
    // ViewPool to share view between task and to-do list
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final TodoAdapter.TaskCustomListener listener;
    private final int TASK_TODAY = 0;
    private final int TASK_TOMORROW = 1;
    private final int TASK_WEEK = 2;
    private final int TASK_LATER = 3;
    private final int TASK_UNKNOWN = 4;

    public ParentTaskAdapter(TodoAdapter.TaskCustomListener listener) {
        this.listener = listener;
        dateCategoryList = new ArrayList<>();
        dateCategoryList.add("Today");
        dateCategoryList.add("Tomorrow");
        dateCategoryList.add("This week");
        dateCategoryList.add("Later");
        dateCategoryList.add("Unknown");
    }

    @NonNull
    @Override
    public ParentTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.parent_recycler_view_task_item, parent, false);
        // return new ParentTaskHolder(itemView, listener);
        return new ParentTaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentTaskHolder holder, int position) {
        String currentCategory = dateCategoryList.get(position);
        if (currentCategory != null) {
            holder.taskDateCategory.setText(currentCategory);

            // Nested layout Manager
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(
                            holder.taskListRecyclerView.getContext(),
                            LinearLayoutManager.VERTICAL,
                            false);

            // Define how many child we need to prefetch when building the nested recyclerView
            TodoList todoListInCategory = getTodoListForCategory(position);
            layoutManager.setInitialPrefetchItemCount(todoListInCategory.getSize());

            // Create a TodoChild adapter as we create a To-Do Collection Adapter
            //  in the List Selection Activity
            TodoAdapter childAdapter = new TodoAdapter(listener, todoListInCategory);
            holder.taskListRecyclerView.setLayoutManager(layoutManager);
            holder.taskListRecyclerView.setAdapter(childAdapter);
            holder.taskListRecyclerView.setRecycledViewPool(viewPool);
        }
    }

    private TodoList getTodoListForCategory(int category) {
        TodoList childTodoList = new TodoList(todoList.getTitle() + category);
        for (int i = 0; i < todoList.getSize(); i++) {
            Task currentTask = todoList.getTask(i);
            if (isInCategory(currentTask, category)) {
                childTodoList.addTask(currentTask);
            }
        }
        return childTodoList;
    }

    private boolean isInCategory(Task task, int category) {
        Date dueDate = task.getDueDate();
        // Unknown date
        if (dueDate == null && category == TASK_UNKNOWN) {
            return true;
        }
        if (dueDate == null) {
            return false;
        }
        Date currDate = new Date();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(currDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);
        int diffDay = calendar.get(Calendar.DAY_OF_YEAR) - calendar2.get(Calendar.DAY_OF_YEAR);
        // Today
        if (diffDay < 1 && category == TASK_TODAY) {
            return true;
        }
        // Tomorrow
        if (diffDay == 1 && category == TASK_TOMORROW) {
            return true;
        }
        // This week
        if (diffDay < 7 && diffDay > 2 && category == TASK_WEEK) {
            return true;
        }
        // Later
        if (diffDay > 7 && category == TASK_LATER) {
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return (dateCategoryList == null) ? 0 : dateCategoryList.size();
    }

    public void setParentTodoList(TodoList todoList) {
        // Updates the adapter with the new todoList (the observable one)
        // this.todoList = todoList;
        this.todoList = todoList.sortByDate();
        // Check notifyDataSetChanged() might not be the best function
        // considering performance
        notifyDataSetChanged();
    }

    public static class ParentTaskHolder extends RecyclerView.ViewHolder {
        private final TextView taskDateCategory;
        private final RecyclerView taskListRecyclerView;

        public ParentTaskHolder(@NonNull View itemView) {
            super(itemView);

            taskDateCategory = itemView.findViewById(R.id.date_category);
            taskListRecyclerView = itemView.findViewById(R.id.child_task_recycler_view);

            /**
             * itemView.setOnClickListener( (View view) -> {
             * listener.onClickCustom(ParentTaskHolder.this); });
             */
        }

        public interface ParentTaskCustomListener {
            void onClickCustom(ParentTaskHolder holder);
        }
    }
}
