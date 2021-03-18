package com.github.steroidteam.todolist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USER_NAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Placeholder: The main To do activity not available yet => go directly to ItemViewActivity
        Intent listViewActivity = new Intent(MainActivity.this, ListSelectionActivity.class);
        startActivity(listViewActivity);
    }
}