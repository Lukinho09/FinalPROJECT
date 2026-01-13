package com.example.algorithmvisualizer.DFS;


import java.util.HashSet;
import java.util.Set;

    public class DFS {
        private Graph G;
        private Set<Integer> visited = new HashSet<>();

        public DFS(Graph G) {
            this.G = G;
        }

        public void run(int startId, VisitListener listener) {
            dfs(startId, listener);
        }

        private void dfs(int id, VisitListener listener) {
            if (visited.contains(id)) return;

            visited.add(id);
            listener.onVisit(id);

            for (Edge e : G.edges) {
                if (e.from == id) {
                    dfs(e.to, listener);
                }
            }
        }
        public void reset() {
            visited.clear();
        }

        public interface VisitListener {
            void onVisit(int vertexId);
        }
    }
