package com.github.steroidteam.todolist.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import com.github.steroidteam.todolist.database.TodoListRepository;
import java.util.UUID;

/**
 * This class is used to provide the current instance of the ViewModelFactory.
 *
 * <p>Mainly helpful for testing.
 */
public final class ViewModelFactoryInjection {

    private static TodoListRepository todoListRepository = null;

    private static TodoListRepository getTodoListRepository(Context context) {
        if (todoListRepository == null) {
            return new TodoListRepository(context);
        } else {
            return todoListRepository;
        }
    }

    public static ViewModelFactory getViewModelFactory(Context context) {
        return new ViewModelFactory(getTodoListRepository(context));
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    public static void setCustomTodoListRepo(Context context, UUID id) {
        // NEEDED FOR TEST:
        // when you invoke ItemViewFragment, normally the ListSelectionFragment
        // will tell the repository which to-do list has been chosen.
        todoListRepository = new TodoListRepository(context);
        todoListRepository.selectTodolist(id);
    }
}
