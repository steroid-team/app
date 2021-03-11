package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.IdManager;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdManagerTest {

    @Test
    public void simpleIdManagerTest() {
        IdManager manager = new IdManager();
        assertEquals(new Integer(1), manager.getNewId());
    }
}
