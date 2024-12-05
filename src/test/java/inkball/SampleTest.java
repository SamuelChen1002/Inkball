package inkball;

import processing.core.PApplet;
import processing.core.PVector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;

public class SampleTest {

    private App app;
    private static final int test_time = 3;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.delay(1000);
        app.setup();
    }

    /**
     * Tests the initial state of the game after setup.
     * Verifies the correct number of spawners, balls, holes, candidate balls,
     * initial score, time, and spawn interval.
     */

    @Test
    void init_test() {
        assertEquals(1, app.get_spawner().size());
        assertEquals(1, app.get_balls().size());
        assertEquals(4, app.get_holes().size());
        assertEquals(6, app.get_candidates_balls().size());
        assertEquals(0, app.get_score());
        assertEquals(120, app.get_time());
        assertEquals(10.0f, app.get_spawn_interval(), 1.0f);
    }

    /**
     * Tests the ball spawning mechanism.
     * Verifies that a new ball is correctly spawned from a specific spawner,
     * checking its type, speed, and position relative to the spawner.
     */
    @Test
    void spawn_ball_test() {
        Spawner spawner = new Spawner(5, 3, 'S', app);
        app.get_spawner().clear();
        app.get_spawner().add(spawner);

        int initialBallsCount = app.get_balls().size();
        int initialCandidateBallsCount = app.get_candidates_balls().size();
        char expectedBallType = app.get_candidates_balls().get(0).get_type();

        spawner.spawnBalls(app.get_balls(), app.get_candidates_balls());

        assertEquals(initialBallsCount + 1, app.get_balls().size(), "One new ball should be added");
        assertEquals(initialCandidateBallsCount - 1, app.get_candidates_balls().size(),
                "One candidate ball should be removed");

        Ball newBall = app.get_balls().get(app.get_balls().size() - 1);

        assertEquals(expectedBallType, newBall.get_type(),
                "New ball should have the same type as the first candidate ball");

        float speedX = newBall.get_speed_x();
        float speedY = newBall.get_speed_y();

        assertTrue(Math.abs(speedX) == 2, "Ball's X speed absolute value should be 2");
        assertTrue(Math.abs(speedY) == 2, "Ball's Y speed absolute value should be 2");

        float tolerance = 5.0f;
        assertEquals(spawner.x, newBall.x, app.CELLSIZE + tolerance,
                "New ball's X position should be close to spawner's X position");
        assertEquals(spawner.y, newBall.y, app.CELLSIZE + tolerance,
                "New ball's Y position should be close to spawner's Y position");
    }

    /**
     * Tests the ball movement mechanism.
     * Verifies that a ball's position changes after calling the move method.
     */
    @Test
    void move_ball_test() {
        Ball ball = app.get_balls().get(0);
        PVector place1 = new PVector((float) ball.get_position_x(), (float) ball.get_position_y());
        ball.move(app);
        PVector place2 = new PVector((float) ball.get_position_x(), (float) ball.get_position_y());
        assertNotEquals(place1, place2);
    }

    /**
     * Tests ball collision with walls.
     * Verifies that balls correctly bounce off walls in all four directions,
     * changing their speed appropriately.
     */
    @Test
    void ball_and_wall_collision_test() {

        Ball testBall1 = new Ball(16, 5, '2', app);
        testBall1.set_speed_x(2);
        testBall1.set_speed_y(0);

        app.get_balls().clear();
        app.get_balls().add(testBall1);

        for (int i = 0; i < 10; i++) {
            testBall1.move(app);
        }

        assertEquals(-2, testBall1.get_speed_x(), 0.01);
        assertEquals(0, testBall1.get_speed_y(), 0.01);

        Ball testBall2 = new Ball(1, 5, '2', app);
        testBall2.set_speed_x(-2);
        testBall2.set_speed_y(0);

        app.get_balls().clear();
        app.get_balls().add(testBall2);

        for (int i = 0; i < 10; i++) {
            testBall2.move(app);
        }

        assertEquals(2, testBall2.get_speed_x(), 0.01);
        assertEquals(0, testBall2.get_speed_y(), 0.01);

        Ball testBall3 = new Ball(5, 1, '2', app);
        testBall3.set_speed_x(0);
        testBall3.set_speed_y(-2);

        app.get_balls().clear();
        app.get_balls().add(testBall3);

        for (int i = 0; i < 10; i++) {
            testBall3.move(app);
        }

        assertEquals(0, testBall3.get_speed_x(), 0.01);
        assertEquals(2, testBall3.get_speed_y(), 0.01);

        Ball testBall4 = new Ball(5, 16, '2', app);
        testBall4.set_speed_x(0);
        testBall4.set_speed_y(2);

        app.get_balls().clear();
        app.get_balls().add(testBall4);

        for (int i = 0; i < 10; i++) {
            testBall4.move(app);
        }

        assertEquals(0, testBall4.get_speed_x(), 0.01);
        assertEquals(-2, testBall4.get_speed_y(), 0.01);
    }

    /**
     * Tests ball collision with a line .
     * Verifies that the ball correctly bounces off the line, reversing its
     * horizontal speed.
     */
    @Test
    void ball_and_line_collision_test() {
        ArrayList<int[]> points = new ArrayList<>();
        points.add(new int[] { 320, 96, 320, 320 });
        Line verticalLine = new Line(points);
        app.Linelist.clear();
        app.Linelist.add(verticalLine);

        Ball testBall = new Ball(9, 4, '2', app);
        testBall.set_speed_x(2);
        testBall.set_speed_y(0);

        app.get_balls().clear();
        app.get_balls().add(testBall);

        for (int i = 0; i < 20; i++) {
            testBall.move(app);
            if (testBall.get_speed_x() < 0) {
                break;
            }
        }

        assertEquals(-2, testBall.get_speed_x(), 0.01);
        assertEquals(0, testBall.get_speed_y(), 0.01);

        assertTrue(testBall.get_position_x() < 320);
        assertEquals(9, testBall.get_index_x());

    }

    /**
     * Tests the pause and resume functionality of the game.
     * Verifies that the game correctly pauses and resumes ball movement when the
     * space key is pressed.
     */
    @Test
    void pause_and_resume_test() {
        assertFalse(app.get_is_paused());
        Ball ball = app.get_balls().get(0);
        ball.set_speed_x(2);
        ball.set_speed_y(2);

        app.key = ' ';
        app.keyPressed(new processing.event.KeyEvent(null, 0, processing.event.KeyEvent.PRESS, 0, ' ', 32));
        assertTrue(app.get_is_paused());

        float initialX = ball.get_position_x();
        float initialY = ball.get_position_y();

        ball.move(app);

        assertEquals(initialX, ball.get_position_x(), 0.01);
        assertEquals(initialY, ball.get_position_y(), 0.01);

        app.keyPressed(new processing.event.KeyEvent(null, 0, processing.event.KeyEvent.PRESS, 0, ' ', 32));
        assertFalse(app.get_is_paused());

        ball.move(app);

        assertNotEquals(initialX, ball.get_position_x(), 0.01);
        assertNotEquals(initialY, ball.get_position_y(), 0.01);

        app.keyPressed(new processing.event.KeyEvent(null, 0, processing.event.KeyEvent.PRESS, 0, ' ', 32));
        assertTrue(app.get_is_paused());

        assertEquals(2, ball.get_speed_x(), 0.01);
        assertEquals(2, ball.get_speed_y(), 0.01);
    }

    /**
     * Tests the game over condition when time runs out.
     * Verifies that the game is correctly marked as lost when the time reaches
     * zero.
     */
    @Test
    void game_over_test() {
        app.set_time(0);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 3000) {
            app.draw();
        }

        assertTrue(app.get_is_lost(), "Game should be lost after time reaches 0");
    }

    /**
     * Tests the game win condition and level progression.
     * Verifies that the game correctly advances to the next level, increases the
     * score,
     * and updates the circular tile animation when a level is completed.
     */
    @Test
    void game_win_test() {
        app.setup();
        app.set_game_levels(new processing.data.JSONArray());
        app.get_game_levels().append(new processing.data.JSONObject());
        app.get_game_levels().append(new processing.data.JSONObject());

        app.set_time(test_time); // Reset time for this test
        int initialLevel = app.get_level_number();
        float initialScore = app.get_score();
        int initialCircularTileSize = app.get_circular_tile_size();

        app.get_balls().clear();
        app.get_candidates_balls().clear();

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < (test_time + 5) * 1000) {
            app.draw();
        }

        assertEquals(initialLevel + 1, app.get_level_number(), "Level should increment after win");
        assertTrue(app.get_score() > initialScore, "Score should increase after win");
        assertFalse(app.get_is_game_ended(), "Game should not end after single level win");
        assertTrue(app.get_circular_tile_size() > initialCircularTileSize,
                "Circular tile animation should have progressed");

    }

    /**
     * Tests the management of candidate balls.
     * Verifies that candidate balls are correctly removed when spawned and that
     * the active ball count increases accordingly.
     */
    @Test
    void candidate_balls_management_test() {
        app.set_is_paused(false);
        app.set_is_lost(false);

        int initialCandidatesCount = app.get_candidates_balls().size();
        System.out.println("Initial candidates count: " + initialCandidatesCount);

        Spawner spawner = app.get_spawner().get(0);
        spawner.spawnBalls(app.get_balls(), app.get_candidates_balls());

        assertEquals(initialCandidatesCount - 1, app.get_candidates_balls().size(),
                "Candidate balls count should decrease by 1");
        assertEquals(2, app.get_balls().size(), "Active balls count should be 2");
    }

    /**
     * Tests the scoring system for balls entering holes.
     * Verifies that the score increases correctly when a ball enters the correct
     * hole,
     * and decreases when a ball enters the wrong hole. Also checks the management
     * of
     * candidate balls in these scenarios.
     */
    @Test
    void testBallScoring() {
        app.set_level_number(0);
        app.setup();
        app.set_is_paused(false);
        app.set_is_lost(false);

        app.get_balls().clear();
        app.get_candidates_balls().clear();

        float initialScore = app.get_score();
        int initialCandidateBallsCount = app.get_candidates_balls().size();

        Ball ball1 = new Ball(16, 15, '2', app);
        ball1.set_speed_x(0);
        ball1.set_speed_y(1);
        app.get_balls().add(ball1);

        long startTime = System.currentTimeMillis();
        while (app.get_balls().size() > 0) {
            app.draw();
            if (System.currentTimeMillis() - startTime > 5000) {
                fail("Timeout waiting for ball1 to enter hole");
            }
        }

        float expectedScore1 = initialScore
                + app.getScoreHoleCapture('2') * app.get_scoreIncreaseFromHoleCaptureModifier();
        assertEquals(expectedScore1, app.get_score(), 0.1,
                "Score should increase correctly for ball entering correct hole");
        assertEquals(initialCandidateBallsCount, app.get_candidates_balls().size(),
                "Candidate balls count should not change");

        Ball ball2 = new Ball(13, 8, '1', app);
        ball2.set_speed_x(0);
        ball2.set_speed_y(1);
        app.get_balls().add(ball2);

        startTime = System.currentTimeMillis();
        while (app.get_balls().size() > 0) {
            app.draw();
            if (System.currentTimeMillis() - startTime > 5000) {
                fail("Timeout waiting for ball2 to enter hole");
            }
        }

        assertEquals(initialCandidateBallsCount + 1, app.get_candidates_balls().size(),
                "A new candidate ball should be added");
        float expectedScore2 = expectedScore1
                - app.getScoreWrongHole('1') * app.get_scoreDecreaseFromWrongHoleModifier();
        assertEquals(expectedScore2, app.get_score(), 0.1,
                "Score should decrease correctly for ball entering wrong hole");

        Ball newCandidateBall = app.get_candidates_balls().get(app.get_candidates_balls().size() - 1);
        assertEquals('1', newCandidateBall.get_type(), "New candidate ball should be of type '1'");
    }
}

// gradle run Run the program
// gradle test Run the testcases

// Please ensure you leave comments in your testcases explaining what the
// testcase is testing.
// Your mark will be based off the average of branches and instructions code
// coverage.
// To run the testcases and generate the jacoco code coverage report:
// gradle test jacocoTestReport
