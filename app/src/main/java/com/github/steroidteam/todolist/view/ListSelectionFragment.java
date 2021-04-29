package com.github.steroidteam.todolist.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.List;

public class ListSelectionFragment extends Fragment {

    private static todoListAdapter adapter;
    private ArrayList<TodoList> todoLists;
    private Database database;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_list_selection, container, false);

        root.findViewById(R.id.create_note_button).setOnClickListener(this::createList);

        todoLists = new ArrayList<>();
        ListView listView = root.findViewById(R.id.activity_list_selection_itemlist);
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

        return root;
    }

    private void setListViewSettings(ListView listView) {
        listView.setAdapter(adapter);
        listView.setLongClickable(true);

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> true);
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
                        LayoutInflater.from(getActivity())
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
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list_id", todoList.getId());
                        Navigation.findNavController(view).navigate(R.id.nav_item_view, bundle);
                    });

            return convertView;
        }

        private void createRenameAlert(TodoList todoList) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            final EditText input = new EditText(getActivity());
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
