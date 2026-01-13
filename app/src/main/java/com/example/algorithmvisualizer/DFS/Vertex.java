package com.example.algorithmvisualizer.DFS;

public class Vertex {
    public int id;         // Vertex-ის ნომერი
    public float x, y;     // Vertex-ის პოზიცია ეკრანზე
    public boolean visited = false; // DFS-ისთვის

    public Vertex(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
