package com.github.steroidteam.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        TextView mainGreeting = findViewById(R.id.activity_greeting_text);
        Intent intentFromMainActivity = getIntent();

        String userName = intentFromMainActivity.getStringExtra(MainActivity.EXTRA_USER_NAME);
        String text = "Welcome " + userName + " !";
        mainGreeting.setText(text);
    }
}