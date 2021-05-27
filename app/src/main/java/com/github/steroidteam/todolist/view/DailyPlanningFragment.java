package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.util.Date;

public class DailyPlanningFragment extends Fragment {

    private TodoListViewModel viewModel;
    private int currentTaskIndex = 0;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_daily_planning, container, false);

        // Add a click listener to the "back" button to return to the previous activity.
        root.findViewById(R.id.back_button)
                .setOnClickListener(v -> getParentFragmentManager().popBackStack());

        TodoViewModelFactory todoViewModelFactory =
                ViewModelFactoryInjection.getTodoViewModelFactory(getContext());
        viewModel =
                new ViewModelProvider(requireActivity(), todoViewModelFactory)
                        .get(TodoListViewModel.class);

        viewModel
                .getTodoList()
                .observe(
                        getViewLifecycleOwner(),
                        (todoList) -> {
                            TextView activityTitle = root.findViewById(R.id.activity_title);
                            activityTitle.setText(todoList.getTitle());
                        });

        root.findViewById(R.id.set_done_button).setOnClickListener(this::setDoneButtonListener);
        root.findViewById(R.id.today_plan_button).setOnClickListener(this::skipTaskInPlan);
        root.findViewById(R.id.other_day_plan_button).setOnClickListener(this::skipTaskInPlan);
        root.findViewById(R.id.delete_task_button).setOnClickListener(this::skipTaskInPlan);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        findNextUnplannedTask();
    }

    public void setDoneButtonListener(View view) {
        viewModel.setTaskDone(currentTaskIndex, true);
        findNextUnplannedTask();
    }

    /**
     * Used as placeholder waiting for more features (selecting day and hour / integrate viewmodel
     */
    public void skipTaskInPlan(View view) {
        currentTaskIndex++;
        findNextUnplannedTask();
    }

    /**
     * Finds the next task to be planned, if there isn't anymore it goes back to ItemViewActivity
     */
    private void findNextUnplannedTask() {
        TodoList list = viewModel.getTodoList().getValue();
        while (currentTaskIndex < list.getSize()) {
            Task task = list.getTask(currentTaskIndex);
            Date date = task.getDueDate();
            if (!task.isDone()
                    && (date == null
                            || DateUtils.isToday(date.getTime())
                            || date.before(new Date()))) {
                TextView text = getView().findViewById(R.id.task_description);
                text.setText(task.getBody());
                return;
            }
            currentTaskIndex++;
        }
        getParentFragmentManager().popBackStack();
    }
}
