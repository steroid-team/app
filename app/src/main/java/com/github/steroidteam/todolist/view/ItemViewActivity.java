package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.viewmodel.ItemViewModel;
import java.util.UUID;

public class ItemViewActivity extends AppCompatActivity {

    private ItemViewModel viewModel = null;
    private TodoAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        // Add a click listener to the "back" button to return to the previous activity.
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener((view) -> finish());

        // Instantiate the view model.
        Intent intent = getIntent();
        String uuidStr = intent.getStringExtra(ListSelectionActivity.EXTRA_ID_TODO_LIST);
        UUID id = UUID.fromString(uuidStr);

        setViewModel(new ItemViewModel(new TodoRepository(id)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Observe the LiveData todoList from the ViewModel,
        // 'this' refers to the activity so it the ItemViewActivity acts as the LifeCycleOwner,
        viewModel
                .getTodoList()
                .observe(
                        this,
                        (todoList) -> {
                            // Change the activity's title.
                            TextView activityTitle = findViewById(R.id.activity_title);
                            activityTitle.setText(todoList.getTitle());

                            adapter.setTodoList(todoList);
                        });

        recyclerView = findViewById(R.id.activity_itemview_itemlist);
        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        this.adapter = new TodoAdapter(createCustomListener());
        this.recyclerView.setAdapter(this.adapter);
    }

    public TodoAdapter.TaskCustomListener createCustomListener() {
        return new TodoAdapter.TaskCustomListener() {
            @Override
            public void onClickCustom(TodoAdapter.TaskHolder holder, int position) {
                updateTaskListener(holder, position);
            }

            @Override
            public void onCheckedChangedCustom(int position, boolean isChecked) {
                checkBoxTaskListener(position, isChecked);
            }
        };
    }

    public void addTask(View view) {
        EditText newTaskET = (EditText) findViewById(R.id.new_task_text);
        String taskDescription = newTaskET.getText().toString();

        // Make sure that we only add the task if the description has text.
        if (taskDescription.length() > 0) viewModel.addTask(taskDescription);

        // Clean the description text box.
        newTaskET.getText().clear();
    }

    public void removeTask(View view) {
        View parentRow = (View) view.getParent();
        RecyclerView recycler = (RecyclerView) parentRow.getParent();
        final int position = recycler.getChildAdapterPosition(parentRow);

        viewModel.removeTask(position);
        Toast.makeText(
                        getApplicationContext(),
                        "Successfully removed the task !",
                        Toast.LENGTH_LONG)
                .show();
    }

    public void closeUpdateLayout(View view) {
        ConstraintLayout updateLayout = findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);
    }

    public void updateTaskListener(TodoAdapter.TaskHolder holder, final int position) {
        ConstraintLayout updateLayout = findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.VISIBLE);

        EditText userInputBody = findViewById(R.id.layout_update_task_body);
        userInputBody.setText(holder.getTaskBody());

        CheckBox taskCheckedBox = findViewById(R.id.layout_update_task_checkbox);
        taskCheckedBox.setChecked(holder.getTaskDone());

        Button saveButton = findViewById(R.id.layout_update_task_save);
        saveButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    viewModel.renameTask(position, userInputBody.getText().toString());
                    viewModel.setTaskDone(position, taskCheckedBox.isChecked());
                });

        Button deleteButton = findViewById(R.id.layout_update_task_delete);
        deleteButton.setOnClickListener(
                (v) -> {
                    closeUpdateLayout(v);
                    viewModel.removeTask(position);
                    Toast.makeText(
                                    getApplicationContext(),
                                    "Successfully removed the task !",
                                    Toast.LENGTH_LONG)
                            .show();
                });
    }

    public void checkBoxTaskListener(final int position, final boolean isChecked) {
        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        viewModel.setTaskDone(position, isChecked);
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public void setViewModel(ItemViewModel vm) {
        viewModel = vm;
    }
}
