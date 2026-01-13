package com.example.algorithmvisualizer.DFS;

import java.util.Vector;

public class Graph {
    public Vector<Vertex> vertices = new Vector<>();
    public Vector<Edge> edges = new Vector<>();

    public Vector<Vector<Integer>> children = new Vector<>();

    public void buildTree() {
        children.clear();
        for (int i = 0; i < vertices.size(); i++)
            children.add(new Vector<>());

        for (Edge e : edges) {
            children.get(e.from).add(e.to);
        }
    }
}
