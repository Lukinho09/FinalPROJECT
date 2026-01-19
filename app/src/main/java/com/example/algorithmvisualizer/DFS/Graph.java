package com.example.algorithmvisualizer.DFS;

import java.util.*;

public class Graph {
    public Map<Integer, Vertex> vertices = new HashMap<>();
    public List<Edge> edges = new ArrayList<>();
    public Map<Integer, List<Integer>> adjacency = new HashMap<>();

    public Vertex addVertexIfNotExists(int id) {
        vertices.putIfAbsent(id, new Vertex(id, 0, 0));
        adjacency.putIfAbsent(id, new ArrayList<>());
        return vertices.get(id);
    }

    public void addEdge(int from, int to) {
        addVertexIfNotExists(from);
        addVertexIfNotExists(to);

        edges.add(new Edge(from, to));

        adjacency.get(from).add(to);

        adjacency.get(to).add(from);
    }

    public boolean hasVertex(int id) {
        return vertices.containsKey(id);
    }

    public Vertex getVertex(int id) {
        return vertices.get(id);
    }

    public Collection<Vertex> getAllVertices() {
        return vertices.values();
    }
}
