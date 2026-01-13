package com.example.algorithmvisualizer.DFS;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.algorithmvisualizer.R;

public class DFSactivity extends AppCompatActivity {

    Graph G = new Graph();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfs);   // აქ არის graphView

        GraphVisual graphView = findViewById(R.id.graphView);
        Button btn = findViewById(R.id.btnStart);

        // ---------- 16 Vertex (ხის სტრუქტურა) ----------
        G.vertices.add(new Vertex(0, 600, 100));

        G.vertices.add(new Vertex(1, 300, 250));
        G.vertices.add(new Vertex(2, 900, 250));

        G.vertices.add(new Vertex(3, 150, 400));
        G.vertices.add(new Vertex(4, 450, 400));
        G.vertices.add(new Vertex(5, 750, 400));
        G.vertices.add(new Vertex(6, 1050, 400));

        G.vertices.add(new Vertex(7, 75, 550));
        G.vertices.add(new Vertex(8, 225, 550));
        G.vertices.add(new Vertex(9, 375, 550));
        G.vertices.add(new Vertex(10, 525, 550));
        G.vertices.add(new Vertex(11, 675, 550));
        G.vertices.add(new Vertex(12, 825, 550));
        G.vertices.add(new Vertex(13, 975, 550));
        G.vertices.add(new Vertex(14, 1125, 550));
        G.vertices.add(new Vertex(15, 1275, 550));


// ---------- Edges (Tree structure) ----------
        G.edges.add(new Edge(0,1));
        G.edges.add(new Edge(0,2));

        G.edges.add(new Edge(1,3));
        G.edges.add(new Edge(1,4));
        G.edges.add(new Edge(2,5));
        G.edges.add(new Edge(2,6));

        G.edges.add(new Edge(3,7));
        G.edges.add(new Edge(3,8));
        G.edges.add(new Edge(4,9));
        G.edges.add(new Edge(4,10));
        G.edges.add(new Edge(5,11));
        G.edges.add(new Edge(5,12));
        G.edges.add(new Edge(6,13));
        G.edges.add(new Edge(6,14));
        G.edges.add(new Edge(6,15));

        graphView.setGraph(G);

        btn.setOnClickListener(v -> {
            DFS dfs = new DFS(G);   // ან თუ DFS უკვე გაქვთ instance, უბრალოდ reset
            dfs.reset();            // გასუფთავება

            // Vertex-ების ფერის გასუფთავება
            for (Vertex vertex : G.vertices) {
                vertex.visited = false;
            }
            graphView.invalidate();

            // DFS დაწყება
            new Thread(() -> {
                dfs.run(0, id -> {
                    G.vertices.get(id).visited = true;
                    runOnUiThread(() -> graphView.invalidate());
                    try { Thread.sleep(600); } catch (Exception e) {}
                });
            }).start();
        });


    }

}
