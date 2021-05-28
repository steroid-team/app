package com.github.steroidteam.todolist.model.todo;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A class that represents a to-do list.
 *
 * <p>You need to specify a title for the to-do list and later you will be able to add Task into it.
 * Upon the creation of a to-do list, the Date is saved and reuse to define the ID of the to-do
 * list.
 */
public class TodoList {

    private final UUID id;
    private final List<Task> list;
    private String title;
    private final Date date;
    private final List<UUID> tags;

    /**
     * Constructs a new to-do list.
     *
     * @param title The title of the to-do list.
     * @throws IllegalArgumentException Thrown if the title is empty.
     */
    public TodoList(String title) {
        if (title == null) {
            throw new IllegalArgumentException();
        }
        this.id = UUID.randomUUID();
        this.list = new ArrayList<>();
        this.title = title;
        this.date = new Date(System.currentTimeMillis());
        this.tags = new ArrayList<>();
    }

    public void addTask(Task task) {
        list.add(task);
    }

    public void removeTask(Integer index) {
        if (index >= 0 && index < list.size()) {
            // Need to cast the index to int.
            // Otherwise we use .remove(Object o) and not .remove(int index) as wanted.
            list.remove((int) index);
        }
    }

    public void removeDoneTasks() {
        list.removeIf(Task::isDone);
    }

    public Task getTask(Integer index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        } else {
            return null;
        }
    }

    public void updateTask(int index, Task task) {
        list.set(index, task);
    }

    public int getSize() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Date getDate() {
        return this.date;
    }

    public UUID getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public TodoList setTitle(String newTitle) {
        this.title = newTitle;
        return this;
    }

    public void addTagId(UUID tagId) {
        tags.add(tagId);
    }

    public boolean removeTagId(UUID tagId) {
        return tags.remove(tagId);
    }

    public List<UUID> getTagsIds() {
        return tags;
    }

    public boolean containsTag(UUID tagId) {
        return tags.contains(tagId);
    }

    public TodoList sortByDate() {
        Collections.sort(
                list,
                (task, t1) -> {
                    Date date1 = task.getDueDate();
                    Date date2 = t1.getDueDate();
                    // We use the convention that null > than others
                    if (date1 == null && date2 == null) {
                        return 0;
                    }
                    if (date1 == null && date2 != null) {
                        return 1;
                    }
                    if (date1 != null && date2 == null) {
                        return -1;
                    }

                    return date1.compareTo(date2);
                });
        return this;
    }

    public TodoList sortByDone() {
        Collections.sort(
                list,
                (task, t1) -> {
                    return Boolean.compare(task.isDone(), t1.isDone());
                });
        return this;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Todo-List{");
        for (Task task : this.list) {
            str.append(task.toString()).append(",").append("\n");
        }
        str.append("}");

        return str.toString();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoList todoList = (TodoList) o;
        return todoList.getId().equals(this.getId());
    }
}
