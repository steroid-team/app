package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.steroidteam.todolist.util.TodoAdapter;

import java.util.UUID;


public class ItemViewActivity extends AppCompatActivity {

    private ItemViewModel model;
    private TodoAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Toolbar toolbar = findViewById(R.id.activity_itemview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Instantiate the view model.
        // random UUID because we don't have persistent memory !
        // this UUID is not used.
        model = new ItemViewModel(this.getApplication(), UUID.randomUUID());

        // Observe the LiveData todoList from the ViewModel,
        // 'this' refers to the activity so it the ItemViewActivity acts as the LifeCycleOwner,
        model.getTodoList().observe(this, (todoList) -> {
                adapter.setTodoList(todoList);
        });

        recyclerView = findViewById(R.id.activity_itemview_itemlist);
        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        this.adapter = new TodoAdapter(this::updateTaskListener);
        this.recyclerView.setAdapter(this.adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
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

        TodoAdapter.TaskHolder holder = (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        adapter.setUpdateLayoutDisplayed(false);
        if(holder!=null) {
            holder.closeUpdateLayout();
        }
        model.removeTask(position);
        Toast.makeText(getApplicationContext(), "Successfully removed the task !", Toast.LENGTH_LONG).show();

        //displayDeletionConfirmation(position);
    }

    public void updateTask(View view) {
        View parentRow = (View) view.getParent();
        RecyclerView recycler = (RecyclerView) parentRow.getParent();
        final int position = recycler.getChildAdapterPosition(parentRow);

        TodoAdapter.TaskHolder holder = (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        adapter.setUpdateLayoutDisplayed(false);
        if(holder!=null) {
            holder.closeUpdateLayout();
            model.renameTask(position, holder.getUserInput());
        }
    }

    public void updateTaskListener(final int position) {
        TodoAdapter.TaskHolder holder = (TodoAdapter.TaskHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if(holder!=null && !adapter.isUpdateLayoutDisplayed()) {
            adapter.setUpdateLayoutDisplayed(true);
            holder.displayUpdateLayout();
        }
    }

    public void displayDeletionConfirmation(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ItemViewActivity.this);
        builder.setTitle("You are about to delete a task!")
                .setMessage("Are you sure ?")
                .setNegativeButton("No", (dialog, which) -> {
                })
                .setPositiveButton("Yes", (dialog, which) -> {
                    model.removeTask(position);
                    Toast.makeText(getApplicationContext(), "Successfully removed the task !", Toast.LENGTH_LONG).show();
                })
                .create()
                .show();
    }
}