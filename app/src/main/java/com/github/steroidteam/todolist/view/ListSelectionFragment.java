package com.github.steroidteam.todolist.view;

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
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.util.SwipeTouchHelper;
import com.github.steroidteam.todolist.view.adapter.TodoArrayListAdapter;
import com.github.steroidteam.todolist.view.dialog.DialogListener;
import com.github.steroidteam.todolist.view.dialog.InputDialogFragment;
import com.github.steroidteam.todolist.view.dialog.SimpleDialogFragment;
import com.github.steroidteam.todolist.viewmodel.ListSelectionViewModel;
import java.util.UUID;

public class ListSelectionFragment extends Fragment {

    private ListSelectionViewModel viewModel;
    private TodoArrayListAdapter adapter;

    public static final String EXTRA_LIST_KEY = "list_id";

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

        viewModel = new ListSelectionViewModel();
        viewModel
                .getListOfTodo()
                .observe(
                        getActivity(),
                        (todoListArrayList) -> {
                            adapter.setTodoListCollection(todoListArrayList);
                            adapter.notifyDataSetChanged();
                        });

        createAndSetSwipeListener(recyclerView);

        return root;
    }

    private TodoArrayListAdapter.TodoHolder.TodoCustomListener createCustomListener() {
        return holder -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_LIST_KEY, holder.getTodo().getId());
            Navigation.findNavController(holder.itemView).navigate(R.id.nav_item_view, bundle);
        };
    }

    private void createAndSetSwipeListener(RecyclerView recyclerView) {
        SwipeTouchHelper.SwipeListener swipeListener =
                new SwipeTouchHelper.SwipeListener() {
                    @Override
                    public void onSwipeLeft(RecyclerView.ViewHolder viewHolder, int position) {
                        removeTodo(
                                ((TodoArrayListAdapter.TodoHolder) viewHolder).getTodo().getId(),
                                position);
                    }

                    @Override
                    public void onSwipeRight(RecyclerView.ViewHolder viewHolder, int position) {
                        renameTodo(
                                ((TodoArrayListAdapter.TodoHolder) viewHolder).getTodo(), position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeTouchHelper(swipeListener));
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
                        if (title.length() > 0) viewModel.renameTodo(todoList, title);
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
