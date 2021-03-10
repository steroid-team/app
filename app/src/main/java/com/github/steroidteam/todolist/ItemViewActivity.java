package com.github.steroidteam.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        // Placeholder
        List<String> items = Arrays.asList("Change passwords", "Replace old server", "Set up firewall",
                "Fix router", "Change passwords", "Replace old server", "Set up firewall");

        // Todo: customize adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.layout_list_item, R.id.checkBox, items);
        ListView listView = findViewById(R.id.itemlist);
        listView.setAdapter(adapter);
    }
}