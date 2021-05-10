package com.github.steroidteam.todolist.database;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TodoListRepository {

    private final Database localDatabase;
    private final Database remoteDatabase;

    private final MutableLiveData<ArrayList<TodoList>> allTodoLiveData;
    private final MutableLiveData<TodoList> observedTodoList;

    private UUID observedTodoListID;

    public TodoListRepository(Context context) {
        this.localDatabase = new FileStorageDatabase(new LocalFileStorageService(context.getCacheDir(), UserFactory.get()));
        this.remoteDatabase = new FileStorageDatabase(new FirebaseFileStorageService(
                FirebaseStorage.getInstance(), UserFactory.get()));

        allTodoLiveData = new MutableLiveData<>();
        observedTodoList = new MutableLiveData<>();

        fetchData();
    }

    public void selectTodolist(UUID id) {
        this.observedTodoListID=id;
        this.localDatabase.getTodoList(id)
                .thenAccept(this.observedTodoList::postValue);
    }

    public LiveData<ArrayList<TodoList>> getAllTodo() {
        return this.allTodoLiveData;
    }

    public LiveData<TodoList> getTodoList() { return this.observedTodoList;}

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

                    // CHECK THAT ALL REMOTE TO-DO WILL BE IN THE LOCAL DATABASE
                    for(int i=0; i<remoteTodoCollection.getSize(); ++i) {
                        UUID currentRemoteID = remoteTodoCollection.getUUID(i);
                        if(localTodoCollection.contains(currentRemoteID)) {
                           // The to-do list is present in the local and remote file system.
                           // We need to check which copy to keep:
                            checkLastModified(currentRemoteID);
                        } else {
                            // The to-do list is not present in the local file system.
                            // We need to add it:
                            this.remoteDatabase.getTodoList(currentRemoteID)
                                    .thenAccept(this.localDatabase::putTodoList);
                        }
                    }

                    // REMOVE ALL TO-DO THAT ARE NOT IN THE REMOTE DATABASE
                    for(int i=0; i<localTodoCollection.getSize(); ++i) {
                        UUID currentLocalID = localTodoCollection.getUUID(i);
                        if(!remoteTodoCollection.contains(currentLocalID)) {
                            this.localDatabase.removeTodoList(currentLocalID);
                        }
                    }
                    return null;
                });
    }

    private void checkLastModified(UUID id) {
        remoteDatabase.getLastModifiedTimeTodo(id)
                .thenCombine(localDatabase.getLastModifiedTimeTodo(id),
                        (remoteTime, localTime) -> {
                            if(remoteTime>localTime) {
                                // The remote copy has been updated more recently than the local one.
                                // So we stored the remote copy in the local database:
                                this.remoteDatabase.getTodoList(id)
                                        .thenAccept(this.localDatabase::putTodoList);
                            }
                            else {
                                // The local copy has been updated more recently than the remote one.
                                // So we stored the local copy in the remote database:
                                this.localDatabase.getTodoList(id)
                                        .thenAccept(this.remoteDatabase::putTodoList);
                            }
                            return null;
                        });
    }

    private void setTodoListMutableLiveData(TodoListCollection todoListCollection, Database database) {
        ArrayList<TodoList> privateArrayList = new ArrayList<>();
        if (todoListCollection.getSize() == 0) {
            allTodoLiveData.postValue(privateArrayList);
        } else {
            for (int i = 0; i < todoListCollection.getSize(); i++) {
                database
                        .getTodoList(todoListCollection.getUUID(i))
                        .thenAccept(
                                todoList -> {
                                    privateArrayList.add(todoList);
                                    allTodoLiveData.postValue(privateArrayList);
                                });
            }
        }
    }

    public void putTodo(TodoList todoList) {
        this.localDatabase
                .putTodoList(todoList)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(todoListCollection -> {setTodoListMutableLiveData(todoListCollection, localDatabase);});

        this.remoteDatabase.putTodoList(todoList);
    }

    public void removeTodo(UUID id) {
        this.localDatabase
                .removeTodoList(id)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(todoListCollection -> {setTodoListMutableLiveData(todoListCollection, localDatabase);});

        this.remoteDatabase.removeTodoList(id);
    }

    public void renameTodo(UUID id, TodoList todoListUpdated) {
        this.localDatabase
                .updateTodoList(id, todoListUpdated)
                .thenCompose(str -> this.localDatabase.getTodoListCollection())
                .thenAccept(todoListCollection -> {setTodoListMutableLiveData(todoListCollection, localDatabase);});

        this.remoteDatabase.updateTodoList(id, todoListUpdated);
    }

    public void putTask(Task task) {
        this.localDatabase
                .putTask(observedTodoListID, task)
                .thenCompose(str -> this.localDatabase.getTodoList(observedTodoListID))
                .thenAccept(this.observedTodoList::postValue);
    }

    public void removeTask(int index) {
        this.localDatabase
                .removeTask(observedTodoListID, index)
                .thenCompose(str -> this.localDatabase.getTodoList(observedTodoListID))
                .thenAccept(this.observedTodoList::postValue);
    }

    public void renameTask(int index, String newText) {
        this.localDatabase
                .getTask(observedTodoListID, index)
                .thenCompose(
                        task -> {
                            task.setBody(newText);
                            return this.localDatabase.updateTask(observedTodoListID, index, task);
                        })
                .thenCompose(task -> this.localDatabase.getTodoList(observedTodoListID))
                .thenAccept(this.observedTodoList::postValue);
    }

    public void setTaskDone(int index, boolean isDone) {
        this.localDatabase
                .getTask(observedTodoListID, index)
                .thenCompose(
                        task -> {
                            task.setDone(isDone);
                            return this.localDatabase.updateTask(observedTodoListID, index, task);
                        })
                .thenCompose(task -> this.localDatabase.getTodoList(observedTodoListID))
                .thenAccept(this.observedTodoList::postValue);
    }

    public void setTaskDueDate(int index, Date dueDate) {
        this.localDatabase
                .getTask(observedTodoListID, index)
                .thenCompose(
                        task -> {
                            task.setDueDate(dueDate);
                            return this.localDatabase.updateTask(observedTodoListID, index, task);
                        })
                .thenCompose(task -> this.localDatabase.getTodoList(observedTodoListID))
                .thenAccept(this.observedTodoList::setValue);
        this.localDatabase.getTodoList(observedTodoListID).thenAccept(this.observedTodoList::postValue);
    }
}
