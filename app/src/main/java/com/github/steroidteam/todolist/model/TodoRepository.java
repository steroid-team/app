package com.github.steroidteam.todolist.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.steroidteam.todolist.database.Database;
import com.github.steroidteam.todolist.database.DatabaseFactory;
import com.github.steroidteam.todolist.model.todo.Tag;
import com.github.steroidteam.todolist.model.todo.Task;
import com.github.steroidteam.todolist.model.todo.TodoList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TodoRepository {
    private final Database database;
    private final MutableLiveData<TodoList> oneTodoList;
    private final UUID todoListID;
    private final MutableLiveData<List<Tag>> listTags;

    public TodoRepository(UUID todoListID) {
        this.database = DatabaseFactory.getDb();
        oneTodoList = new MutableLiveData<>();
        this.todoListID = todoListID;
        this.oneTodoList.setValue(new TodoList("Placeholder"));
        this.database.getTodoList(todoListID).thenAccept(this.oneTodoList::setValue);
        listTags = new MutableLiveData<>();
        listTags.setValue(new ArrayList<>());
        setTagsList();
    }

    public LiveData<TodoList> getTodoList() {
        return this.oneTodoList;
    }

    public void putTask(Task task) {
        this.database
                .putTask(todoListID, task)
                .thenCompose(str -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void removeTask(int index) {
        this.database
                .removeTask(todoListID, index)
                .thenCompose(str -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void renameTask(int index, String newText) {
        this.database
                .getTask(todoListID, index)
                .thenCompose(
                        task -> {
                            task.setBody(newText);
                            return this.database.updateTask(todoListID, index, task);
                        })
                .thenCompose(task -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void setTaskDone(int index, boolean isDone) {
        this.database
                .getTask(todoListID, index)
                .thenCompose(
                        task -> {
                            task.setDone(isDone);
                            return this.database.updateTask(todoListID, index, task);
                        })
                .thenCompose(task -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
    }

    public void setTaskDueDate(int index, Date dueDate) {
        this.database
                .getTask(todoListID, index)
                .thenCompose(
                        task -> {
                            task.setDueDate(dueDate);
                            return this.database.updateTask(todoListID, index, task);
                        })
                .thenCompose(task -> this.database.getTodoList(todoListID))
                .thenAccept(this.oneTodoList::setValue);
        this.database.getTodoList(todoListID).thenAccept(this.oneTodoList::setValue);
    }

    private void setTagsList() {
        List<UUID> tagsIds = oneTodoList.getValue().getTagsIds();
        System.out.println("Tags IDs : " + tagsIds);
        database.getTagsFromIds(tagsIds).thenAccept(tagsList -> listTags.setValue(tagsList));
        System.out.println("rep tags list : " + listTags.getValue());
    }

    public List<Tag> getTags() {
        setTagsList();
        return listTags.getValue();
    }

    public void putTag(Tag tag) {
        database.putTag(tag)
                .thenCompose(t -> database.putTagInList(todoListID, t.getId()))
                .thenCompose(str -> database.getTodoList(todoListID))
                .thenAccept(oneTodoList::setValue);
    }

    public void destroyTag(Tag tag) {
        database.removeTag(tag.getId()).thenAccept(str -> setTagsList());
    }
}
