package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;
import com.github.steroidteam.todolist.util.TodoAdapter;

import java.util.UUID;


public class ItemViewActivity extends AppCompatActivity {

    private ItemViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Toolbar toolbar = findViewById(R.id.activity_itemview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.activity_itemview_itemlist);

        // The layout manager takes care of displaying the task below each other
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setHasFixedSize(true);

        final TodoAdapter adapter = new TodoAdapter();
        recyclerView.setAdapter(adapter);

        //Instantiate the view model.
        // random UUID because we don't have persistent memory !
        // this UUID is not used.
        model = new ItemViewModel(this.getApplication(), UUID.randomUUID());

        // Observe the LiveData todoList from the ViewModel,
        // 'this' refers to the activity so it the ItemViewActivity acts as the LifeCycleOwner,
        // and use a lambda for the observer.

        model.getTodoList().observe(this, new Observer<TodoList>() {
            @Override
            public void onChanged(TodoList todoList) {
                // Update the adapter
                adapter.setTodoList(todoList);
            }
        });
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
}