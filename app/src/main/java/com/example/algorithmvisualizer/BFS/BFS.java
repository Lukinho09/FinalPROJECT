package com.example.algorithmvisualizer.BFS;

import com.example.algorithmvisualizer.DFS.Graph;
import java.util.*;

public class BFS {

    private Graph G;

    public interface VisitListener {
        void onVisit(int vertexId);
    }

    public BFS(Graph G) {
        this.G = G;
    }

    public void run(int startId, VisitListener listener) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new ArrayDeque<>();

        visited.add(startId);
        queue.add(startId);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            listener.onVisit(v);

            for (int u : G.adjacency.getOrDefault(v, new ArrayList<>())) {
                if (!visited.contains(u)) {
                    visited.add(u);
                    queue.add(u);
                }
            }
        }
    }
}
