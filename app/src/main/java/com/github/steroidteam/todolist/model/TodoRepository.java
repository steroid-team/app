package com.github.steroidteam.todolist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.UUID;

public class TodoRepository {

    private VolatileDatabase database;
    private MutableLiveData<TodoList> oneTodoList;
    private MutableLiveData<ArrayList<TodoList>> allTodo;

    // ====== TO DELETE ======== BEGIN
    // We don't have persistent database !
    // So we set a public attribute to gets the ID
    // of the todoList in the item view model.
    public UUID id;
    // ========================= END

    public TodoRepository() {
        this.database = new VolatileDatabase();

        // ====== TO DELETE ======== BEGIN
        // We don't have persistent database !
        // We need to add a todoList,
        // Otherwise we will get a NullPointerException
        // in the getTodoList method.
        TodoList tl = new TodoList("A Todo!");
        Task t1 = new Task("First Task");
        /*
        TodoList tl2 = new TodoList("Homework");
        tl2.addTask(t1);
        TodoList tl3 = new TodoList("Shopping List");
        Task t2 = new Task("Buy Bananas");
        Task t3 = new Task("Buy Cheese");
        tl3.addTask(t2);
        tl3.addTask(t3);
        this.database.putTodoList(tl2);
        this.database.putTodoList(tl3);
         */
        this.database.putTodoList(tl);
        id = tl.getId();
        // ========================= END
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        if (oneTodoList == null) {
            oneTodoList = new MutableLiveData<TodoList>(this.database.getTodoList(todoListID));
        }
        return this.oneTodoList;
    }

    public void putTask(UUID todoListID, Task task) {
        this.database.putTask(todoListID, task);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }

    public void removeTask(UUID todoListID, int index) {
        this.database.removeTask(todoListID, index);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }

    public void renameTask(UUID todoListID, int index, String newText) {
        this.database.renameTask(todoListID, index, newText);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }

    public LiveData<ArrayList<TodoList>> getAllTodo() {
        if (allTodo == null) {
            allTodo = new MutableLiveData<>(this.database.getAllTodo());
        }
        return allTodo;
    }

    public void putTodo(TodoList todoList) {
        this.database.putTodoList(todoList);
        this.allTodo.postValue(this.database.getAllTodo());
    }

    public void removeTodo(UUID id) {
        this.database.removeTodoList(id);
        this.allTodo.postValue(this.database.getAllTodo());
    }

    public void renameTodo(UUID id, String newTitle) {
        this.database.renameTodo(id, newTitle);
        this.allTodo.postValue(this.database.getAllTodo());
    }
}
