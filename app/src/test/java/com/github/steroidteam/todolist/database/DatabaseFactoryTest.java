package com.github.steroidteam.todolist.database;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseFactoryTest {

    @Mock Database databaseMock;

    @Test
    public void returnSettedCustomDatabase() {
        DatabaseFactory.setCustomDatabase(databaseMock);
        assertEquals(databaseMock, DatabaseFactory.getDb());
    }
}
