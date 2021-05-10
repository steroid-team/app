package com.github.steroidteam.todolist.database;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.steroidteam.todolist.filestorage.FirebaseFileStorageService;
import com.github.steroidteam.todolist.filestorage.LocalFileStorageService;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import com.github.steroidteam.todolist.model.todo.TodoListCollection;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Repository {

    private final Database localDatabase;
    private final Database remoteDatabase;

    private final MutableLiveData<List<TodoList>> allTodoLiveData;

    private UUID todoListIDObserved;

    public Repository(Application application) {
        this.localDatabase = new FileStorageDatabase(new LocalFileStorageService(application.getCacheDir(), UserFactory.get()));
        this.remoteDatabase = new FileStorageDatabase(new FirebaseFileStorageService(
                FirebaseStorage.getInstance(), UserFactory.get()));

        allTodoLiveData = new MutableLiveData<>();
        fetchData();
    }

    public LiveData<List<TodoList>> getAllTodo() {
        return this.allTodoLiveData;
    }

    private void fetchData() {

        CompletableFuture<TodoListCollection> localTodoCollection = this.localDatabase.getTodoListCollection();
        // Recover first local data:
        localTodoCollection
        .thenAccept(todoListCollection -> {
            setTodoListMutableLiveData(todoListCollection, this.localDatabase);
        });

        // Then sync local data with remote database:
        syncData();
    }

    private void syncData() {
        this.remoteDatabase.getTodoListCollection()
                .thenCombine(this.localDatabase.getTodoListCollection(), (remoteTodoCollection, localTodoCollection) -> {

                    if(localTodoCollection.isEmpty()) {
                        for(int i=0; i<remoteTodoCollection.getSize(); ++i) {
                            CompletableFuture<TodoList> remoteTodo = this.remoteDatabase.getTodoList(remoteTodoCollection.getUUID(i));
                            remoteTodo.thenAccept(this.localDatabase::putTodoList);
                        }
                    }
                    else if(remoteTodoCollection.isEmpty()) {
                        for(int i=0; i<localTodoCollection.getSize(); ++i) {
                            CompletableFuture<TodoList> localTodo = this.localDatabase.getTodoList(localTodoCollection.getUUID(i));
                            localTodo.thenAccept(this.remoteDatabase::putTodoList);
                        }
                    }
                    else if(remoteTodoCollection.getSize()>localTodoCollection.getSize()) {
                        for (int i = 0; i < remoteTodoCollection.getSize(); ++i) {

                            CompletableFuture<TodoList> remoteTodo = this.remoteDatabase.getTodoList(remoteTodoCollection.getUUID(i));
                            CompletableFuture<TodoList> localTodo = this.localDatabase.getTodoList(localTodoCollection.getUUID(i));

                        }
                    }
                    else if(remoteTodoCollection.getSize()<localTodoCollection.getSize()) {
                        for (int i = 0; i < localTodoCollection.getSize(); ++i) {

                            CompletableFuture<TodoList> remoteTodo = this.remoteDatabase.getTodoList(remoteTodoCollection.getUUID(i));
                            CompletableFuture<TodoList> localTodo = this.localDatabase.getTodoList(localTodoCollection.getUUID(i));

                        }
                    }
                    else {
                        // remoteTodoCollection.getSize() == localTodoCollection.getSize()
                        for (int i = 0; i < remoteTodoCollection.getSize(); ++i) {

                            CompletableFuture<TodoList> remoteTodo = this.remoteDatabase.getTodoList(remoteTodoCollection.getUUID(i));
                            CompletableFuture<TodoList> localTodo = this.localDatabase.getTodoList(localTodoCollection.getUUID(i));

                        }

                        return null;
                    }
                });
    }

    private void setTodoListMutableLiveData(TodoListCollection todoListCollection, Database database) {
        ArrayList<TodoList> privateArrayList = new ArrayList<>();
        System.err.println("ONNNNNNNNNNNNNNNa " + todoListCollection.getSize());
        if (todoListCollection.getSize() == 0) {
            allTodoLiveData.postValue(privateArrayList);
        } else {
            for (int i = 0; i < todoListCollection.getSize(); i++) {
                System.err.println("ZDOQPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP ");
                database
                        .getTodoList(todoListCollection.getUUID(i))
                        .thenAccept(
                                todoList -> {
                                    System.err.println("THIS IS A TODOLIST ! " + todoList.toString());
                                    privateArrayList.add(todoList);
                                    allTodoLiveData.postValue(privateArrayList);
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
