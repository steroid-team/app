package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.DatabaseException;
import com.github.steroidteam.todolist.database.VolatileDatabase;

import org.junit.Test;

import static org.junit.Assert.*;

public class VolatileDatabaseTest {

    @Test
    public void someSimpleTest() {
        String k1 = "key1";
        String k2 = "key2";
        String v1 = "value1";
        String v2 = "value2";


        VolatileDatabase db = new VolatileDatabase();

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase db2 = new VolatileDatabase();
            db2.put(null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase db2 = new VolatileDatabase();
            db2.put("null", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase db2 = new VolatileDatabase();
            db2.get(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            VolatileDatabase db2 = new VolatileDatabase();
            db2.remove(null);
        });

        assertThrows(DatabaseException.class, () -> {
            VolatileDatabase db2 = new VolatileDatabase();
            db2.remove("Not a key");
        });

        assertEquals(null, db.get("not a key"));

        db.put(k1, v1);
        db.put(k2, v2);

        assertEquals(v1, db.get(k1));
        assertEquals(v2, db.get(k2));

        try {
            db.remove(k2);
        } catch (Exception e) {
        }
        assertEquals(null, db.get(k2));
    }
}
