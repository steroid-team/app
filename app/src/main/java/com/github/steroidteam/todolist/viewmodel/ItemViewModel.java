package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.UUID;

public class ItemViewModel extends ViewModel {

    private TodoRepository repository;
    private LiveData<TodoList> todoList;
    private UUID todoListID;

    public ItemViewModel(UUID todoListID) {
        super();
        repository = new TodoRepository();

        // ====== TO DELETE ======== BEGIN
        // We don't have persistent database !
        this.todoListID = repository.id;
        // ========================= END
        // We will do this instead :
        // this.todoListID = todoListIDD

        todoList = repository.getTodoList(this.todoListID);
    }

    public LiveData<TodoList> getTodoList() {
        return this.todoList;
    }

    public void addTask(String body) {
        repository.putTask(todoListID, new Task(body));
    }

    public void removeTask(int index) {
        repository.removeTask(todoListID, index);
    }

    public void renameTask(int index, String newBody) {
        if(!newBody.equals("")) {
            repository.renameTask(todoListID, index, newBody);
        }
    }

    public void setTaskDone(int index, boolean isDone) {
        repository.setTaskDone(todoListID, index, isDone);
    }
}
