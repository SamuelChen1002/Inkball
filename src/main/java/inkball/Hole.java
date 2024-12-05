package inkball;

/**
 * Represents a hole on the game board.
 */
public class Hole extends Sprite {
    private char hole_type;

    /**
     * Constructs a new Hole object.
     *
     * @param index_x   The x-index of the hole on the game grid.
     * @param index_y   The y-index of the hole on the game grid.
     * @param hole_type The type (color) of the hole.
     */
    public Hole(int index_x, int index_y, char hole_type) {
        super(index_x, index_y, hole_type);
        this.hole_type = hole_type;
    }

    /**
     * The getter and setter methods for the hole type.
     */

    public char get_hole_type() {
        return hole_type;
    }

    public void set_hole_type(char type) {
        this.hole_type = type;
    }

    @Override
    public String toString() {
        return "Hole{x=" + x + ", y=" + y + ", type=" + hole_type + '}';
    }
}
