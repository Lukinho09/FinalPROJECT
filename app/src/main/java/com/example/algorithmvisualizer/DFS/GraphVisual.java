package com.example.algorithmvisualizer.DFS;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.*;

public class GraphVisual extends View {

    private Graph G;
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();

    private Vertex draggedVertex = null;
    private float offsetX, offsetY;

    // ✅ selected vertex id (to draw red ring)
    private int selectedVertexId = -1;

    // Vertex click listener DFS/BFS-ისთვის
    public interface OnVertexClickListener {
        void onVertexClick(Vertex v);
    }

    private OnVertexClickListener vertexClickListener;

    public void setOnVertexClickListener(OnVertexClickListener listener) {
        vertexClickListener = listener;
    }

    // ✅ allow activity to set selection too (optional but useful)
    public void setSelectedVertexId(int id) {
        selectedVertexId = id;
        invalidate();
    }

    public int getSelectedVertexId() {
        return selectedVertexId;
    }

    public GraphVisual(Context context, AttributeSet attrs) {
        super(context, attrs);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(8, 2, 2, Color.DKGRAY);

        paint.setAntiAlias(true);
    }

    public void setGraph(Graph G) {
        this.G = G;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (G == null) return;

        layoutTree(); // მხოლოდ ახალ Vertex-ებზე პოზიციის დადგენა

        drawEdges(canvas);
        drawVertices(canvas);
    }

    private void drawEdges(Canvas canvas) {
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);

        for (Edge e : G.edges) {
            Vertex from = G.getVertex(e.from);
            Vertex to = G.getVertex(e.to);
            if (from != null && to != null) {
                canvas.drawLine(from.x, from.y, to.x, to.y, paint);
            }
        }
    }

    private void drawVertices(Canvas canvas) {
        int radius = 70;

        for (Vertex v : G.getAllVertices()) {
            int circleColor = v.visited ? Color.parseColor("#4CAF50") : Color.parseColor("#2196F3");

            RadialGradient gradient = new RadialGradient(
                    v.x, v.y, radius,
                    Color.WHITE, circleColor,
                    Shader.TileMode.MIRROR
            );

            // Fill
            paint.setShader(gradient);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(v.x, v.y, radius, paint);

            // Black border
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(v.x, v.y, radius, paint);

            // ✅ Selected red ring (outer glow/border)
            if (v.id == selectedVertexId) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setColor(Color.RED);
                canvas.drawCircle(v.x, v.y, radius + 10, paint);
            }

            // Text
            canvas.drawText(String.valueOf(v.id), v.x, v.y + 18, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (G == null) return false;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Vertex v : G.getAllVertices()) {
                    float dx = x - v.x;
                    float dy = y - v.y;

                    if (dx * dx + dy * dy <= 70 * 70) {
                        draggedVertex = v;
                        offsetX = x - v.x;
                        offsetY = y - v.y;

                        // ✅ mark selected and redraw red ring
                        selectedVertexId = v.id;
                        invalidate();

                        if (vertexClickListener != null) {
                            vertexClickListener.onVertexClick(v);
                        }
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (draggedVertex != null) {
                    draggedVertex.x = x - offsetX;
                    draggedVertex.y = y - offsetY;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                draggedVertex = null;
                break;
        }

        return true;
    }

    // Vertex-ების ავტომატური განლაგება მხოლოდ ახალ Vertex-ებზე
    private void layoutTree() {
        if (G.getAllVertices().isEmpty()) return;

        int width = getWidth();
        int topOffset = 200;
        int levelHeight = 180;

        // დონეების დადგენა DFS-ის მსგავსი ალგორითმით
        Map<Integer, Integer> levels = new HashMap<>();
        for (Vertex v : G.getAllVertices()) {
            assignLevel(v.id, 0, levels, new HashSet<>());
        }

        Map<Integer, Integer> levelCounts = new HashMap<>();
        for (Vertex v : G.getAllVertices()) {
            if (v.x != 0 && v.y != 0) continue; // ხელით გადატანილი Vertex შენარჩუნება

            Integer levelObj = levels.get(v.id);
            int level = (levelObj == null) ? 0 : levelObj;
            int count = levelCounts.getOrDefault(level, 0);

            long total = 0;
            for (Vertex vv : G.getAllVertices()) {
                Integer lv = levels.get(vv.id);
                if (lv != null && lv == level && vv.x == 0 && vv.y == 0) total++;
            }

            int x = 50 + (int) ((width - 100) * (count + 1) / (float) (total + 1));
            int y = topOffset + level * levelHeight;

            v.x = x;
            v.y = y;

            levelCounts.put(level, count + 1);
        }
    }

    // დონეების დადგენა DFS-ის მსგავსი ალგორითმი
    private void assignLevel(int id, int level, Map<Integer, Integer> levels, Set<Integer> visited) {
        if (visited.contains(id)) return;
        visited.add(id);

        if (!levels.containsKey(id) || level < levels.get(id)) {
            levels.put(id, level);
        }

        List<Integer> neighbors = G.adjacency.getOrDefault(id, new ArrayList<>());
        for (int n : neighbors) assignLevel(n, level + 1, levels, visited);
    }
}
