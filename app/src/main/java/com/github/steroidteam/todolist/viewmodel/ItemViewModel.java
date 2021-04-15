package com.github.steroidteam.todolist.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.UUID;

public class ItemViewModel extends AndroidViewModel {

    private final TodoRepository repository;
    private final LiveData<TodoList> todoList;

    public ItemViewModel(@NonNull Application application, UUID todoListID) {
        super(application);
        repository = new TodoRepository(todoListID);
        todoList = repository.getTodoList();
    }

    public LiveData<TodoList> getTodoList() {
        return this.todoList;
    }

    public void addTask(String body) {
        repository.putTask(new Task(body));
    }

    public void removeTask(int index) {
        repository.removeTask(index);
    }

    public void renameTask(int index, String newBody) {
        repository.renameTask(index, newBody);
    }
}
