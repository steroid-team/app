package com.github.steroidteam.todolist.model.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserFactory {
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static void set(FirebaseUser user) {
        UserFactory.user = user;
    }

    public static FirebaseUser get() {
        return UserFactory.user;
    }
}
