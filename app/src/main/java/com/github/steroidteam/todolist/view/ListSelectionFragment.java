package com.github.steroidteam.todolist.view;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.database.Repository;
import com.github.steroidteam.todolist.model.TodoArrayRepository;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.adapter.TodoArrayListAdapter;
import com.github.steroidteam.todolist.view.dialog.DialogListener;
import com.github.steroidteam.todolist.view.dialog.InputDialogFragment;
import com.github.steroidteam.todolist.view.dialog.SimpleDialogFragment;
import com.github.steroidteam.todolist.view.misc.TodoTouchHelper;
import com.github.steroidteam.todolist.viewmodel.ListSelectionViewModel;
import java.util.UUID;

public class ListSelectionFragment extends Fragment {

    private TodoArrayListAdapter adapter;
    private ListSelectionViewModel viewModel;

    public static final String EXTRA_LIST_KEY = "list_id";

    public static final String SIMPLE_DIALOG_KEY = "simple_dialog_key";

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_list_selection, container, false);

        root.findViewById(R.id.create_todo_button).setOnClickListener(this::createList);

        RecyclerView recyclerView = root.findViewById(R.id.activity_list_selection_itemlist);
        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new TodoArrayListAdapter(createCustomListener());
        recyclerView.setAdapter(adapter);

        Repository repository = new Repository(getContext());
        viewModel = new ListSelectionViewModel(repository);
        viewModel
                .getListOfTodo()
                .observe(
                        getActivity(),
                        (todoListArrayList) -> {
                            System.err.println(todoListArrayList.size() + "zqddddddddddddddddddddddddddddd");
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
                bundle.putSerializable(EXTRA_LIST_KEY, holder.getTodo().getId());
                Navigation.findNavController(holder.itemView).navigate(R.id.nav_item_view, bundle);
            }
        };
    }

    public void createList(View view) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0) viewModel.addTodo(title);
                    }

                    @Override
                    public void onPositiveClick() {
                        // NEVER CALLED
                    }

                    @Override
                    public void onNegativeClick() {
                        // DO NOTHING
                    }
                };

        DialogFragment newFragment =
                new InputDialogFragment().newInstance(dialogListener, R.string.add_todo_suggestion);
        newFragment.show(getParentFragmentManager(), "add_dialog");
    }

    public void removeTodo(UUID toDoListID, final int position) {

        DialogListener simpleDialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String input) {
                        // NEVER CALLED
                    }

                    @Override
                    public void onPositiveClick() {
                        viewModel.removeTodo(toDoListID);
                    }

                    @Override
                    public void onNegativeClick() {
                        adapter.notifyItemChanged(position);
                    }
                };

        DialogFragment newFragment =
                new SimpleDialogFragment()
                        .newInstance(simpleDialogListener, R.string.delete_todo_suggestion);
        newFragment.show(getParentFragmentManager(), "deletion_dialog");
    }

    public void renameTodo(TodoList todoList, final int position) {

        DialogListener dialogListener =
                new DialogListener() {

                    @Override
                    public void onPositiveClick(String title) {
                        if (title.length() > 0)
                            viewModel.renameTodo(todoList.getId(), todoList.setTitle(title));
                    }

                    @Override
                    public void onPositiveClick() {
                        // NEVER CALLED
                    }

                    @Override
                    public void onNegativeClick() {
                        adapter.notifyItemChanged(position);
                    }
                };

        DialogFragment newFragment =
                new InputDialogFragment()
                        .newInstance(dialogListener, R.string.rename_todo_suggestion);
        newFragment.show(getParentFragmentManager(), "rename_dialog");
    }
}
