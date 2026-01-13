package com.example.algorithmvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.DFS.DFSactivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // მხოლოდ მენიუ

        TextView dfsIcon = findViewById(R.id.dfsText);

        dfsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DFSactivity.class);
            startActivity(intent);
        });
    }
}