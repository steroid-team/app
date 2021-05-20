package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.database.TodoListRepository;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class TodoListViewModel extends ViewModel {

    private final TodoListRepository todoListRepository;

    private final LiveData<ArrayList<TodoList>> allTodoList;
    private final LiveData<TodoList> todoListSelected;

    private UUID selectedTodoList;

    public TodoListViewModel(@NonNull TodoListRepository repository) {
        this.todoListRepository = repository;
        this.todoListSelected = repository.getTodoList();
        this.allTodoList = repository.getAllTodo();
    }

    public void selectTodoList(UUID id) {
        this.selectedTodoList = id;
        todoListRepository.selectTodolist(id);
    }

    public LiveData<ArrayList<TodoList>> getListOfTodo() {
        return this.allTodoList;
    }

    public LiveData<TodoList> getTodoList() {
        return this.todoListSelected;
    }

    public void addTodo(String title) {
        todoListRepository.putTodo(new TodoList(title));
    }

    public void removeTodo(UUID id) {
        todoListRepository.removeTodo(id);
    }

    public void renameTodo(UUID id, TodoList todoListUpdated) {
        todoListRepository.updateTodo(id, todoListUpdated);
    }

    public Task getTask(int index) {
        return todoListSelected.getValue().getTask(index);
    }

    public void addTask(Task task) {
        todoListRepository.putTask(selectedTodoList, task);
    }

    public void removeTask(int index) {
        todoListRepository.removeTask(selectedTodoList, index);
    }

    public void removeDoneTasks() {
        todoListRepository.removeDoneTasks(selectedTodoList);
    }

    public void renameTask(int index, String newBody) {
        if (!newBody.equals("")) {
            Task currentTask = todoListSelected.getValue().getTask(index);
            todoListRepository.updateTask(selectedTodoList, index, currentTask.setBody(newBody));
        }
    }

    public void setTaskDone(int index, boolean isDone) {
        Task currentTask = todoListSelected.getValue().getTask(index);
        todoListRepository.updateTask(selectedTodoList, index, currentTask.setDone(isDone));
    }

    public void setTaskDueDate(int index, Date date) {
        Task currentTask = todoListSelected.getValue().getTask(index);
        todoListRepository.updateTask(selectedTodoList, index, currentTask.setDueDate(date));
    }
}
