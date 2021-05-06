package com.github.steroidteam.todolist.view;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.broadcast.ReminderDateBroadcast;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.viewmodel.ItemViewModel;
import java.util.UUID;

public class ItemViewFragment extends Fragment {

    private ItemViewModel itemViewModel;
    private TodoAdapter adapter;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_item_view, container, false);

        // Add a click listener to the "back" button to return to the previous activity.
        root.findViewById(R.id.back_button)
                .setOnClickListener(v -> getParentFragmentManager().popBackStack());

        RecyclerView recyclerView = root.findViewById(R.id.activity_itemview_itemlist);
        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        UUID id = (UUID) getArguments().getSerializable(ListSelectionFragment.EXTRA_LIST_KEY);
        TodoRepository repository = new TodoRepository(id);
        itemViewModel = new ItemViewModel(repository);
        itemViewModel
                .getTodoList()
                .observe(
                        getActivity(),
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
        String taskDescription = newTaskET.getText().toString();

        // Make sure that we only add the task if the description has text.
        if (taskDescription.length() > 0) itemViewModel.addTask(taskDescription);

        // Clean the description text box.
        newTaskET.getText().clear();
    }

    public void removeTask(final int position) {
        itemViewModel.removeTask(position);
        Toast.makeText(getContext(), "Successfully removed the task !", Toast.LENGTH_LONG).show();
    }

    public void closeUpdateLayout(View view) {
        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);
    }

    public void openUpdateLayout(TodoAdapter.TaskHolder holder, final int position) {
        ConstraintLayout updateLayout = getView().findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.VISIBLE);

        EditText userInputBody = getView().findViewById(R.id.layout_update_task_body);
        userInputBody.setText(holder.getTaskBody());

        CheckBox taskCheckedBox = getView().findViewById(R.id.layout_update_task_checkbox);
        taskCheckedBox.setChecked(holder.getTaskDone());

        Button saveButton = getView().findViewById(R.id.layout_update_task_save);
        saveButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    itemViewModel.renameTask(position, userInputBody.getText().toString());
                    itemViewModel.setTaskDone(position, taskCheckedBox.isChecked());
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
        itemViewModel.setTaskDone(position, isChecked);
    }
}
