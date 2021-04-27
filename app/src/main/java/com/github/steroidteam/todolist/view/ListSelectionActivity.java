package com.github.steroidteam.todolist.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.List;

public class ListSelectionActivity extends AppCompatActivity {

    public static final String EXTRA_ID_TODO_LIST = "id_todo_list";

    private static todoListAdapter adapter;
    private ArrayList<TodoList> todoLists;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);

        todoLists = new ArrayList<>();

        setTitle("TODO Lists");
        ListView listView = findViewById(R.id.activity_list_selection_itemlist);
        adapter = new todoListAdapter(todoLists);
        setListViewSettings(listView);

        database = DatabaseFactory.getDb();

        database.getTodoListCollection()
                .thenAccept(
                        (todoListCollection -> {
                            for (int i = 0; i < todoListCollection.getSize(); i++) {
                                database.getTodoList(todoListCollection.getUUID(i))
                                        .thenAccept(
                                                todoList -> {
                                                    todoLists.add(todoList);
                                                    adapter.notifyDataSetChanged();
                                                });
                            }
                        }));
    }

    private void setListViewSettings(ListView listView) {
        listView.setAdapter(adapter);
        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(
                (adapterView, view, i, l) -> {
                    return true;
                });
    }

    public void logOut(View view) {
        Activity thisActivity = this;
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> thisActivity.finish());
    }

    public void openList(View view) {
        Intent itemViewActivity = new Intent(ListSelectionActivity.this, ItemViewActivity.class);
        startActivity(itemViewActivity);
    }

    public void openNotes(View view) {
        Intent noteSelectionActivity =
                new Intent(ListSelectionActivity.this, NoteSelectionActivity.class);
        startActivity(noteSelectionActivity);
    }

    public void openDrawing(View view) {
        Intent drawingActivity = new Intent(ListSelectionActivity.this, DrawingActivity.class);
        startActivity(drawingActivity);
    }

    public void createList(View view) {
        TodoList todoList = new TodoList(getString(R.string.new_todolist_title));
        database.putTodoList(todoList)
                .thenAccept(
                        filePath -> {
                            todoLists.add(todoList);
                            adapter.notifyDataSetChanged();
                        });
    }

    private class todoListAdapter extends BaseAdapter {

        private List<TodoList> todoLists;

        public todoListAdapter(List<TodoList> todoLists) {
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
            return todoLists.get(i).getId().getLeastSignificantBits();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TodoList todoList = (TodoList) getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView =
                        LayoutInflater.from(getBaseContext())
                                .inflate(R.layout.layout_todo_list_item, parent, false);
            }

            TextView todoListViewTitle = convertView.findViewById(R.id.layout_todo_list_text);
            todoListViewTitle.setText(todoList.getTitle());

            ConstraintLayout todoListView = convertView.findViewById(R.id.layout_todo_list);
            todoListView.setOnLongClickListener(
                    view -> {
                        createRenameAlert(todoList);
                        return false;
                    });

            todoListView.setOnClickListener(
                    view -> {
                        Intent itemViewActivity =
                                new Intent(ListSelectionActivity.this, ItemViewActivity.class);
                        itemViewActivity.putExtra(EXTRA_ID_TODO_LIST, todoList.getId().toString());
                        startActivity(itemViewActivity);
                    });

            return convertView;
        }

        private void createRenameAlert(TodoList todoList) {
            AlertDialog.Builder alert = new AlertDialog.Builder(ListSelectionActivity.this);
            final EditText input = new EditText(getBaseContext());
            input.setSingleLine(true);
            input.setText(todoList.getTitle());
            alert.setTitle("Please enter a new name")
                    .setView(input)
                    .setNegativeButton("Cancel", (dialog, which) -> {})
                    .setPositiveButton(
                            "Confirm",
                            (dialog, which) -> {
                                if (input.getText().length() <= 0) {
                                    return;
                                }
                                todoList.setTitle(input.getText().toString());
                                database.updateTodoList(todoList.getId(), todoList);
                            })
                    .create()
                    .show();
        }
    }
}
