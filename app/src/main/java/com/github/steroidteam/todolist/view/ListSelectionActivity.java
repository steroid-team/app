package com.github.steroidteam.todolist.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.view.adapter.TodoCollectionAdapter;
import com.github.steroidteam.todolist.viewmodel.ListSelectionViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.UUID;

public class ListSelectionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TodoCollectionAdapter adapter;
    private ListSelectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);

        viewModel = new ListSelectionViewModel(this.getApplication());
        viewModel
                .getListOfTodo()
                .observe(
                        this,
                        (listOfTodo) -> {
                            adapter.setTodoListCollection(listOfTodo);
                        });

        recyclerView = findViewById(R.id.activity_list_selection_itemlist);
        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        TodoCollectionAdapter.TodoHolder.TodoCustomListener customListener =
                new TodoCollectionAdapter.TodoHolder.TodoCustomListener() {
                    @Override
                    public void onClickCustom(TodoCollectionAdapter.TodoHolder holder) {
                        openTodoList(holder);
                    }
                };
        adapter = new TodoCollectionAdapter(customListener);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TodoTouchHelper(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(adapter);
    }

    public void logOut(View view) {
        Activity thisActivity = this;
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> thisActivity.finish());
    }

    public void openTodoList(TodoCollectionAdapter.TodoHolder holder) {
        Intent itemViewActivity = new Intent(ListSelectionActivity.this, ItemViewActivity.class);
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

        LayoutInflater inflater = LayoutInflater.from(this.getApplicationContext());
        View user_input = inflater.inflate(R.layout.alert_dialog_input, null);

        final EditText titleInput = user_input.findViewById(R.id.alert_dialog_edit_text);
        titlePopup.setView(user_input);

        titlePopup.setPositiveButton(
                "Create",
                (DialogInterface dialog, int which) -> {
                    String title = titleInput.getText().toString();
                    if (title.length() > 0) viewModel.addTodo(title);
                    titleInput.getText().clear();
                    dialog.cancel();
                });
        titlePopup.setNegativeButton(
                "Cancel",
                (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                });
        titlePopup.show().getWindow().setGravity(0x00000035);
        ;
    }

    public void removeTodo(UUID toDoListID, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to delete this to-do ?");

        builder.setPositiveButton(
                "Delete",
                (DialogInterface dialog, int which) -> {
                    viewModel.removeTodo(toDoListID);
                    dialog.cancel();
                });
        builder.setNegativeButton(
                "Cancel",
                (DialogInterface dialog, int which) -> {
                    adapter.notifyItemChanged(position);
                    dialog.cancel();
                });
        builder.show().getWindow().setGravity(0x00000035);
    }

    public void renameTodo(UUID toDoListID, final int position) {

        Context context = new ContextThemeWrapper(ListSelectionActivity.this, R.style.Dialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Rename your to-do list");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_input = inflater.inflate(R.layout.alert_dialog_input, null);
        builder.setView(dialog_input);

        builder.setPositiveButton(
                "Rename",
                (DialogInterface dialog, int which) -> {
                    EditText titleInput =
                            (EditText) dialog_input.findViewById(R.id.alert_dialog_edit_text);
                    String title = titleInput.getText().toString();
                    if (title.length() > 0) viewModel.renameTodo(toDoListID, title);
                    titleInput.getText().clear();
                    dialog.dismiss();
                });
        builder.setNegativeButton(
                "Cancel",
                (DialogInterface dialog, int which) -> {
                    adapter.notifyItemChanged(position);
                    dialog.dismiss();
                });
        builder.show().getWindow().setGravity(0x00000035);
    }
}
