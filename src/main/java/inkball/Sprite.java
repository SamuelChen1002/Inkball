package inkball;

/**
 * Base class for game objects that have a position and type.
 */
public class Sprite {
    public int x;
    public int y;
    protected char type;

    /**
     * Constructs a new Sprite object.
     *
     * @param index_x The initial x-index of the sprite on the game grid.
     * @param index_y The initial y-index of the sprite on the game grid.
     * @param type    The type of the sprite.
     */
    public Sprite(int index_x, int index_y, char type) {
        this.x = X_index_to_pixel(index_x);
        this.y = Y_index_to_pixel(index_y);
        this.type = type;
    }

    /**
     * Converts a grid x-index to a pixel x-coordinate.
     *
     * @param index_x The x-index in the grid.
     * @return The corresponding x-coordinate in pixels.
     */
    public int X_index_to_pixel(int index_x) {
        return index_x * 32;
    }

    /**
     * Converts a grid y-index to a pixel y-coordinate.
     *
     * @param index_y The y-index in the grid.
     * @return The corresponding y-coordinate in pixels.
     */
    public int Y_index_to_pixel(int index_y) {
        return 64 + index_y * 32;
    }

    /**
     * Converts a pixel x-coordinate to a grid x-index.
     *
     * @param pixel_x The x-coordinate in pixels.
     * @return The corresponding x-index in the grid.
     */
    public int X_pixel_to_index(int pixel_x) {
        return Math.round((float) pixel_x / 32);
    }

    /**
     * Converts a pixel y-coordinate to a grid y-index.
     *
     * @param pixel_y The y-coordinate in pixels.
     * @return The corresponding y-index in the grid.
     */
    public int Y_pixel_to_index(int pixel_y) {
        return Math.round((float) (pixel_y - 64) / 32);
    }

    @Override
    public String toString() {
        return "Sprite{" + "x=" + x + ", y=" + y + ", type=" + type + '}';
    }

    /**
     * Getter and setter methods for the sprite's type, x, and y coordinates.
     */

    public char get_type() {
        return this.type;
    }

    public void set_x(int x) {
        this.x = x;
    }

    public void set_y(int y) {
        this.y = y;
    }

    public int getGridX() {
        return this.x / 32;
    }

    public int getGridY() {
        return (this.y - 64) / 32;
    }

    public void setIndex(int indexX, int indexY) {
        this.x = X_index_to_pixel(indexX);
        this.y = Y_index_to_pixel(indexY);
    }

}
