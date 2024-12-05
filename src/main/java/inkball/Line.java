package inkball;

import java.util.*;

/**
 * Represents a line drawn by the player.
 */
public class Line {
    public ArrayList<int[]> points;

    /**
     * Constructs a new Line object.
     *
     * @param points An ArrayList of int arrays, each representing a point on the
     *               line.
     */
    public Line(ArrayList<int[]> points) {
        this.points = points;
    }

    /**
     * Checks if this line intersects with another line.
     *
     * @param other The other line to check for intersection.
     * @return true if the lines intersect, false otherwise.
     */
    public boolean intersects(Line other) {
        for (int[] segment1 : this.points) {
            for (int[] segment2 : other.points) {
                if (linesIntersect(segment1[0], segment1[1], segment1[2], segment1[3],
                        segment2[0], segment2[1], segment2[2], segment2[3])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if two line segments intersect.
     *
     * @param x1 The x-coordinate of the first point of the first line segment.
     * @param y1 The y-coordinate of the first point of the first line segment.
     * @param x2 The x-coordinate of the second point of the first line segment.
     * @param y2 The y-coordinate of the second point of the first line segment.
     * @param x3 The x-coordinate of the first point of the second line segment.
     * @param y3 The y-coordinate of the first point of the second line segment.
     * @param x4 The x-coordinate of the second point of the second line segment.
     * @param y4 The y-coordinate of the second point of the second line segment.
     * @return true if the line segments intersect, false otherwise.
     */
    private boolean linesIntersect(float x1, float y1, float x2, float y2,
            float x3, float y3, float x4, float y4) {
        float denom = ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));
        if (denom == 0) {
            return false;
        }
        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
        return ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1;
    }
}
