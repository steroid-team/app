package com.github.steroidteam.todolist.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.adapter.TodoArrayListAdapter;
import com.github.steroidteam.todolist.viewmodel.ListSelectionViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.UUID;

public class ListSelectionFragment extends Fragment {

    private TodoArrayListAdapter adapter;
    private ListSelectionViewModel viewModel;
    private Database database;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_list_selection, container, false);

        root.findViewById(R.id.create_note_button).setOnClickListener(this::createList);

        RecyclerView recyclerView = root.findViewById(R.id.activity_list_selection_itemlist);
        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoArrayListAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        TodoRepository repository = new TodoRepository(UUID.randomUUID());
        viewModel = new ListSelectionViewModel(repository);
        viewModel
                .getListOfTodo()
                .observe(
                        getActivity(),
                        (todoListArrayList) -> {
                            adapter.setTodoListCollection(todoListArrayList);
                        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TodoTouchHelper(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    public TodoArrayListAdapter.TodoHolder.TodoCustomListener createCustomListener() {
        return new TodoArrayListAdapter.TodoHolder.TodoCustomListener() {
            @Override
            public void onClickCustom(TodoArrayListAdapter.TodoHolder holder) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("list_id", holder.getTodo().getId());
                Navigation.findNavController(holder.itemView).navigate(R.id.nav_item_view, bundle);
            }
        };
    }

    public void createList(View view) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.Dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.add_todo_suggestion));

        LayoutInflater inflater = this.getLayoutInflater();
        View user_input = inflater.inflate(R.layout.alert_dialog_input, null);

        builder.setView(user_input);

        builder.setPositiveButton(
                getString(R.string.add_todo),
                (DialogInterface dialog, int which) -> {
                    EditText titleInput = user_input.findViewById(R.id.alert_dialog_edit_text);
                    String title = titleInput.getText().toString();
                    if (title.length() > 0) viewModel.addTodo(title);
                    titleInput.getText().clear();
                    dialog.dismiss();
                });
        builder.setNegativeButton(
                getString(R.string.cancel),
                (DialogInterface dialog, int which) -> {
                    dialog.dismiss();
                });
        Dialog dialog = builder.show();
        dialog.getWindow().setGravity(0x00000035);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void removeTodo(UUID toDoListID, final int position) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.Dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.delete_todo_suggestion));

        builder.setPositiveButton(
                getString(R.string.delete),
                (DialogInterface dialog, int which) -> {
                    viewModel.removeTodo(toDoListID);
                    dialog.dismiss();
                });
        builder.setNegativeButton(
                getString(R.string.cancel),
                (DialogInterface dialog, int which) -> {
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                });
        Dialog dialog = builder.show();
        dialog.getWindow().setGravity(0x00000035);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void renameTodo(TodoList todoList, final int position) {

        Context context = new ContextThemeWrapper(getActivity(), R.style.Dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(getString(R.string.rename_todo_suggestion));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_input = inflater.inflate(R.layout.alert_dialog_input, null);
        builder.setView(dialog_input);

        builder.setPositiveButton(
                getString(R.string.rename),
                (DialogInterface dialog, int which) -> {
                    EditText titleInput = dialog_input.findViewById(R.id.alert_dialog_edit_text);
                    String title = titleInput.getText().toString();
                    if (title.length() > 0)
                        viewModel.renameTodo(todoList.getId(), todoList.setTitle(title));
                    titleInput.getText().clear();
                    dialog.dismiss();
                });
        builder.setNegativeButton(
                getString(R.string.cancel),
                (DialogInterface dialog, int which) -> {
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                });
        Dialog dialog = builder.show();
        dialog.getWindow().setGravity(0x00000035);
        dialog.setCanceledOnTouchOutside(false);
    }
}
