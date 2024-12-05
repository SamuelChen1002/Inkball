package inkball;

import java.util.*;
import processing.core.PVector;

/**
 * Represents a ball in the Inkball game.
 * This class extends the Sprite class and handles ball movement, collision, and
 * scoring.
 */

public class Ball extends Sprite {

    private App app;
    protected float speedX;
    protected float speedY;
    public Random random = new Random();
    public float ballSize = 24; // You could check it through the "property" of the picture.
    public int const_speed[] = { 2, -2 };

    private float increase_score;
    private float decrease_score;

    public boolean in_hole_flag;

    /**
     * Constructs a new Ball object.
     *
     * @param index_x The initial x-index of the ball on the game grid.
     * @param index_y The initial y-index of the ball on the game grid.
     * @param type    The type (color) of the ball.
     * @param app     The main App instance for accessing game state and methods.
     */

    public Ball(int index_x, int index_y, char type, App app) {
        super(index_x, index_y, type);
        this.speedX = const_speed[random.nextInt(const_speed.length)];
        this.speedY = const_speed[random.nextInt(const_speed.length)];
        this.in_hole_flag = false;
        this.increase_score = app.getScoreHoleCapture(type);
        this.decrease_score = app.getScoreWrongHole(type);
    }

    /**
     * Sets the ball's score values based on its type.
     *
     * @param app  The main App instance for accessing scoring methods.
     * @param type The type (color) of the ball.
     */
    public void set_ball_score(App app, char type) {
        this.app = app;
        if (app != null) {
            this.increase_score = app.getScoreHoleCapture(type);
            this.decrease_score = app.getScoreWrongHole(type);
        }
    }

