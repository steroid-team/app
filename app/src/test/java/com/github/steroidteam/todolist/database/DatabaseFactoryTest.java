package com.github.steroidteam.todolist.database;

import static org.junit.Assert.assertEquals;

import java.io.File;
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
        assertEquals(databaseMock, DatabaseFactory.getLocalDb(new File("Fake pathname")));
        assertEquals(databaseMock, DatabaseFactory.getRemoteDb());
    }
}
