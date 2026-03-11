package edu.luc.etl.cs313.android.shapes.model;

/**
 * A shape visitor for calculating the bounding box, that is, the smallest
 * rectangle containing the shape. The resulting bounding box is returned as a
 * rectangle at a specific location.
 */
public class BoundingBox implements Visitor<Location> {

    @Override
    public Location onCircle(final Circle c) {
        final int radius = c.getRadius();
        return new Location(-radius, -radius, new Rectangle(2 * radius, 2 * radius));
    }

    @Override
    public Location onRectangle(final Rectangle r) {
        return new Location(0, 0, new Rectangle(r.getWidth(), r.getHeight()));
    }

    @Override
    public Location onFill(final Fill f) {
        return f.getShape().accept(this);
    }

    @Override
    public Location onOutline(final Outline o) {
        return o.getShape().accept(this);
    }

    @Override
    public Location onStrokeColor(final StrokeColor c) {
        return c.getShape().accept(this);
    }

    @Override
    public Location onLocation(final Location l) {
        final Location inner = l.getShape().accept(this);
        return new Location(
                l.getX() + inner.getX(),
                l.getY() + inner.getY(),
                inner.getShape()
        );
    }

    @Override
    public Location onGroup(final Group g) {
        Location result = null;
        for (Shape s : g.getShapes()) {
            Location bb = s.accept(this);
            if (result == null) {
                result = bb;
            } else {
                result = union(result, bb);
            }
        }
        return result;
    }

    @Override
    public Location onPolygon(final Polygon p) {
        return onGroup(p);
    }

    private Location union(final Location a, final Location b) {
        final int ax = a.getX(), ay = a.getY();
        final int bx = b.getX(), by = b.getY();
        final int aw = ((Rectangle) a.getShape()).getWidth();
        final int ah = ((Rectangle) a.getShape()).getHeight();
        final int bw = ((Rectangle) b.getShape()).getWidth();
        final int bh = ((Rectangle) b.getShape()).getHeight();

        final int minX = Math.min(ax, bx);
        final int minY = Math.min(ay, by);
        final int maxX = Math.max(ax + aw, bx + bw);
        final int maxY = Math.max(ay + ah, by + bh);

        return new Location(minX, minY, new Rectangle(maxX - minX, maxY - minY));
    }
}