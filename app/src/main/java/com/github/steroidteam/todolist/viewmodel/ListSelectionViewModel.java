package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListSelectionViewModel extends ViewModel {

    private final Database database;
    private final MutableLiveData<List<TodoList>> arrayOfTodoList;

    public ListSelectionViewModel() {
        super();
        this.database = DatabaseFactory.getDb();
        arrayOfTodoList = new MutableLiveData<>(new ArrayList<>());
        this.database.getTodoListCollection().thenAccept(this::setArrayOfTodoList);
    }

    public LiveData<List<TodoList>> getListOfTodo() {
        return this.arrayOfTodoList;
    }

    private void setArrayOfTodoList(TodoListCollection todoListCollection) {
        ArrayList<TodoList> privateArrayList = new ArrayList<>();
        if (todoListCollection.getSize() == 0) {
            arrayOfTodoList.setValue(privateArrayList);
        } else {
            for (int i = 0; i < todoListCollection.getSize(); i++) {
                this.database
                        .getTodoList(todoListCollection.getUUID(i))
                        .thenAccept(
                                todoList -> {
                                    privateArrayList.add(todoList);
                                    arrayOfTodoList.setValue(privateArrayList);
                                });
            }
        }
    }

    public void addTodo(String title) {
        this.database
                .putTodoList(new TodoList(title))
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }

    public void removeTodo(UUID id) {
        this.database
                .removeTodoList(id)
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }

    public void renameTodo(TodoList todoList, String newTitle) {
        this.database
                .updateTodoList(todoList.getId(), todoList.setTitle(newTitle))
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }
}
