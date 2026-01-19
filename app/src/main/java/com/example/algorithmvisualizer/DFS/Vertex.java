package com.example.algorithmvisualizer.DFS;

public class Vertex {
    public int id;
    public float x, y;
    public boolean visited = false;

    public Vertex(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
