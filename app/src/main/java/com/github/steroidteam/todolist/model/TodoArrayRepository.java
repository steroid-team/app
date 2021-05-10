package com.github.steroidteam.todolist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import java.util.ArrayList;
import java.util.UUID;

public class TodoArrayRepository {
    private final Database database;
    private final MutableLiveData<ArrayList<TodoList>> arrayOfTodoList;
    // private final ArrayList<TodoList> privateArrayList;

    public TodoArrayRepository() {
        this.database = DatabaseFactory.getDb();
        arrayOfTodoList = new MutableLiveData<>(new ArrayList<>());
        this.database.getTodoListCollection().thenAccept(this::setArrayOfTodoList);
    }

    public LiveData<ArrayList<TodoList>> getAllTodo() {
        return this.arrayOfTodoList;
    }

    private void setArrayOfTodoList(TodoListCollection todoListCollection) {
        ArrayList<TodoList> privateArrayList = new ArrayList<>();
        System.err.println("ONNNNNNNNNNNNNNNa " + todoListCollection.getSize());
        if (todoListCollection.getSize() == 0) {
            arrayOfTodoList.postValue(privateArrayList);
        } else {
            for (int i = 0; i < todoListCollection.getSize(); i++) {
                System.err.println("ZDOQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP ");
                this.database
                        .getTodoList(todoListCollection.getUUID(i))
                        .thenAccept(
                                todoList -> {
                                    System.err.println(
                                            "THIS IS A TODOLIST ! " + todoList.toString());
                                    privateArrayList.add(todoList);
                                    arrayOfTodoList.postValue(privateArrayList);
                                });
            }
        }
    }

    public void putTodo(TodoList todoList) {
        this.database
                .putTodoList(todoList)
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }

    public void removeTodo(UUID id) {
        this.database
                .removeTodoList(id)
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }

    public void renameTodo(UUID id, TodoList todoListUpdated) {
        this.database
                .updateTodoList(id, todoListUpdated)
                .thenCompose(str -> this.database.getTodoListCollection())
                .thenAccept(this::setArrayOfTodoList);
    }
}
