package com.github.steroidteam.todolist.database;

import com.github.steroidteam.todolist.notes.Note;
import com.github.steroidteam.todolist.todo.Task;
import com.github.steroidteam.todolist.todo.TodoList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A simple volatile implementation of the interface database.
 * The database is just a HashMap storing the ID of to-do lists for keys,
 * and objects TodoList for values.
 * It has the same structure for notes.
 */
public class VolatileDatabase implements Database {

    private Map<UUID, TodoList> todoListDB;
    private Map<UUID, Note> noteDB;

    /**
     * Creates a volatile database.
     */
    public VolatileDatabase() {
        this.todoListDB = new HashMap<>();
        this.noteDB = new HashMap<>();
    }

    /**
     * Pushes the to-do list in the database.
     * Updates the to-do list if it's already present in the database
     * (=if there is already a same ID).
     *
     * @param list The to-do list to push.
     */
    @Override
    public void putTodoList(TodoList list) {
        if (list == null) {
            throw new IllegalArgumentException();
        }

        todoListDB.put(list.getId(), list);
    }

    /**
     * Removes the to-do list from the database.
     * Do nothing if the ID is not a key.
     *
     * @param id The id of the to-do list.
     */
    @Override
    public void removeTodoList(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        todoListDB.remove(id);
    }

    /**
     * Gets a to-do list given an ID.
     *
     * @param todoListID The ID of the to-do list.
     * @return The to-do list or null if not present in the database.
     */
    @Override
    public TodoList getTodoList(UUID todoListID) {
        if (todoListID == null) {
            throw new IllegalArgumentException();
        }
        return todoListDB.get(todoListID);
    }

    /**
     * Pushes a task in the database.
     * Assign the correct ID to the task (equivalent to its index in the to-do list).
     * Updates the task if it's already present in the database.
     * Do nothing if the ID is not is the database or if the task is already present in a to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param task       The new task to add.
     */
    @Override
    public void putTask(UUID todoListID, Task task) {
        if (todoListID == null || task == null) {
            throw new IllegalArgumentException();
        }
        //get the to-do list from the database.
        TodoList list = todoListDB.get(todoListID);
        if (list != null) {
            list.addTask(task);
        }
    }

    /**
     * Removes a task from the database.
     * Do nothing if the ID of the to-do is not present in the database,
     * or if the task is not in the to-do list.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex  The index of the task within the to-do list.
     */
    @Override
    public void removeTask(UUID todoListID, Integer taskIndex) {
        if (todoListID == null || taskIndex == null) {
            throw new IllegalArgumentException();
        }
        TodoList list = todoListDB.get(todoListID);
        if (list != null) {
            list.removeTask(taskIndex);
        }
    }

    /**
     * Gets a task given a to-do list ID and a task index.
     *
     * @param todoListID The id of the associated list of the task.
     * @param taskIndex  The index of the task within the to-do list.
     * @return Return null if the to-do list or the task are not present in the database.
     */
    @Override
    public Task getTask(UUID todoListID, Integer taskIndex) {
        if (todoListID == null || taskIndex == null) {
            throw new IllegalArgumentException();
        }
        TodoList list = todoListDB.get(todoListID);
        if (list != null) {
            return list.getTask(taskIndex);
        }
        return null;
    }

    /**
     * Pushes the note in the database.
     * Updates the note if it's already present in the database
     * (=if there is already a same ID).
     *
     * @param note The note to push.
     */
    @Override
    public void putNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException();
        }
        noteDB.put(note.getId(), note);
    }

    /**
     * Removes the note from the database.
     * Do nothing if the ID is not a key.
     *
     * @param id The id of the note.
     */
    @Override
    public void removeNote(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        noteDB.remove(id);
    }

    /**
     * Gets a note given an ID.
     *
     * @param id The ID of the to-do list.
     * @return The note or null if not present in the database.
     */
    @Override
    public Note getNote(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        return noteDB.get(id);
    }

    public ArrayList<Note> getNoteList() {
        return new ArrayList(noteDB.values());
    }
}
