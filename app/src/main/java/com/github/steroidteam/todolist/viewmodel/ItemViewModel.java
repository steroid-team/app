package com.github.steroidteam.todolist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.model.TodoRepository;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.Set;

public class ItemViewModel extends ViewModel {

    private final TodoRepository repository;
    private final LiveData<TodoList> todoList;

    public ItemViewModel(@NonNull TodoRepository repository) {
        super();
        this.repository = repository;
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
        if (!newBody.equals("")) {
            repository.renameTask(index, newBody);
        }
    }

    public void setTaskDone(int index, boolean isDone) {
        repository.setTaskDone(index, isDone);
    }

    public Set<Tag> getTags() {
        // TODO : Change when adding persistence, add method in TodoRepository
        return Tag.getTagsFromListId(todoList.getValue().getId());
    }

    public Tag createTag(String body, int color) {
        // TODO : Change when adding persistence, add method in TodoRepository
        Tag tag = new Tag(body, color, todoList.getValue().getId());
        return tag;
    }
}
