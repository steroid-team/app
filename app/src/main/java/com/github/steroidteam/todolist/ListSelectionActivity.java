package com.github.steroidteam.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.steroidteam.todolist.todo.TodoList;

import java.util.ArrayList;
import java.util.List;

public class ListSelectionActivity extends AppCompatActivity {
    private static todoListAdapter adapter;
    private ArrayList<TodoList> todoLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);
        todoLists = new ArrayList<>();
        TodoList todoList = new TodoList("Sweng project");
        todoLists.add(todoList);
        todoLists.add(new TodoList("Homework"));
        todoLists.add(new TodoList("Some stuff"));
        setTitle("TODO Lists");

        ListView listView = findViewById(R.id.activity_list_selection_itemlist);
        adapter = new todoListAdapter(todoLists);
        setListViewSettings(listView);
        //Toolbar toolbar = findViewById(R.id.list_selection_toolbar);
        //setSupportActionBar(toolbar);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setListViewSettings(ListView listView) {
        listView.setAdapter(adapter);
        listView.setLongClickable(true);

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            return true;
        });
    }

    public void openList(View view){
        Intent itemViewActivity = new Intent(ListSelectionActivity.this, ItemViewActivity.class);
        startActivity(itemViewActivity);
    }

    private class todoListAdapter extends BaseAdapter{

        private List<TodoList> todoLists;

        public todoListAdapter(List<TodoList> todoLists){
            this.todoLists = todoLists;
        }

        @Override
        public int getCount() {
            return todoLists.size();
        }

        @Override
        public Object getItem(int i) {
            return todoLists.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TodoList todoList = (TodoList) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_todo_list_item, parent, false);
            }

            TextView todoListView = convertView.findViewById(R.id.layout_todo_list_text);
            todoListView.setText(todoList.getTitle());

            todoListView.setOnLongClickListener(view -> {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListSelectionActivity.this);
                final EditText input = new EditText(getBaseContext());
                alert.setTitle("Please enter a new name")
                        .setView(input)
                        .setNegativeButton("Cancel", (dialog, which) -> {})
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            todoList.setTitle(input.getText().toString());
                            Toast.makeText(getApplicationContext(), "Successfully changed the name ! ", Toast.LENGTH_LONG).show();
                        })
                        .create()
                        .show();
                // Notify database?
                return false;
            });

            todoListView.setOnClickListener(view -> {
                Intent itemViewActivity = new Intent(ListSelectionActivity.this, ItemViewActivity.class);
                itemViewActivity.putExtra("title_todo_list",todoList.getTitle());
                startActivity(itemViewActivity);
            });

            return convertView;
        }
    }

}