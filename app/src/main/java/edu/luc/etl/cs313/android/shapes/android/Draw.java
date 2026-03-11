package edu.luc.etl.cs313.android.shapes.android;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import edu.luc.etl.cs313.android.shapes.model.*;

public class Draw implements Visitor<Void> {

    private final Canvas canvas;
    private final Paint paint;

    public Draw(final Canvas canvas, final Paint paint) {
        this.canvas = canvas;
        this.paint = paint;
        paint.setStyle(Style.STROKE);
    }

    @Override
    public Void onCircle(final Circle c) {
        canvas.drawCircle(0, 0, c.getRadius(), paint);
        return null;
    }

    @Override
    public Void onRectangle(final Rectangle r) {
        canvas.drawRect(0, 0, r.getWidth(), r.getHeight(), paint);
        return null;
    }

    @Override
    public Void onLocation(final Location l) {
        canvas.translate(l.getX(), l.getY());
        l.getShape().accept(this);
        canvas.translate(-l.getX(), -l.getY());
        return null;
    }

    @Override
    public Void onGroup(final Group g) {
        for (Shape shape : g.getShapes()) {
            shape.accept(this);
        }
        return null;
    }

    @Override
    public Void onFill(final Fill f) {
        final Style savedStyle = paint.getStyle();
        paint.setStyle(Style.FILL_AND_STROKE);
        f.getShape().accept(this);
        paint.setStyle(savedStyle);
        return null;
    }

    @Override
    public Void onOutline(final Outline o) {
        final Style savedStyle = paint.getStyle();
        paint.setStyle(Style.STROKE);
        o.getShape().accept(this);
        paint.setStyle(savedStyle);
        return null;
    }

    @Override
    public Void onStrokeColor(final StrokeColor c) {
        final int savedColor = paint.getColor();
        paint.setColor(c.getColor());
        c.getShape().accept(this);
        paint.setColor(savedColor);
        return null;
    }

    @Override
    public Void onPolygon(final Polygon s) {
        final java.util.List<? extends Point> points = s.getPoints();
        final int size = points.size();
        final float[] pts = new float[size * 4];
        for (int i = 0; i < size; i++) {
            Point curr = points.get(i);
            Point next = points.get((i + 1) % size);
            pts[i * 4]     = curr.getX();
            pts[i * 4 + 1] = curr.getY();
            pts[i * 4 + 2] = next.getX();
            pts[i * 4 + 3] = next.getY();
        }
        canvas.drawLines(pts, paint);
        return null;
    }
}