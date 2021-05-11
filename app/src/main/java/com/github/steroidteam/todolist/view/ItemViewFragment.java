package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.broadcast.ReminderDateBroadcast;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.view.misc.DateHighlighterTextWatcher;
import com.github.steroidteam.todolist.view.misc.DueDateInputSpan;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.util.Date;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class ItemViewFragment extends Fragment {

    private TodoListViewModel viewModel;
    private TodoAdapter adapter;
    private final PrettyTimeParser timeParser = new PrettyTimeParser();

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_item_view, container, false);

        // Add a click listener to the "back" button to return to the previous activity.
        root.findViewById(R.id.back_button)
                .setOnClickListener(v -> getParentFragmentManager().popBackStack());

        EditText newTaskText = root.findViewById(R.id.new_task_text);
        newTaskText.addTextChangedListener(
                new DateHighlighterTextWatcher(getContext(), timeParser));

        RecyclerView recyclerView = root.findViewById(R.id.activity_itemview_itemlist);
        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        ViewModelFactory viewModelFactory =
                ViewModelFactoryInjection.getViewModelFactory(getContext());
        viewModel =
                new ViewModelProvider(requireActivity(), viewModelFactory)
                        .get(TodoListViewModel.class);

        viewModel
                .getTodoList()
                .observe(
                        getViewLifecycleOwner(),
                        (todoList) -> {
                            TextView activityTitle = root.findViewById(R.id.activity_title);
                            activityTitle.setText(todoList.getTitle());

                            adapter.setTodoList(todoList);
                        });

        recyclerView = root.findViewById(R.id.activity_itemview_itemlist);
        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        root.findViewById(R.id.new_task_btn).setOnClickListener(this::addTask);

        ReminderDateBroadcast.createNotificationChannel(getActivity());

        return root;
    }

    public TodoAdapter.TaskCustomListener createCustomListener() {
        return new TodoAdapter.TaskCustomListener() {
            @Override
            public void onItemClick(TodoAdapter.TaskHolder holder, final int position) {
                openUpdateLayout(holder, position);
            }

            public void onItemDelete(final int position) {
                removeTask(position);
            }

            @Override
            public void onCheckedChangedCustom(int position, boolean isChecked) {
                checkBoxTaskListener(position, isChecked);
            }
        };
    }

    public void addTask(View view) {
        EditText newTaskET = getView().findViewById(R.id.new_task_text);

        Task task = getTaskFromEditable(newTaskET.getText());
        if (task == null) return;

        viewModel.addTask(task);

        // Clean the description text box.
        newTaskET.getText().clear();
    }

    private Task getTaskFromEditable(Editable editable) {
        String taskDescription = editable.toString();

        DueDateInputSpan[] dueDateInputSpans =
                editable.getSpans(0, editable.length(), DueDateInputSpan.class);

        Date dueDate = null;
        if (dueDateInputSpans.length > 0) {
            // There should only be one span, as we make sure that only the last date in the
            // task's body has one.
            DueDateInputSpan span = dueDateInputSpans[0];
            dueDate = span.getDate();

            // Remove one leading/trailing space, to there are no two contiguous spaces after
            // removing the date.
            int start = editable.getSpanStart(span);
            int end = editable.getSpanEnd(span);
            if (start > 0 && taskDescription.charAt(start - 1) == ' ') start--;
            else if (end < taskDescription.length() - 1 && taskDescription.charAt(end) == ' ')
                end++;

            StringBuilder sb = new StringBuilder(taskDescription);
            sb.delete(start, end);
            taskDescription = sb.toString();
        }

        // Make sure that we only return a task if the description has actual text.
        if (taskDescription.length() == 0) return null;

        Task task = new Task(taskDescription);
        if (dueDate != null) task.setDueDate(dueDate);
        return task;
    }

    public void removeTask(final int position) {
        viewModel.removeTask(position);
        Toast.makeText(getContext(), "Successfully removed the task !", Toast.LENGTH_LONG).show();
    }

    public void closeUpdateLayout(View view) {
        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);

        RecyclerView recyclerView = getView().findViewById(R.id.activity_itemview_itemlist);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void openUpdateLayout(TodoAdapter.TaskHolder holder, final int position) {
        RecyclerView recyclerView = getView().findViewById(R.id.activity_itemview_itemlist);
        recyclerView.setVisibility(View.GONE);

        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.VISIBLE);

        EditText userInputBody = getView().findViewById(R.id.layout_update_task_body);
        userInputBody.setText(holder.getTaskBody());
        userInputBody.addTextChangedListener(
                new DateHighlighterTextWatcher(getContext(), timeParser));

        CheckBox taskCheckedBox = getView().findViewById(R.id.layout_update_task_checkbox);
        taskCheckedBox.setChecked(holder.getTaskDone());

        Button saveButton = getView().findViewById(R.id.layout_update_task_save);
        saveButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    Task task = getTaskFromEditable(userInputBody.getText());

                    if (task == null) return;

                    if (task.getBody() != null) {
                        viewModel.renameTask(position, task.getBody());
                    }
                    if (task.getDueDate() != null) {
                        viewModel.setTaskDueDate(position, task.getDueDate());
                    }
                });

        Button deleteButton = getView().findViewById(R.id.layout_update_task_delete);
        deleteButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    removeTask(position);
                });

        Button closeButton = getView().findViewById(R.id.layout_update_task_close);
        closeButton.setOnClickListener(this::closeUpdateLayout);
    }

    public void checkBoxTaskListener(final int position, final boolean isChecked) {
        viewModel.setTaskDone(position, isChecked);
    }
}
