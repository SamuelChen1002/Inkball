package inkball;

import java.util.*;

/**
 * Represents a ball spawner in the game.
 */
public class Spawner extends Sprite {

    public static Random random = new Random();
    public App app;

    /**
     * Constructs a new Spawner object.
     *
     * @param x    The x-coordinate of the spawner.
     * @param y    The y-coordinate of the spawner.
     * @param type The type of the spawner.
     * @param app  The main App instance.
     */
    public Spawner(int x, int y, char type, App app) {
        super(x, y, type);
        this.app = app.get_this();
    }

    /**
     * Spawns a new ball and adds it to the game.
     *
     * @param balls            The list of active balls in the game.
     * @param candidates_balls The list of candidate balls to spawn from.
     */
    public void spawnBalls(ArrayList<Ball> balls, ArrayList<Ball> candidates_balls) {
        int ball_x = X_pixel_to_index(this.x);
        int ball_y = Y_pixel_to_index(this.y);

        Ball b = new Ball(ball_x, ball_y, candidates_balls.get(0).get_type(), app);
        // Seems like there is a more efficient way to do this: Ball b =
        // candidates_balls.get(0);
        balls.add(b);
        candidates_balls.remove(0);
    }
}
