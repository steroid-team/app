package com.github.steroidteam.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.todo.TodoList;
import com.github.steroidteam.todolist.util.TodoCollectionAdapter;

import java.util.ArrayList;

public class ListSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoCollectionAdapter adapter;
    private ListSelectionViewModel viewModel;


    private ArrayList<TodoList> todoLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);

        Toolbar toolbar = findViewById(R.id.list_selection_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        viewModel = new ListSelectionViewModel(this.getApplication());
        viewModel.getListOfTodo().observe(this, (listOfTodo) -> {
            adapter.setTodoListCollection(listOfTodo);
        });

        recyclerView = findViewById(R.id.activity_list_selection_itemlist);
        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoCollectionAdapter();
        recyclerView.setAdapter(adapter);
    }

    public void logOut(View view) {
        Activity thisActivity = this;
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> thisActivity.finish());
    }

    public void openTodoList(View view) {
        View parentRow = (View) view.getParent();
        final int position = recyclerView.getChildAdapterPosition(parentRow);

        TodoCollectionAdapter.TodoHolder holder =
                (TodoCollectionAdapter.TodoHolder) recyclerView.findViewHolderForAdapterPosition(position);

        Intent itemViewActivity =
                new Intent(ListSelectionActivity.this, ItemViewActivity.class);
        itemViewActivity.putExtra("id_todo_list", holder.getIdOfTodo());
        startActivity(itemViewActivity);
    }

    public void openNotes(View view) {
        Intent noteSelectionActivity =
                new Intent(ListSelectionActivity.this, NoteSelectionActivity.class);
        startActivity(noteSelectionActivity);
    }

    public void addTodoList(View view) {
        AlertDialog.Builder titlePopup = new AlertDialog.Builder(this);
        titlePopup.setTitle("Enter the title of your to-do list");
        final EditText titleInput = new EditText(this);
        titleInput.setInputType(InputType.TYPE_CLASS_TEXT);
        titlePopup.setView(titleInput);
        titlePopup.setPositiveButton("Create", (DialogInterface dialog, int which) -> {
                String title = titleInput.getText().toString();
                if(title.length() > 0) viewModel.addTodo(title);
                titleInput.getText().clear();
                dialog.cancel();
        });
        titlePopup.setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {
                dialog.cancel();
        });
        titlePopup.show();
    }
}
