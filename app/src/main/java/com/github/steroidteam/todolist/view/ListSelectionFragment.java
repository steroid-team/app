package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.view.adapter.TodoArrayListAdapter;
import com.github.steroidteam.todolist.view.dialog.DialogListener;
import com.github.steroidteam.todolist.view.dialog.InputDialogFragment;
import com.github.steroidteam.todolist.view.dialog.SimpleDialogFragment;
import com.github.steroidteam.todolist.view.misc.SwipeTouchHelper;
import com.github.steroidteam.todolist.viewmodel.TodoListViewModel;
import com.github.steroidteam.todolist.viewmodel.TodoViewModelFactory;
import com.github.steroidteam.todolist.viewmodel.ViewModelFactoryInjection;
import java.util.UUID;

public class ListSelectionFragment extends Fragment {

    private TodoArrayListAdapter adapter;
    private TodoListViewModel viewModel;

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

        TodoViewModelFactory todoViewModelFactory =
                ViewModelFactoryInjection.getTodoViewModelFactory(getContext());
        viewModel =
                new ViewModelProvider(requireActivity(), todoViewModelFactory)
                        .get(TodoListViewModel.class);
        viewModel
                .getListOfTodo()
                .observe(
                        getViewLifecycleOwner(),
                        (todoListArrayList) -> {
                            adapter.setTodoListCollection(todoListArrayList);
                            adapter.notifyDataSetChanged();
                        });

        createAndSetSwipeListener(recyclerView);

        return root;
    }

    public TodoArrayListAdapter.TodoHolder.TodoCustomListener createCustomListener() {
        return holder -> {
            viewModel.selectTodoList(holder.getTodo().getId());
            Navigation.findNavController(holder.itemView)
                    .navigate(R.id.nav_item_view, new Bundle());
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
