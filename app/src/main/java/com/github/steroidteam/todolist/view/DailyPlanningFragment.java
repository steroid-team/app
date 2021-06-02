package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;

import java.util.Calendar;
import java.util.Date;

public class DailyPlanningFragment extends Fragment {

    private TodoListViewModel viewModel;
    private int currentTaskIndex = -1;

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

        setListeners(root);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        findNextUnplannedTask();
    }

    public void setListeners(View root) {
        root.findViewById(R.id.set_done_button).setOnClickListener(this::setDoneButtonListener);
        root.findViewById(R.id.today_plan_button).setOnClickListener(this::switchToToday);
        root.findViewById(R.id.other_day_plan_button).setOnClickListener(this::switchToOtherDay);
        root.findViewById(R.id.delete_task_button).setOnClickListener(this::skipTaskInPlan);

        root.findViewById(R.id.today_none_button).setOnClickListener(new SetDateListener(Plan.TODAY));
        root.findViewById(R.id.midday_button).setOnClickListener(new SetDateListener(Plan.MIDDAY));
        root.findViewById(R.id.afternoon_button).setOnClickListener(new SetDateListener(Plan.AFTERNOON));
        root.findViewById(R.id.evening_button).setOnClickListener(new SetDateListener(Plan.EVENING));
        root.findViewById(R.id.night_button).setOnClickListener(new SetDateListener(Plan.NIGHT));
        root.findViewById(R.id.tomorrow_button).setOnClickListener(new SetDateListener(Plan.TOMORROW));
        root.findViewById(R.id.twodays_plan_button).setOnClickListener(new SetDateListener(Plan.TWODAYS));
        root.findViewById(R.id.week_plan_button).setOnClickListener(new SetDateListener(Plan.WEEK));
        root.findViewById(R.id.oneday_button).setOnClickListener(new SetDateListener(Plan.ONEDAY));
    }

    public void setDoneButtonListener(View view) {
        viewModel.setTaskDone(currentTaskIndex, true);
        findNextUnplannedTask();
    }

    public void switchToToday(View view) {
        ConstraintLayout mainPlan = getView().findViewById(R.id.main_plan);
        mainPlan.setVisibility(View.GONE);
        ConstraintLayout todayPlan = getView().findViewById(R.id.today_plan);
        todayPlan.setVisibility(View.VISIBLE);
    }

    public void switchToOtherDay(View view) {
        ConstraintLayout mainPlan = getView().findViewById(R.id.main_plan);
        mainPlan.setVisibility(View.GONE);
        ConstraintLayout otherDayPlan = getView().findViewById(R.id.other_day_plan);
        otherDayPlan.setVisibility(View.VISIBLE);
    }

    /**
     * Used as placeholder waiting for more features (selecting day and hour / integrate viewmodel
     */
    public void skipTaskInPlan(View view) {
        findNextUnplannedTask();
    }

    /**
     * Finds the next task to be planned, if there isn't anymore it goes back to ItemViewActivity
     */
    private void findNextUnplannedTask() {
        TodoList list = viewModel.getTodoList().getValue();
        while (currentTaskIndex + 1 < list.getSize()) {
            currentTaskIndex++;
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
        }
        getParentFragmentManager().popBackStack();
    }

    private enum Plan {
        TODAY, TOMORROW, TWODAYS, WEEK, ONEDAY, MIDDAY, AFTERNOON, EVENING, NIGHT
    }

    private class SetDateListener implements View.OnClickListener {
        private Plan plan;

        public SetDateListener(Plan plan) {
            this.plan = plan;
        }

        @Override
        public void onClick(View v) {
            Date date = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            switch (plan) {
                case TOMORROW:
                    c.add(Calendar.DATE, 1);
                    date = c.getTime();
                    break;
            }

            viewModel.setTaskDueDate(currentTaskIndex, date);
            findNextUnplannedTask();
            ConstraintLayout mainPlan = getView().findViewById(R.id.main_plan);
            mainPlan.setVisibility(View.VISIBLE);
            ConstraintLayout todayPlan = getView().findViewById(R.id.today_plan);
            todayPlan.setVisibility(View.GONE);
            ConstraintLayout otherDayPlan = getView().findViewById(R.id.other_day_plan);
            otherDayPlan.setVisibility(View.GONE);
        }
    }
}
