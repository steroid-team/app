package com.github.steroidteam.todolist.util;

import java.util.Objects;

public class Utils {
    public static void checkNonNullArgs(Object... objects) {
        for (Object o : objects) Objects.requireNonNull(o);
    }
}
