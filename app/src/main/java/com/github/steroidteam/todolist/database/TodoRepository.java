package com.github.steroidteam.todolist.database;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.loader.content.AsyncTaskLoader;


import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.xml.transform.Result;


public class TodoRepository {

    private VolatileDatabase database;
    private MutableLiveData<TodoList> oneTodoList;

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
        this.database.putTodoList(tl);
        id = tl.getId();
        // ========================= END
    }

    public LiveData<TodoList> getTodoList(UUID todoListID) {
        if(oneTodoList==null) {
            oneTodoList = new MutableLiveData<TodoList>(this.database.getTodoList(todoListID));
        }
        return this.oneTodoList;
    }

    public void putTask(UUID todoListID, Task task) {
        new PutTaskAsyncTask(database, oneTodoList).execute(new UUIDTask(todoListID, task));
    }

    public void removeTask(UUID todoListID, int index) {
        new RemoveTaskAsyncTask(database, oneTodoList).execute(new UUIDInt(todoListID, index));
    }

    private static class UUIDTask {
        protected UUID id;
        protected Task task;
        public UUIDTask(UUID id, Task task){
            this.id = id;
            this.task = task;
        }
    }

    private static class PutTaskAsyncTask extends AsyncTask<UUIDTask, Void, Void> {
        private Database database;
        private MutableLiveData<TodoList> oneTodoList;

        private PutTaskAsyncTask(Database database, MutableLiveData<TodoList> oneTodoList) {
            this.database = database;
            this.oneTodoList = oneTodoList;
        }

        @Override
        protected Void doInBackground(UUIDTask... uuidTasks) {
            try {
                database.putTask(uuidTasks[0].id, uuidTasks[0].task);
                oneTodoList.postValue(database.getTodoList(uuidTasks[0].id));
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class UUIDInt {
        protected UUID id;
        protected int index;
        public UUIDInt(UUID id, int index){
            this.id = id;
            this.index = index;
        }
    }

    private static class RemoveTaskAsyncTask extends AsyncTask<UUIDInt, Void, Void> {
        private Database database;
        private MutableLiveData<TodoList> oneTodoList;

        private RemoveTaskAsyncTask(Database database, MutableLiveData<TodoList> oneTodoList) {
            this.database = database;
            this.oneTodoList = oneTodoList;
        }

        @Override
        protected Void doInBackground(UUIDInt... uuidInts) {
            try {
                database.removeTask(uuidInts[0].id, uuidInts[0].index);
                oneTodoList.postValue(database.getTodoList(uuidInts[0].id));
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void renameTask(UUID todoListID, int index, String newText) {
        this.database.renameTask(todoListID, index, newText);
        this.oneTodoList.setValue(this.database.getTodoList(todoListID));
    }
}
