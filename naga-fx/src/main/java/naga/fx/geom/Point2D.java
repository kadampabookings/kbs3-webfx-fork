package naga.fx.geom;

/**
 * A 2D geometric point that usually represents the x, y coordinates.
 * It can also represent a relative magnitude vector's x, y magnitudes.
 */
public class Point2D {

    /**
     * The x coordinate.
     */
    private final double x;

    /**
     * The y coordinate.
     */
    private final double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The x coordinate.
     * @return the x coordinate
     */
    public final double getX() {
        return x;
    }

    /**
     * The y coordinate.
     * @return the y coordinate
     */
    public final double getY() {
        return y;
    }

    /**
     * Returns a string representation of this {@code Point2D}.
     * This method is intended to be used only for informational purposes.
     * The content and format of the returned string might vary between
     * implementations.
     * The returned string might be empty but cannot be {@code null}.
     */
    @Override public String toString() {
        return "Point2D [x = " + getX() + ", y = " + getY() + "]";
    }
}