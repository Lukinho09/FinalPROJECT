package com.example.algorithmvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.DFS.DFSactivity;
import com.example.algorithmvisualizer.BFS.BFSactivity;
import com.example.algorithmvisualizer.Search.SearchMenuActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // მთავარი მენიუ

        TextView dfsText = findViewById(R.id.dfsText);
        TextView bfsText = findViewById(R.id.bfsText);
        TextView searchText = findViewById(R.id.searchText);

        searchText.setOnClickListener(v ->
                startActivity(new Intent(this, SearchMenuActivity.class)));

        // DFS-ზე გადასვლა
        dfsText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DFSactivity.class);
            startActivity(intent);
        });

        // BFS-ზე გადასვლა
        bfsText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BFSactivity.class);
            startActivity(intent);
        });
    }
}
