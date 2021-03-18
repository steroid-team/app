package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;


public class ItemViewActivity extends AppCompatActivity {
    private static todoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Toolbar toolbar = findViewById(R.id.activity_itemview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        TodoList todoList = new TodoList("Example list");
        setTitle(todoList.getTitle());

        // Pre-populate the database with a few sample tasks.
        todoList.addTask(new Task("Change passwords"));
        todoList.addTask(new Task("Replace old server"));
        todoList.addTask(new Task("Set up firewall"));
        todoList.addTask(new Task("Fix router"));
        todoList.addTask(new Task("Change passwords"));
        todoList.addTask(new Task("Replace old server"));
        todoList.addTask(new Task("Set up firewall"));

        //adapter = new TasksAdapter(this, tasks);
        adapter = new todoAdapter(todoList);
        ListView listView = findViewById(R.id.activity_itemview_itemlist);
        setListViewSettings(listView, todoList);
    }

    private void setListViewSettings(ListView listView, TodoList todoList) {
        listView.setAdapter(adapter);

        listView.setLongClickable(true);
        // The part for deleteTask
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                todoList.removeTask(i);
                adapter.notifyDataSetChanged();
                return true;
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
        if (taskDescription.length() > 0) adapter.add(new Task(taskDescription));

        // Clean the description text box.
        newTaskET.getText().clear();
    }


    private class todoAdapter extends BaseAdapter {

        private final TodoList todoList;

        public todoAdapter(TodoList todoList) {
            this.todoList = todoList;
        }

        public void add(Task task) {
            todoList.addTask(task);
        }

        @Override
        public int getCount() {
            return todoList.getSize();
        }

        @Override
        public Object getItem(int position) {
            return todoList.getTask(position);
        }

        @Override
        public long getItemId(int position) {
            return position; //No need to specify a particular ID, we just return the position.
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Task task = (Task)getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_task_item, parent, false);
            }

            CheckBox taskView = convertView.findViewById(R.id.layout_task_checkbox);
            taskView.setText(task.getBody());

            taskView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task task = (Task) buttonView.getTag();
                    // Notify database?
                }
            });
            taskView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // Notify database?
                    return false;
                }
            });

            return convertView;
        }
    }
}