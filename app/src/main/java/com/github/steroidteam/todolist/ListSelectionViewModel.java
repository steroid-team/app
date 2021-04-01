package com.github.steroidteam.todolist;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.github.steroidteam.todolist.database.TodoRepository;
import com.github.steroidteam.todolist.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

public class ListSelectionViewModel extends AndroidViewModel {

    private TodoRepository repository;
    private LiveData<ArrayList<TodoList>> listOfTodo;

    public ListSelectionViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository();

        listOfTodo = repository.getAllTodo();
    }

    public LiveData<ArrayList<TodoList>> getListOfTodo() {
        return this.listOfTodo;
    }

    public void addTodo(String title) {
        repository.putTodo(new TodoList(title));
    }

    public void removeTodo(UUID id) {
        repository.removeTodo(id);
    }

    public void renameTodo(UUID id, String newTitle) {
        repository.renameTodo(id, newTitle);
    }
}
