package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.broadcast.ReminderDateBroadcast;
import com.github.steroidteam.todolist.broadcast.ReminderLocationBroadcast;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.view.adapter.ParentTaskAdapter;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.view.misc.DateHighlighterTextWatcher;
import com.github.steroidteam.todolist.view.misc.DueDateInputSpan;
import com.github.steroidteam.todolist.view.misc.TagView;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

public class ItemViewFragment extends Fragment {

    private TodoListViewModel viewModel;
    private ParentTaskAdapter adapter;
    public static final int PERMISSIONS_ACCESS_LOCATION = 2;
    private final PrettyTimeParser timeParser = new PrettyTimeParser();
    private ActivityResultLauncher<Intent> calendarExportIntentLauncher;
    private final TagView tagView = new TagView();

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

        adapter = new ParentTaskAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

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

                            adapter.setParentTodoList(todoList);
                        });

        root.findViewById(R.id.new_task_btn).setOnClickListener(this::addTask);
        root.findViewById(R.id.remove_done_tasks_btn).setOnClickListener(this::removeDoneTasks);

        ConstraintLayout updateLayout = root.findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);

        root.findViewById(R.id.itemview_tag_button)
                .setOnClickListener(v -> tagView.tagButton(this, viewModel));
        root.findViewById(R.id.itemview_tag_save_button)
                .setOnClickListener(v -> tagView.tagSaveButton(this));

        ReminderDateBroadcast.createNotificationChannel(getActivity());
        ReminderLocationBroadcast.createLocationNotificationChannel(getActivity());

        calendarExportIntentLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), (result) -> {});

        return root;
    }

    public TodoAdapter.TaskCustomListener createCustomListener() {
        return new TodoAdapter.TaskCustomListener() {
            @Override
            public void onItemClick(final int position) {
                openUpdateLayout(position);
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
        if (dueDate != null) {
            task.setDueDate(dueDate);
            ReminderDateBroadcast.createNotification(dueDate, taskDescription, getActivity());
        }

        return task;
    }

    public void removeTask(final int position) {
        viewModel.removeTask(position);
        Toast.makeText(getContext(), "Successfully removed the task !", Toast.LENGTH_SHORT).show();
    }

    public void removeDoneTasks(View view) {
        viewModel.removeDoneTasks();
        Toast.makeText(
                        getContext(),
                        "Successfully removed all tasks you have done !",
                        Toast.LENGTH_LONG)
                .show();
    }

    public void closeUpdateLayout(View view) {
        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);

        RecyclerView recyclerView = getView().findViewById(R.id.activity_itemview_itemlist);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void openUpdateLayout(final int position) {
        RecyclerView recyclerView = getView().findViewById(R.id.activity_itemview_itemlist);
        recyclerView.setVisibility(View.GONE);

        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.VISIBLE);

        Task thisTask = viewModel.getTask(position);

        EditText userInputBody = getView().findViewById(R.id.layout_update_task_body);
        userInputBody.setText(thisTask.getBody());
        userInputBody.addTextChangedListener(
                new DateHighlighterTextWatcher(getContext(), timeParser));

        CheckBox taskCheckedBox = getView().findViewById(R.id.layout_update_task_checkbox);
        taskCheckedBox.setChecked(thisTask.isDone());

        SaveButtonSetup(userInputBody, position);
        DeleteButtonSetup(position);
        calendarExportButtonSetup(position);

        Button closeButton = getView().findViewById(R.id.layout_update_task_close);
        closeButton.setOnClickListener(this::closeUpdateLayout);
    }

    private void SaveButtonSetup(EditText userInput, final int position) {

        Button saveButton = getView().findViewById(R.id.layout_update_task_save);
        saveButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    Task task = getTaskFromEditable(userInput.getText());

                    if (task == null) return;

                    if (task.getBody() != null) {
                        viewModel.renameTask(position, task.getBody());
                    }
                    if (task.getDueDate() != null) {
                        viewModel.setTaskDueDate(position, task.getDueDate());
                    }
                });
    }

    private void DeleteButtonSetup(final int position) {
        Button deleteButton = getView().findViewById(R.id.layout_update_task_delete);
        deleteButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    removeTask(position);
                });

        Button addLocationButton = getView().findViewById(R.id.AddLocationReminderButton);
        String locationName =
                viewModel.getTodoList().getValue().getTask(position).getLocationName();
        if (locationName != null) addLocationButton.setText(locationName);

        getView()
                .findViewById(R.id.AddLocationReminderButton)
                .setOnClickListener(
                        v -> {
                            getParentFragmentManager()
                                    .setFragmentResultListener(
                                            MapFragment.LOCATION_REQ,
                                            this,
                                            (requestKey, bundle) -> {
                                                viewModel.setTaskLocationReminder(
                                                        position,
                                                        bundle.getParcelable(
                                                                MapFragment.LOCATION_KEY),
                                                        bundle.getString(
                                                                MapFragment.LOCATION_NAME_KEY));
                                                getParentFragmentManager()
                                                        .clearFragmentResultListener(
                                                                MapFragment.LOCATION_REQ);
                                            });

                            Navigation.findNavController(getView()).navigate(R.id.nav_map);
                        });

        Button closeButton = getView().findViewById(R.id.layout_update_task_close);
        closeButton.setOnClickListener(this::closeUpdateLayout);
    }

    private void calendarExportButtonSetup(final int position) {
        Task thisTask = viewModel.getTask(position);

        Button calendarExportButton =
                getView().findViewById(R.id.layout_update_task_export_calendar);
        if (thisTask.getDueDate() != null)
            calendarExportButton.setText(thisTask.getDueDate().toString());
        calendarExportButton.setOnClickListener(
                (v) -> {
                    Calendar startTime = Calendar.getInstance();
                    // If available, use the task's due date for the calendar export. If it has
                    // not been set, it will use the current date.
                    if (thisTask.getDueDate() != null) {
                        startTime.setTime(thisTask.getDueDate());
                    }

                    Intent intent =
                            new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(
                                            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                            startTime.getTimeInMillis())
                                    .putExtra(CalendarContract.Events.TITLE, thisTask.getBody());
                    calendarExportIntentLauncher.launch(intent);
                });
    }

    public void checkBoxTaskListener(final int position, final boolean isChecked) {
        viewModel.setTaskDone(position, isChecked);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull @NotNull String[] permissions,
            @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionGiven = false;
        switch (requestCode) {
            case PERMISSIONS_ACCESS_LOCATION:
                isPermissionGiven = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!isPermissionGiven) {
            Toast.makeText(
                            getContext(),
                            "You must give access to the location to use this feature !",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
