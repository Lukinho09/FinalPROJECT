package com.example.algorithmvisualizer.DFS;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.R;

public class DFSactivity extends AppCompatActivity {
    Graph G = new Graph();
    Integer startVertexId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfs);

        GraphVisual graphView = findViewById(R.id.graphView);
        Button btnStart = findViewById(R.id.btnStart);
        EditText inputEdge = findViewById(R.id.inputEdge);
        Button btnAdd = findViewById(R.id.btnAdd);

        graphView.setOnVertexClickListener(v -> {
            startVertexId = v.id;
            // ✅ (optional) selection stays even if you start later
            graphView.setSelectedVertexId(startVertexId);
        });

        btnAdd.setOnClickListener(v -> {
            String text = inputEdge.getText().toString().trim();
            if (text.isEmpty()) return;

            String[] parts = text.split("\\s+");
            try {
                if (parts.length == 1) G.addVertexIfNotExists(Integer.parseInt(parts[0]));
                else if (parts.length == 2) G.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } catch (Exception ignored) {}

            graphView.setGraph(G);
            inputEdge.setText("");
        });

        btnStart.setOnClickListener(v -> {
            if (startVertexId == null || !G.hasVertex(startVertexId)) return;

            // ✅ keep selected shown
            graphView.setSelectedVertexId(startVertexId);

            DFS dfs = new DFS(G);
            dfs.reset();

            for (Vertex vertex : G.getAllVertices()) vertex.visited = false;
            graphView.invalidate();

            new Thread(() -> {
                dfs.run(startVertexId, id -> {
                    Vertex vertexObj = G.getVertex(id);
                    if (vertexObj != null) vertexObj.visited = true;

                    runOnUiThread(graphView::invalidate);
                    try { Thread.sleep(600); } catch (Exception ignored) {}
                });
            }).start();
        });
    }
}
