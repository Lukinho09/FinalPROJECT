package com.example.algorithmvisualizer.DFS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class GraphVisual extends View {
    private boolean layoutDone = false;
    private Graph G;
    private Paint paint = new Paint();
    private Paint textPaint = new Paint();

    private Vertex selectedVertex = null;
    private float offsetX, offsetY;

    public GraphVisual(Context context, AttributeSet attrs) {
        super(context, attrs);

        // ტექსტისთვის ფერი, ზომა, bold, shadow
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(8, 2, 2, Color.DKGRAY);
    }

    public void setGraph(Graph G) {
        this.G = G;
        layoutDone = false; // ახალ Graph-ზე layout ხელახლა მოხდება
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (G == null) return;

        if (!layoutDone) {
            G.buildTree();
            int startY = getHeight() / 3;
            layoutTree(0, 0, getWidth(), startY, 180);
            layoutDone = true;
        }

        paint.setStrokeWidth(5);

        // Edge-ების დახატვა
        for (Edge e : G.edges) {
            Vertex from = G.vertices.get(e.from);
            Vertex to = G.vertices.get(e.to);
            paint.setColor(Color.BLACK);
            canvas.drawLine(from.x, from.y, to.x, to.y, paint);
        }

        // Vertex-ების დახატვა
        for (Vertex v : G.vertices) {
            // Gradient background for circle
            int circleColor = v.visited ? Color.parseColor("#4CAF50") : Color.parseColor("#2196F3");
            RadialGradient gradient = new RadialGradient(
                    v.x, v.y, 70,
                    Color.WHITE, circleColor,
                    Shader.TileMode.MIRROR
            );
            paint.setShader(gradient);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(v.x, v.y, 70, paint);

            // Outline for contrast
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(v.x, v.y, 70, paint);

            // Draw text
            canvas.drawText(String.valueOf(v.id), v.x, v.y + 18, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case android.view.MotionEvent.ACTION_DOWN:
                for (Vertex v : G.vertices) {
                    float dx = x - v.x;
                    float dy = y - v.y;
                    if (dx * dx + dy * dy <= 70 * 70) {
                        selectedVertex = v;
                        offsetX = x - v.x;
                        offsetY = y - v.y;
                        break;
                    }
                }
                break;

            case android.view.MotionEvent.ACTION_MOVE:
                if (selectedVertex != null) {
                    selectedVertex.x = x - offsetX;
                    selectedVertex.y = y - offsetY;
                    invalidate();
                }
                break;

            case android.view.MotionEvent.ACTION_UP:
                selectedVertex = null;
                break;
        }
        return true;
    }

    private void layoutTree(int v, int left, int right, int y, int levelHeight) {
        int screenWidth = getWidth();
        int screenHeight = getHeight();
        int radius = 70;
        int padding = 20;

        // ეკრანის საზღვრები
        left = Math.max(left, padding + radius);
        right = Math.min(right, screenWidth - padding - radius);
        y = Math.min(y, screenHeight - padding - radius);

        int mid = (left + right) / 2;
        Vertex vert = G.vertices.get(v);
        vert.x = mid;
        vert.y = y;

        int childrenCount = G.children.get(v).size();
        if (childrenCount == 0) return;

        int segment = (right - left) / childrenCount;
        int childLeft = left;

        for (int u : G.children.get(v)) {
            layoutTree(u, childLeft, childLeft + segment, y + levelHeight, levelHeight);
            childLeft += segment;
        }
    }
}
