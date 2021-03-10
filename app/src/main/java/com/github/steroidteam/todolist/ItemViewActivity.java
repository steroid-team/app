package com.github.steroidteam.todolist;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Toolbar toolbar = findViewById(R.id.activity_itemview_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String todoListTitle = "Example list";
        setTitle(todoListTitle);

        // Placeholder
        List<String> items = Arrays.asList("Change passwords", "Replace old server", "Set up firewall",
                "Fix router", "Change passwords", "Replace old server", "Set up firewall");

        // Todo: customize adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.layout_list_item, R.id.checkBox, items);
        ListView listView = findViewById(R.id.activity_itemview_itemlist);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }
}