package com.github.steroidteam.todolist.database;

/**
 * A dumb class to manage the ID of the tasks that are created.
 * It just increment a counter each time there is a request of a new Id.
 */
public class IdManager {

    private Integer index;

    public IdManager() {
        this.index = 0;
    }

    public Integer getNewId() {
        return ++index;
    }

}
