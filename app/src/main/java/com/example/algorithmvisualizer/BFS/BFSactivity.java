package com.example.algorithmvisualizer.BFS;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.R;
import com.example.algorithmvisualizer.DFS.Graph;
import com.example.algorithmvisualizer.DFS.GraphVisual;
import com.example.algorithmvisualizer.DFS.Vertex;

public class BFSactivity extends AppCompatActivity {

    Graph G = new Graph();
    Integer startVertexId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bfs); // ⚠️ სხვა layout

        GraphVisual graphView = findViewById(R.id.graphView);
        Button btnStart = findViewById(R.id.btnStart);
        EditText inputEdge = findViewById(R.id.inputEdge);
        Button btnAdd = findViewById(R.id.btnAdd);

        btnStart.setText("Start BFS");

        graphView.setOnVertexClickListener(v -> startVertexId = v.id);

        btnAdd.setOnClickListener(v -> {
            String text = inputEdge.getText().toString().trim();
            if (text.isEmpty()) return;

            String[] parts = text.split("\\s+");
            try {
                if (parts.length == 1)
                    G.addVertexIfNotExists(Integer.parseInt(parts[0]));
                else if (parts.length == 2)
                    G.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } catch (Exception ignored) {}

            graphView.setGraph(G);
            inputEdge.setText("");
        });

        btnStart.setOnClickListener(v -> {
            if (startVertexId == null) return;

            for (Vertex vertex : G.getAllVertices())
                vertex.visited = false;

            graphView.invalidate();

            BFS bfs = new BFS(G);

            new Thread(() -> {
                bfs.run(startVertexId, id -> {
                    Vertex vv = G.getVertex(id);
                    if (vv != null) vv.visited = true;
                    runOnUiThread(graphView::invalidate);
                    try { Thread.sleep(600); } catch (Exception ignored) {}
                });
            }).start();
        });
    }
}
