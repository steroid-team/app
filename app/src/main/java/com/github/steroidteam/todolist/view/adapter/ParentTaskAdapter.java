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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParentTaskAdapter extends RecyclerView.Adapter<ParentTaskAdapter.ParentTaskHolder> {

    private List<String> dateCategoryList, sortedDateCategoryList;
    private TodoList todoList;
    // ViewPool to share view between task and to-do list
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final TodoAdapter.TaskCustomListener listener;
    private final int TASK_PAST = 0;
    private final int TASK_TODAY = 1;
    private final int TASK_TOMORROW = 2;
    private final int TASK_WEEK = 3;
    private final int TASK_LATER = 4;
    private final int TASK_UNKNOWN = 5;
    private final int TASK_DONE = 6;
    private final int INDEX_TASK_NOT_FOUND = -1;

    public ParentTaskAdapter(TodoAdapter.TaskCustomListener listener) {
        this.listener = listener;
        dateCategoryList = new ArrayList<>();
        dateCategoryList.add("Past due");
        dateCategoryList.add("Today");
        dateCategoryList.add("Tomorrow");
        dateCategoryList.add("Next 5 days after Tomorrow");
        dateCategoryList.add("Later");
        dateCategoryList.add("Unknown");
        dateCategoryList.add("Done");
        sortedDateCategoryList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ParentTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.parent_recycler_view_task_item, parent, false);
        return new ParentTaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentTaskHolder holder, int position) {
        int realPosition = getIndexRealCategory(position);
        String currentCategory = sortedDateCategoryList.get(position);
        if (currentCategory != null) {
            TodoList todoListInCategory = getTodoListForCategory(realPosition);
            holder.itemView.setVisibility(View.VISIBLE);
            if (todoListInCategory.getSize() == 0) {
                holder.itemView.setVisibility(View.GONE);
            }
            holder.taskDateCategory.setText(currentCategory);

            // Nested layout Manager
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(
                            holder.taskListRecyclerView.getContext(),
                            LinearLayoutManager.VERTICAL,
                            false);

            // Define how many child we need to prefetch when building the nested recyclerView

            layoutManager.setInitialPrefetchItemCount(todoListInCategory.getSize());

            Map<Task, Integer> taskIntegerMap = new HashMap<>();
            for (int i = 0; i < todoListInCategory.getSize(); i++) {
                Task currTask = todoListInCategory.getTask(i);
                taskIntegerMap.put(currTask, getPositionInTodolist(currTask));
            }

            // Create a TodoChild adapter as we create a To-Do Collection Adapter
            //  in the List Selection Activity
            TodoAdapter childAdapter = new TodoAdapter(listener, taskIntegerMap);
            childAdapter.setTodoList(todoListInCategory);
            holder.taskListRecyclerView.setLayoutManager(layoutManager);
            holder.taskListRecyclerView.setAdapter(childAdapter);
            holder.taskListRecyclerView.setRecycledViewPool(viewPool);
        }
    }

    private void sortCategoryList() {
        sortedDateCategoryList.clear();
        List<Integer> noTaskCategories = new ArrayList<>();
        for (int i = 0; i < dateCategoryList.size(); i++) {
            if (getTodoListForCategory(i).getSize() == 0) {
                noTaskCategories.add(i);
            } else {
                sortedDateCategoryList.add(dateCategoryList.get(i));
            }
        }
        for (int i = 0; i < noTaskCategories.size(); i++) {
            sortedDateCategoryList.add(dateCategoryList.get(noTaskCategories.get(i)));
        }
    }

    private int getIndexRealCategory(int position) {
        for (int i = 0; i < sortedDateCategoryList.size(); i++) {
            if (sortedDateCategoryList.get(position).equals(dateCategoryList.get(i))) {
                return i;
            }
        }
        return INDEX_TASK_NOT_FOUND;
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

        if (task.isDone() && category == TASK_DONE) {
            return true;
        }

        if (task.isDone() && category != TASK_DONE) {
            return false;
        }

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
        // Past
        if (diffDay < 0 && category == TASK_PAST) {
            return true;
        }
        // Today
        if (diffDay == 0 && category == TASK_TODAY) {
            return true;
        }
        // Tomorrow
        if (diffDay == 1 && category == TASK_TOMORROW) {
            return true;
        }
        // Next 5 days after tomorrow
        if (diffDay < 7 && diffDay > 1 && category == TASK_WEEK) {
            return true;
        }
        // Later
        return diffDay >= 7 && category == TASK_LATER;
    }

    public int getPositionInTodolist(Task task) {
        for (int i = 0; i < todoList.getSize(); i++) {
            Task currTask = todoList.getTask(i);
            if (currTask.getBody() == task.getBody()
                    && currTask.getDueDate() == task.getDueDate()) {
                return i;
            }
        }
        return INDEX_TASK_NOT_FOUND;
    }

    @Override
    public int getItemCount() {
        return (dateCategoryList == null) ? 0 : dateCategoryList.size();
    }

    public void setParentTodoList(TodoList todoList) {
        // Updates the adapter with the new todoList (the observable one)
        this.todoList = todoList.sortByDate();
        sortCategoryList();
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
        }
    }
}
