package com.github.steroidteam.todolist.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.viewmodel.ItemViewModel;
import java.util.UUID;

public class ItemViewActivity extends AppCompatActivity {

    private ItemViewModel viewModel;
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
        // random UUID because we don't have persistent memory !
        // this UUID is not used.
        viewModel = new ItemViewModel(UUID.randomUUID());

        // Observe the LiveData todoList from the ViewModel,
        // 'this' refers to the activity so it the ItemViewActivity acts as the LifeCycleOwner,
        viewModel.getTodoList()
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

        this.adapter = new TodoAdapter(new TodoAdapter.TaskCustomListener() {
            @Override
            public void onClickCustom(TodoAdapter.TaskHolder holder, int position) {
                updateTaskListener(holder, position);
            }

            @Override
            public void onCheckedChangedCustom(int position, boolean isChecked) {
                checkBoxTaskListener(position, isChecked);
            }
        });
        this.recyclerView.setAdapter(this.adapter);
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

        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.hideDeleteButton();
        }
        viewModel.removeTask(position);
        Toast.makeText(
                getApplicationContext(),
                "Successfully removed the task !",
                Toast.LENGTH_LONG)
                .show();
    }

    public void closeUpdateLayout(View view) {
        RelativeLayout updateLayout = findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.GONE);
    }

    public void updateTaskListener(TodoAdapter.TaskHolder holder, final int position) {
        RelativeLayout updateLayout = findViewById(R.id.layout_update_task);
        updateLayout.setVisibility(View.VISIBLE);

        EditText userInputBody = findViewById(R.id.layout_update_task_body);
        userInputBody.setText(holder.getTaskBody());

        Button saveButton = findViewById(R.id.layout_update_task_save);
        saveButton.setOnClickListener((v) -> {
            closeUpdateLayout(v);
            viewModel.renameTask(position, userInputBody.getText().toString());
        });
    }

    public void checkBoxTaskListener(final int position, final boolean isChecked) {
        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        viewModel.setTaskDone(position, isChecked);
    }
}
