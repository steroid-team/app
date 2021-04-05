package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.view.adapter.TodoAdapter;
import com.github.steroidteam.todolist.viewmodel.ItemViewModel;
import java.util.UUID;

public class ItemViewActivity extends AppCompatActivity {

    private ItemViewModel model;
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
        model = new ItemViewModel(this.getApplication(), UUID.randomUUID());

        UUID todo_list_id = (UUID) getIntent().getSerializableExtra("id_todo_list");
        if (todo_list_id != null) {
            model = new ItemViewModel(this.getApplication(), todo_list_id);
        } else {
            // Instantiate the view model.
            // random UUID because we don't have persistent memory !
            // this UUID is not used.
            model = new ItemViewModel(this.getApplication(), UUID.randomUUID());
        }
        // Observe the LiveData todoList from the ViewModel,
        // 'this' refers to the activity so it the ItemViewActivity acts as the LifeCycleOwner,
        model.getTodoList()
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

        this.adapter = new TodoAdapter(this::updateTaskListener);
        this.recyclerView.setAdapter(this.adapter);
    }

    public void addTask(View view) {
        EditText newTaskET = (EditText) findViewById(R.id.new_task_text);
        String taskDescription = newTaskET.getText().toString();

        // Make sure that we only add the task if the description has text.
        if (taskDescription.length() > 0) model.addTask(taskDescription);

        // Clean the description text box.
        newTaskET.getText().clear();
    }

    public void removeTask(View view) {
        View parentRow = (View) view.getParent();
        RecyclerView recycler = (RecyclerView) parentRow.getParent();
        final int position = recycler.getChildAdapterPosition(parentRow);

        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        adapter.setCurrentlyDisplayedUpdateLayoutPos(null);
        if (holder != null) {
            holder.closeUpdateLayout();
        }
        model.removeTask(position);
        Toast.makeText(
                        getApplicationContext(),
                        "Successfully removed the task !",
                        Toast.LENGTH_LONG)
                .show();
    }

    public void updateTask(View view) {
        View parentRow = (View) view.getParent();
        RecyclerView recycler = (RecyclerView) parentRow.getParent();
        final int position = recycler.getChildAdapterPosition(parentRow);

        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        adapter.setCurrentlyDisplayedUpdateLayoutPos(null);
        if (holder != null) {
            holder.closeUpdateLayout();
            model.renameTask(position, holder.getUserInput());
        }
    }

    public void updateTaskListener(final int position) {
        TodoAdapter.TaskHolder holder =
                (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        Integer currentlyDisplayed = adapter.getCurrentlyDisplayedUpdateLayoutPos();

        if (currentlyDisplayed != null) {
            TodoAdapter.TaskHolder currentHolder =
                    (TodoAdapter.TaskHolder)
                            recyclerView.findViewHolderForAdapterPosition(currentlyDisplayed);
            currentHolder.closeUpdateLayout();
            adapter.setCurrentlyDisplayedUpdateLayoutPos(null);
        }
        if (holder != null) {
            adapter.setCurrentlyDisplayedUpdateLayoutPos(position);
            holder.displayUpdateLayout();
        }
    }
}
