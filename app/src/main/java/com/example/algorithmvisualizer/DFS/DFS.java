package com.example.algorithmvisualizer.DFS;

import java.util.*;
import java.util.function.Consumer;

public class DFS {
    private Graph G;

    public DFS(Graph G) {
        this.G = G;
    }

    public void reset() {
        for (Vertex v : G.getAllVertices()) v.visited = false;
    }

    public void run(int startId, Consumer<Integer> visit) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(startId);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                visit.accept(current);

                List<Integer> neighbors = G.adjacency.getOrDefault(current, new ArrayList<>());
                for (int n : neighbors) {
                    if (!visited.contains(n)) stack.push(n);
                }
            }
        }
    }
}
