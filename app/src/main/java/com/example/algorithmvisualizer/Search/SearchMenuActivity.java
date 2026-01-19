package com.example.algorithmvisualizer.Search;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.algorithmvisualizer.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.R;

public class SearchMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

        Button btnLinear = findViewById(R.id.btnLinear);
        Button btnBinary = findViewById(R.id.btnBinary);

        btnLinear.setOnClickListener(v ->
                startActivity(new Intent(this, LinearSearchActivity.class)));

        btnBinary.setOnClickListener(v ->
                startActivity(new Intent(this, BinarySearchActivity.class)));
    }
}