    /**
     * Handles ball collision with walls and updates ball properties accordingly.
     *
     * @param tiles The 2D array of tiles representing the game board.
     * @param app   The main App instance for accessing game state and methods.
     */
    public void WallBounce(Tile[][] tiles, App app) {
        int ball_up = this.y;
        int ball_down = this.y + 24;
        int ball_left = this.x;
        int ball_right = this.x + 24;

        int ball_col = X_pixel_to_index(x);
        int ball_row = Y_pixel_to_index(y);

        Tile up_tile = tiles[ball_row - 1][ball_col];

        if (up_tile.is_wall) {
            if (ball_up <= up_tile.y + 24 && speedY < 0) {
                speedY = -speedY;
                if (up_tile.get_type() != 'X') {
                    this.type = up_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(up_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(up_tile.get_type());
                }
            }
        }

        Tile left_tile = tiles[ball_row][ball_col - 1];

        if (left_tile.is_wall) {
            if (ball_left <= left_tile.x + 24 && speedX < 0) {
                speedX = -speedX;
                if (left_tile != null && left_tile.get_type() != 'X') {
                    this.type = left_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(left_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(left_tile.get_type());
                }
            }
        }

        Tile right_tile = tiles[ball_row][ball_col + 1];

        if (right_tile.is_wall) {
            if (ball_right >= right_tile.x && speedX > 0) {
                speedX = -speedX;
                if (right_tile != null && right_tile.get_type() != 'X') {
                    this.type = right_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(right_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(right_tile.get_type());
                }
            }
        }

        Tile down_tile = tiles[ball_row + 1][ball_col];

        if (down_tile.is_wall) {
            if (ball_down >= down_tile.y && speedY > 0) {
                speedY = -speedY;
                if (down_tile != null && down_tile.get_type() != 'X') {
                    this.type = down_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(down_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(down_tile.get_type());
                }
            }
        }

        Tile left_down_tile = tiles[ball_row + 1][ball_col - 1];
        if (left_down_tile.is_wall) {
            if (ball_left <= left_down_tile.x + 24 && ball_down >= left_down_tile.y && speedX < 0 && speedY > 0) {
                speedX = -speedX;
                speedY = -speedY;
                if (left_down_tile != null && left_down_tile.get_type() != 'X') {
                    this.type = left_down_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(left_down_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(left_down_tile.get_type());
                }
            }
        }

        Tile right_down_tile = tiles[ball_row + 1][ball_col + 1];
        if (right_down_tile.is_wall) {
            if (ball_right >= right_down_tile.x && ball_down >= right_down_tile.y && speedX > 0 && speedY > 0) {
                speedX = -speedX;
                speedY = -speedY;
                if (right_down_tile != null && right_down_tile.get_type() != 'X') {
                    this.type = right_down_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(right_down_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(right_down_tile.get_type());
                }
            }
        }

        Tile left_up_tile = tiles[ball_row - 1][ball_col - 1];
        if (left_up_tile.is_wall) {
            if (ball_left <= left_up_tile.x + 24 && ball_up <= left_up_tile.y + 24 && speedX < 0 && speedY < 0) {
                speedX = -speedX;
                speedY = -speedY;
                if (left_up_tile != null && left_up_tile.get_type() != 'X') {
                    this.type = left_up_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(left_up_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(left_up_tile.get_type());
                }
            }
        }

        Tile right_up_tile = tiles[ball_row - 1][ball_col + 1];
        if (right_up_tile.is_wall) {
            if (ball_right >= right_up_tile.x && ball_up <= right_up_tile.y + 24 && speedX > 0 && speedY < 0) {
                speedX = -speedX;
                speedY = -speedY;
                if (right_up_tile != null && right_up_tile.get_type() != 'X') {
                    this.type = right_up_tile.get_type();
                    this.increase_score = app.getScoreHoleCapture(right_up_tile.get_type());
                    this.decrease_score = app.getScoreWrongHole(right_up_tile.get_type());
                }
            }
        }
    }

    /**
     * Checks if the ball has entered a hole and updates game state accordingly.
     *
     * @param holes The list of holes on the game board.
     * @param app   The main App instance for updating game state.
     */
    public void enter_hole(ArrayList<Hole> holes, App app) {
        double ball_center_x = this.x + ballSize / 2;
        double ball_center_y = this.y + ballSize / 2;

        for (Hole hole : holes) {
            double hole_center_x = hole.x + 32;
            double hole_center_y = hole.y + 32;

            double distance = Math
                    .sqrt(Math.pow(hole_center_x - ball_center_x, 2) + Math.pow(hole_center_y - ball_center_y, 2));
            if (distance < 32) {
                speedX += (hole_center_x - ball_center_x) * 0.0065;
                speedY += (hole_center_y - ball_center_y) * 0.0065;

                this.ballSize *= 0.8;
                if (distance < 15) {
                    this.ballSize *= 0.725;
                    if (this.ballSize <= 0.1 || distance < 1) {
                        this.in_hole_flag = true;
                        update_the_score(hole, app);
                        this.ballSize = 0;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Updates the game score when a ball enters a hole.
     *
     * @param hole The hole that the ball entered.
     * @param app  The main App instance for updating the score.
     */
    public void update_the_score(Hole hole, App app) {
        float update_score = 0;
        float increaseModifier = app.get_scoreIncreaseFromHoleCaptureModifier();
        float decreaseModifier = app.get_scoreDecreaseFromWrongHoleModifier();

        if (this.type == hole.get_hole_type() || hole.get_hole_type() == '0') {
            update_score += this.increase_score * increaseModifier;
        } else if (this.type != '0') {
            update_score += -this.decrease_score * decreaseModifier;

            ArrayList<Ball> candidates_balls = app.get_candidates_balls();
            this.x = 10 + (candidates_balls.size() + 2) * 32;
            this.y = 20;
            candidates_balls.add(this);
        }
        app.set_score(update_score);
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The distance between the two points.
     */
    public double dist(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Handles ball collision with player-drawn lines.
     *
     * @param lines The list of lines drawn by the player.
     */
    public void LineBounce(ArrayList<Line> lines) {
        double ball_center_x = this.x + ballSize / 2;
        double ball_center_y = this.y + ballSize / 2;

        Iterator<Line> lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            Line line = lineIterator.next();
            boolean collision = false;

            for (int[] point : line.points) {
                PVector p1 = new PVector(point[0], point[1]);
                PVector p2 = new PVector(point[2], point[3]);

                double distance1 = dist(p1.x, p1.y, (float) ball_center_x + speedX, (float) ball_center_y + speedY);
                double distance2 = dist(p2.x, p2.y, (float) ball_center_x + speedX, (float) ball_center_y + speedY);
                double distance1_2 = dist(p1.x, p1.y, p2.x, p2.y);

                if (distance1 + distance2 < distance1_2 + ballSize / 2) {
                    // the ball collides with the line
                    float dx = p2.x - p1.x;
                    float dy = p2.y - p1.y;

                    PVector normal1 = new PVector(-dy, dx).normalize();
                    PVector normal2 = new PVector(dy, -dx).normalize();

                    PVector correct_normal = get_correct_normal(p1, p2, normal1, normal2);
                    PVector old_speed = new PVector(speedX, speedY);

                    PVector new_speed = new_trajectory(correct_normal, old_speed);

                    speedX = new_speed.x;
                    speedY = new_speed.y;

                    collision = true;
                    break;
                }
            }

            if (collision) {
                lineIterator.remove();
                break;
            }
        }
    }

    /**
     * Calculates the new trajectory of the ball after collision with a line.
     *
     * @param n The normal vector of the line at the point of collision.
     * @param v The current velocity vector of the ball.
     * @return The new velocity vector after collision.
     */
    public PVector new_trajectory(PVector n, PVector v) {
        float dotProduct = v.dot(n);
        return PVector.sub(v, PVector.mult(n, 2 * dotProduct));
    }

    /**
     * Determines the correct normal vector for line collision.
     *
     * @param p1 The start point of the line segment.
     * @param p2 The end point of the line segment.
     * @param n1 One possible normal vector.
     * @param n2 The other possible normal vector.
     * @return The correct normal vector for collision calculation.
     */
    public PVector get_correct_normal(PVector p1, PVector p2, PVector n1, PVector n2) {
        PVector midPoint = PVector.add(p1, p2).div(2);
        PVector midP1 = PVector.add(midPoint, n1);
        PVector midP2 = PVector.add(midPoint, n2);
        float ball_center_x = this.x + ballSize / 2;
        float ball_center_y = this.y + ballSize / 2;

        double distance11 = dist(midP1.x, midP1.y, (float) ball_center_x, (float) ball_center_y);
        double distance12 = dist(midP2.x, midP2.y, (float) ball_center_x, (float) ball_center_y);

        if (distance11 < distance12) {
            return n1;
        } else {
            return n2;
        }
    }

    /**
     * Moves the ball based on its current speed and handles collisions.
     *
     * @param app The main App instance for accessing game state and methods.
     */
    public void move(App app) {
        if (!app.get_is_paused() && !app.get_is_lost()) {
            app.get_balls();
            WallBounce(app.get_tiles(), app);
            enter_hole(app.get_holes(), app);
            LineBounce(app.get_lines());

            this.x += this.speedX;
            this.y += this.speedY;
        }
    }

    public void set_speed_x(int x) {
        this.speedX = x;
    }

    public float get_speed_x() {
        return this.speedX;
    }

    public float get_speed_y() {
        return this.speedY;
    }

    public void set_speed_y(int y) {
        this.speedY = y;
    }

    public void setIndex(int indexX, int indexY) {
        super.setIndex(indexX, indexY);
        // If you need to reset the speed, you can do it here
        // this.speedX = 0;
        // this.speedY = 0;
    }

    @Override
    public char get_type() {
        return super.get_type();
    }

    public void set_type(char type) {
        this.type = type;
    }

    public float getIncreaseScore() {
        return increase_score;
    }

    public float getDecreaseScore() {
        return decrease_score;
    }

    public float get_position_x() {
        return this.x;
    }

    public int get_index_x() {
        return X_pixel_to_index(this.x);
    }

    public float get_position_y() {
        return this.y;
    }

    public int get_index_y() {
        return Y_pixel_to_index(this.y);
    }

    public float get_decrease_score() {
        return this.decrease_score;
    }

    public float get_increase_score() {
        return this.increase_score;
    }
}
