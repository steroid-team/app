package com.github.steroidteam.todolist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

public class ItemViewActivityViewModel extends ViewModel {

    private MutableLiveData<TodoList> todoList;

    public LiveData<TodoList> getTodoList() {
        if (todoList == null) {
            todoList = new MutableLiveData<>();
            loadTodoList();
        }
        return todoList;
    }

    private void loadTodoList() {
        // Here we create a to-do from scratch,
        // But normally we need to recover the data from the database.
        TodoList todo = new TodoList("Example list");

        todo.addTask(new Task("Change passwords"));
        todo.addTask(new Task("Replace old server"));
        todo.addTask(new Task("Set up firewall"));
        todo.addTask(new Task("Fix router"));
        todo.addTask(new Task("Change passwords"));
        todo.addTask(new Task("Replace old server"));
        todo.addTask(new Task("Set up firewall"));

        todoList.setValue(todo);
    }

    public void addTask(String body) {
        //DUMB METHOD TO CHANGE !
        //You do not want to recreate the to-do each time but I did like this
        //just to have a first draft.
        TodoList todo = this.todoList.getValue();
        todo.addTask(new Task(body));
        todoList.setValue(todo);
    }

    public void removeTask(int index) {
        //DUMB METHOD TO CHANGE !
        //Same as addTask()
        TodoList todo = this.todoList.getValue();
        todo.removeTask(index);
        todoList.setValue(todo);
    }
}
