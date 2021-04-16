package com.github.steroidteam.todolist.util;

import com.github.steroidteam.todolist.database.DatabaseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileStorageUtils {

    public static String getFileFromStorage(File rootDestination, String path) {
        File file = getFile(rootDestination, path);
        try {
            return readOnFile(file);
        } catch (DatabaseException e) {
            return null;
        }
    }

    public static void setFileInStorage(File rootDestination, String path, String value) {
        File file = getFile(rootDestination, path);
        try {
            writeOnFile(value, file);
        } catch (DatabaseException ignored) {

        }
    }

    public static void deleteFileFromStorage(File rootDestination, String path) {
        File file = getFile(rootDestination, path);
        if (file.exists()) {
            file.delete();
        }
    }

    private static File getFile(File rootDestination, String path) {
        return new File(rootDestination, path);
    }

    private static String readOnFile(File file) throws DatabaseException {
        String result = null;
        if (file.exists()) {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    result = sb.toString();
                }
            } catch (Exception e) {
                throw new DatabaseException("unable to read file at: " + file);
            }
        }
        return result;
    }

    private static void writeOnFile(String text, File file) throws DatabaseException {

        try {
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            try (Writer w = new BufferedWriter(new OutputStreamWriter(fos))) {
                w.write(text);
                w.flush();
                fos.getFD().sync();
            }
        } catch (Exception e) {
            throw new DatabaseException("unable to write on file at: " + file);
        }
    }
}
