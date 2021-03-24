package com.github.steroidteam.todolist;

import com.github.steroidteam.todolist.database.VolatileDatabase;
import com.github.steroidteam.todolist.notes.Note;

// The purpose of this class is for testing, in order to demonstrate the use of the
// volatile database before it is fully integrated.
public class DBInstance {

    public static VolatileDatabase volatileDatabase = initDatabase();

    private static VolatileDatabase initDatabase() {
        VolatileDatabase db = new VolatileDatabase();

        Note note1 = new Note("Lorem ipsum");
        note1.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor" +
                " incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation" +
                " ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in" +
                " voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non" +
                " proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        Note note2 = new Note("Note 2");
        note2.setContent("This is the second note");
        Note note3 = new Note("Note 3");
        note3.setContent("This is the third note");

        db.putNote(note1);
        db.putNote(note2);
        db.putNote(note3);

        return db;
    }

}
