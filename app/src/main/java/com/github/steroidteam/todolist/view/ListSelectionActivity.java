package com.github.steroidteam.todolist.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.R;

public class ListSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selection);
        //Toolbar toolbar = findViewById(R.id.list_selection_toolbar);
        //setSupportActionBar(toolbar);
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void logOut(View view) {
        Activity thisActivity = this;
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> thisActivity.finish());
    }

    public void openList(View view){
        Intent itemViewActivity = new Intent(ListSelectionActivity.this, ItemViewActivity.class);
        startActivity(itemViewActivity);
    }

    public void openNotes(View view) {
        Intent noteSelectionActivity = new Intent(ListSelectionActivity.this, NoteSelectionActivity.class);
        startActivity(noteSelectionActivity);
    }
}