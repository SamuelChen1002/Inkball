package inkball;

/**
 * Represents a tile on the game board.
 */
public class Tile extends Sprite {

    public boolean is_wall;
    public boolean is_hole;
    public boolean is_ball;
    public boolean is_spawner;

    /**
     * Constructs a new Tile object.
     *
     * @param index_x The x-index of the tile on the game grid.
     * @param index_y The y-index of the tile on the game grid.
     * @param type    The type of the tile.
     */
    public Tile(int index_x, int index_y, char type) {
        super(index_x, index_y, type);
        is_wall = type == 'X';
    }

    /**
     * Sets this tile as a hole.
     */
    public void set_hole() {
        this.is_hole = true;
    }

    /**
     * Sets this tile as a wall.
     */
    public void set_wall() {
        this.is_wall = true;
    }

    /**
     * Sets this tile as containing a ball.
     */
    public void set_ball() {
        this.is_ball = true;
    }

    /**
     * Sets this tile as a spawner.
     */
    public void set_spawner() {
        this.is_spawner = true;
    }
}
