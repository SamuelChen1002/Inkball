/**
 * IMPORTANT NOTE FOR EXAMINERS AND USERS:
 * 
 * The game's speed is controlled by the 'timeScale' variable (line 46).
 * Due to performance variations across different computers, this value may need adjustment.
 * 
 * On the developer's laptop, a high value (100.0f) was necessary for smooth gameplay.
 * However, on more powerful systems, this may cause the game to run too quickly.
 * 
 * If you experience issues with game speed:
 * 1. Locate the 'timeScale' variable (currently on line 44).
 * 2. Adjust its value within the range of 1.0f to 100.0f.
 * 3. Lower values will slow down the game, higher values will speed it up.
 * 
 * Please adjust this value as needed to ensure a playable experience on your system.
 * The optimal setting may vary depending on your computer's performance.
 */

package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

/**
 * The main application class for the Inkball game.
 * This class extends PApplet and handles the game logic, rendering, and user
 * interactions.
 */

public class App extends PApplet {

    // If you want to test, please change spawn_interval and FPS.

    private float timeScale = 100.0f; // recommended value: 1.0f ~ 100.0f

    // change the speed of the timer in candidate_timer.
    // If the candidate_balls_timer or any other thing is too fast,
    // please decrease the timeScale.

    private int level_number = 0;// It should be (expected level number - 1).
    // Please change the level number here.

    public static final int CELLSIZE = 32; // 8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 640; // 576
    public static int HEIGHT = 704; // 640
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;// change it!

    public static final int Tile_SIZE = 18;

    public String configPath;

    public static Random random = new Random();

    private PImage initial_background;
    private PImage last_background;
    private float score = 0;
    public Tile[][] board;
    private ArrayList<Ball> balls;
    private ArrayList<Ball> candidates_balls;
    private ArrayList<Hole> holes;
    private ArrayList<Spawner> spawners;

    private HashMap<Character, Float> scoreFromHoleCapture;
    private HashMap<Character, Float> scoreFromWrongHole;

    public ArrayList<Line> Linelist;
    public Line drawing_line;
    public boolean is_drawing;

    // timer

    private int time;
    private float spawn_interval;
    private float spawn_timer;
    private long last_time;

    private float time_count;
    private long last_time_count;
    private long last_time_change_tile;

    private PImage tileImage;
    private PImage[] wallImages;
    private PImage entrypointImage;
    private PImage[] holeImages;
    private PImage[] ballImages;

    private boolean is_restarted;
    private boolean is_paused;
    private boolean is_lost;
    private boolean is_game_ended = false;

    private boolean is_won;

    private boolean first_frame_flag = true;
    private boolean first_frame_flag_last_background;

    private int circular_tile[][];
    private int circular_tile_size;

    private String level_file;

    private JSONArray levels;

    private float scoreIncreaseFromHoleCaptureModifier;
    private float scoreDecreaseFromWrongHoleModifier;

    // Feel free to add any additional methods or attributes you want. Please put
    // classes in different files.

    /**
     * Constructs an App instance with the default configuration file path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initializes the window size settings for the game.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Processes the JSON configuration file and initializes game settings.
     * This method loads level data, time limits, scoring rules, and other game
     * parameters.
     */

    public void processJsonFile() {
        JSONObject config = loadJSONObject(configPath);
        levels = config.getJSONArray("levels");
        JSONObject level_n = levels.getJSONObject(level_number);
        level_file = level_n.getString("layout");

        time = level_n.getInt("time");

        spawn_interval = level_n.getFloat("spawn_interval");
        // spawn_interval = 0.1f;
        spawn_timer = spawn_interval;

        scoreIncreaseFromHoleCaptureModifier = level_n.getFloat("score_increase_from_hole_capture_modifier");
        scoreDecreaseFromWrongHoleModifier = level_n.getFloat("score_decrease_from_wrong_hole_modifier");

        scoreFromHoleCapture = new HashMap<>();
        JSONObject increaseScores = config.getJSONObject("score_increase_from_hole_capture");
        for (Object keyObj : increaseScores.keys()) {
            String Key = keyObj.toString();
            char typeChar = getBallTypeFromColor(Key);
            int scoreInt = increaseScores.getInt(Key);
            float scoreFloat = (float) scoreInt;
            scoreFromHoleCapture.put(typeChar, scoreFloat);
        }

        scoreFromWrongHole = new HashMap<>();
        JSONObject decreaseScores = config.getJSONObject("score_decrease_from_wrong_hole");
        for (Object keyObj : decreaseScores.keys()) {
            String key = keyObj.toString();
            char typeChar = getBallTypeFromColor(key);
            int scoreInt = decreaseScores.getInt(key);
            float scoreFloat = (float) scoreInt;
            scoreFromWrongHole.put(typeChar, scoreFloat);
        }

        JSONArray candidateBallColors = level_n.getJSONArray("balls");
        int x_of_ball = 40;
        for (int i = 0; i < candidateBallColors.size(); i++) {
            String candidate_ballColor = candidateBallColors.getString(i);
            char candidate_ballType = getBallTypeFromColor(candidate_ballColor);

            Ball b = new Ball(0, 0, candidate_ballType, this);
            b.set_x(x_of_ball);
            b.set_y(20);
            candidates_balls.add(b);
            x_of_ball += CELLSIZE;

        }
    }

    /**
     * Converts a color string to the corresponding ball type character.
     *
     * @param color The color string to convert.
     * @return The character representing the ball type.
     */

    private char getBallTypeFromColor(String color) {
        switch (color.toLowerCase()) {
            case "grey":
                return '0';
            case "orange":
                return '1';
            case "blue":
                return '2';
            case "green":
                return '3';
            case "yellow":
            default:
                return '4';
        }
    }

    /**
     * Reads the level layout from a file and initializes the game board.
     *
     * @param level_file The path to the level file.
     */

    public void readLevel(String level_file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(level_file));
            String line = reader.readLine();
            int row1 = 0;
            while (line != null) {
                for (int col1 = 0; col1 < line.length(); col1++) {
                    char tile_type = line.charAt(col1);
                    Tile tile = new Tile(col1, row1, tile_type);
                    board[row1][col1] = tile;

                    if (tile_type == 'B') {
                        Ball b = new Ball(col1, row1, line.charAt(col1 + 1), this);
                        balls.add(b);
                        Tile colorTile = new Tile(col1 + 1, row1, ' ');
                        board[row1][col1 + 1] = colorTile;
                        col1++;
                    } else if (tile_type == 'H') {
                        char holeType = line.charAt(col1 + 1);
                        Hole h = new Hole(col1, row1, holeType);
                        tile.set_hole();
                        holes.add(h);
                        Tile colorTile = new Tile(col1 + 1, row1, ' ');
                        board[row1][col1 + 1] = colorTile;
                        col1++;
                    } else if (tile_type == 'S') {
                        Spawner s = new Spawner(col1, row1, line.charAt(col1 + 1), this);
                        tile.set_spawner();
                        spawners.add(s);
                        Tile colorTile = new Tile(col1 + 1, row1, ' ');
                        board[row1][col1 + 1] = colorTile;
                        col1++;
                    } else if (tile_type == 'X' || tile_type == '1' || tile_type == '2' || tile_type == '3'
                            || tile_type == '4') {
                        tile.set_wall();
                    }
                }
                row1++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the game by initializing resources, game elements, and loading the
     * first level.
     */
    @Override
    public void setup() {
        is_won = false;
        is_lost = false;
        is_game_ended = false;
        frameRate(FPS);
        board = new Tile[Tile_SIZE][Tile_SIZE];
        balls = new ArrayList<>();
        holes = new ArrayList<>();
        spawners = new ArrayList<>();
        candidates_balls = new ArrayList<>();

        first_frame_flag_last_background = true;

        last_time = millis();
        last_time_count = millis();
        last_time_change_tile = millis();

        is_restarted = false;
        is_paused = false;

        if (first_frame_flag) {
            initial_background = get();
            first_frame_flag = false;
        }

        processJsonFile();
        readLevel(level_file);

        Linelist = new ArrayList<Line>();
        is_drawing = false;

        // We need to store x, y coordinates, that's why we need [2] here.
        circular_tile = new int[68][2];
        for (int i = 0; i < 18; i++) {
            circular_tile[i][0] = 0;
            circular_tile[i][1] = i;
        }

        // the most right edge.

        int i1 = 1;
        for (int i = 18; i < 35; i++) {
            circular_tile[i][0] = i1;
            circular_tile[i][1] = 17;
            i1++;
        }

        // the most bottom edge.

        int i2 = 16;
        for (int i = 35; i < 52; i++) {
            circular_tile[i][0] = 17;
            circular_tile[i][1] = i2;
            i2--;
        }

        // the most left edge.

        int i3 = 16;
        for (int i = 52; i < 68; i++) {
            circular_tile[i][0] = i3;
            circular_tile[i][1] = 0;
            i3--;
        }

        tileImage = loadImage("inkball/tile.png");

        wallImages = new PImage[5];
        for (int i = 0; i < 5; i++) {
            wallImages[i] = loadImage("inkball/wall" + i + ".png");
        }

        entrypointImage = loadImage("inkball/entrypoint.png");

        holeImages = new PImage[5];
        for (int i = 0; i < 5; i++) {
            holeImages[i] = loadImage("inkball/hole" + i + ".png");
        }

        ballImages = new PImage[5];
        for (int i = 0; i < 5; i++) {
            ballImages[i] = loadImage("inkball/ball" + i + ".png");
        }
    }

    /**
     * Handles key press events.
     *
     * @param event The KeyEvent object containing information about the key press.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (key == 'r') {
            is_restarted = true;
        } else if (key == ' ') {
            is_paused = !is_paused;
            if (!is_paused) {
                redraw();
            }
        }
    }

    /**
     * Removes lines that intersect with the currently drawing line.
     * This method is used to clean up overlapping lines during drawing.
     */

    private void removeIntersectingLines() {
        Iterator<Line> iterator = Linelist.iterator();
        while (iterator.hasNext()) {
            Line line = iterator.next();
            if (line.intersects(drawing_line)) {
                iterator.remove();
            }
        }
    }

    /**
     * Handles key release events.
     */
    @Override
    public void keyReleased() {

    }

    /**
     * Handles mouse press events.
     *
     * @param e The MouseEvent object containing information about the mouse press.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
        if (!is_paused && !is_lost) {
            is_drawing = true;
            ArrayList<int[]> points = new ArrayList<>();
            drawing_line = new Line(points);
            // Notice that: it's previous x and y, current x and y.
            drawing_line.points.add(new int[] { pmouseX, pmouseY, mouseX, mouseY });
        }
    }

    /**
     * Handles mouse drag events.
     *
     * @param e The MouseEvent object containing information about the mouse drag.
     */

    @Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held

        // remove player-drawn line object if right mouse button is held
        // and mouse position collides with the line
        if (!is_paused && is_drawing && !is_lost) {
            drawing_line.points.add(new int[] { pmouseX, pmouseY, mouseX, mouseY });
            if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
                removeIntersectingLines();
            }
        }
    }

    /**
     * Handles mouse release events.
     *
     * @param e The MouseEvent object containing information about the mouse
     *          release.
     */

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!is_paused && is_drawing && !is_lost) {
            is_drawing = false;
            if (e.getButton() == LEFT && !e.isControlDown()) {
                Linelist.add(drawing_line);
            }
            drawing_line = null;
        }
    }

    /**
     * Draws the game board, including walls, tiles, and other static elements.
     */
    public void drawBoard() {
        for (int i = 0; i < Tile_SIZE; i++) {
            for (int j = 0; j < Tile_SIZE; j++) {
                Tile t = board[i][j];
                if (t == null) {
                    continue;
                }
                PImage imageToDraw = tileImage;
                if (!t.is_ball && !t.is_hole) {
                    if (t.get_type() == 'X') {
                        imageToDraw = wallImages[0];
                    } else if (t.get_type() == '1') {
                        imageToDraw = wallImages[1];
                    } else if (t.get_type() == '2') {
                        imageToDraw = wallImages[2];
                    } else if (t.get_type() == '3') {
                        imageToDraw = wallImages[3];
                    } else if (t.get_type() == '4') {
                        imageToDraw = wallImages[4];
                    } else if (t.get_type() == 'B') {
                        Tile nextTile = board[i][j + 1];
                        if (nextTile != null) {
                            nextTile.set_ball();
                        }
                    } else if (t.get_type() == 'S') {
                        imageToDraw = entrypointImage;
                    }
                }
                image(imageToDraw, t.x, t.y);
            }
        }
    }

    /**
     * Draws the holes on the game board.
     */
    public void drawHoles() {
        for (Hole h : holes) {
            char holeType = h.get_hole_type();
            int index = holeType - '0';
            if (index >= 0 && index < holeImages.length) {
                image(holeImages[index], h.x, h.y);
            }
        }
    }

    /**
     * Gets the image file name for a given ball type.
     *
     * @param type The character representing the ball type.
     * @return The file name of the corresponding ball image.
     */
    public String getBallImageName(char type) {
        switch (type) {
            case '0':
                return "inkball/ball0.png";
            case '1':
                return "inkball/ball1.png";
            case '2':
                return "inkball/ball2.png";
            case '3':
                return "inkball/ball3.png";
            case '4':
                return "inkball/ball4.png";
            default:
                return "inkball/ball0.png";
        }
    }

    /**
     * Draws and updates the balls on the game board.
     */
    public void drawBalls() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball b = iterator.next();
            String ball_type = getBallImageName(b.type);
            PImage ballImage = loadImage(ball_type);
            image(ballImage, b.x, b.y);
            b.move(this);
            if (b.in_hole_flag) {
                iterator.remove();
            }
        }
    }

    /**
     * Loads and updates the top bar of the game screen.
     */
    public void loadTopBar() {
        // initialize the top bar
        image(initial_background, 0, 0);
        fill(0);
        textSize(15);
        text("Score: " + String.format("%.1f", score), 480, 60);

        text("Time: " + time, 480, 30);
        // update the time
        if (!is_paused && !is_lost) {
            long current_time = millis();
            last_time_count = millis();
            if (current_time - last_time >= 1000) {
                time--;
                last_time = current_time;
            }
        }
    }

    /**
     * Loads and displays the candidate balls at the top of the game screen.
     */
    public void load_candidates_balls() {
        fill(0);
        rect(10, 15, 165, 35);

        fill(0);
        textSize(20);

        if (candidates_balls.size() > 0) {
            text(String.format("%.1f", spawn_interval), 185, 38);
            textSize(15);
            if (!is_paused && !is_lost) {
                long current_time = millis();
                if (current_time - last_time_count >= 100 / timeScale) {
                    spawn_interval -= 0.1f;
                    if (spawn_interval <= 0.0f) {
                        spawn_interval = spawn_timer;
                        int spawn_index = random.nextInt(spawners.size());
                        Spawner s = spawners.get(spawn_index);
                        s.spawnBalls(balls, candidates_balls);
                    }
                    last_time_count = current_time;
                }
            }
            /*
             * float elapsed_time = (current_time - last_time_count) / 1000.0f;
             * 
             * time_count -= elapsed_time * timeScale;
             * 
             * if (time_count <= 0.0f) {
             * time_count = spawn_interval;
             * int spawn_index = random.nextInt(candidates_balls.size());
             * Spawner s = spawners.get(spawn_index);
             * s.spawnBalls(balls, candidates_balls);
             * }
             */

            for (int i = 0; i < candidates_balls.size(); i++) {
                Ball b = candidates_balls.get(i);
                int targetX = (i * 32) + 10;

                if (b.x > targetX) {
                    b.set_speed_x(-1);
                } else {
                    b.set_speed_x(0);
                }

                b.x += b.speedX;

                int ballIndex = b.type - '0';
                if (ballIndex >= 0 && ballIndex < ballImages.length) {
                    PImage ballImage = ballImages[ballIndex];

                    int visible_start_x = Math.max(b.x, 10);
                    int visible_end_x = Math.min(b.x + 32, 175);
                    int visible_width = visible_end_x - visible_start_x;

                    if (visible_width > 0) {
                        int source_start_x = visible_start_x - b.x;
                        image(ballImage, visible_start_x, b.y, visible_width, 32,
                                source_start_x, 0, visible_width, 32);
                    }
                }

                /*
                 * String ball_type = getBallImageName(b.type);
                 * int visible_start_x = b.x;
                 * int visible_width = Math.min(b.x + 32, 165) - visible_start_x;
                 * image(loadImage(ball_type), b.x, b.y, visible_width, 32,
                 * 0, 0, visible_width, 32);
                 */
            }

        }
    }

    /**
     * Draws the player-drawn lines on the game board.
     */
    public void drawLines() {
        for (Line line : Linelist) {
            strokeWeight(10);
            ArrayList<int[]> points = line.points;
            for (int[] point : points) {
                line((float) point[0], (float) point[1], (float) point[2], (float) point[3]);
            }
        }
        if (is_drawing) {
            strokeWeight(10);
            for (int[] point : drawing_line.points) {
                line((float) point[0], (float) point[1], (float) point[2], (float) point[3]);
            }
        }
    }

    /**
     * Draws and updates the circular tile animation.
     * This method is responsible for the rotating tile effect on the game board.
     */
    public void drawCircularTile() {
        if (time <= 0) {
            return;
        }
        int index1[] = circular_tile[circular_tile_size % 67];
        int index2[] = circular_tile[(circular_tile_size + 34) % 67];

        int xIndex1 = index1[1];
        int yIndex1 = index1[0];
        int xIndex2 = index2[1];
        int yIndex2 = index2[0];

        char original_tile_type = board[yIndex1][xIndex1].get_type();
        char new_tile_type = board[yIndex2][xIndex2].get_type();

        int x1 = xIndex1 * CELLSIZE;
        int y1 = (yIndex1 * CELLSIZE) + 64;

        int x2 = xIndex2 * CELLSIZE;
        int y2 = (yIndex2 * CELLSIZE) + 64;

        boolean change_tile = false;
        image(wallImages[4], x1, y1);
        image(wallImages[4], x2, y2);

        long current_time = millis();

        if (current_time - last_time_change_tile >= 67) {
            circular_tile_size++;
            time--;
            score++;
            last_time_change_tile = current_time;
            change_tile = true;
        }

        if (change_tile) {
            // change
            Map<Character, PImage> image1 = new HashMap<>();
            image1.put('1', wallImages[1]);
            image1.put('2', wallImages[2]);
            image1.put('3', wallImages[3]);
            image1.put('4', wallImages[4]);
            image1.put('X', wallImages[0]);

            PImage image_to_draw = image1.get(original_tile_type);
            PImage image_to_draw2 = image1.get(new_tile_type);

            image(image_to_draw, x1, y1);
            image(image_to_draw2, x2, y2);
        }
    }

    /**
     * Checks the game state based on time and ball count.
     * This method determines if the player has won or lost the game.
     */
    public void time_check() {
        if (balls.size() == 0 && candidates_balls.size() == 0) {
            is_won = true;
            is_lost = false;
        } else if (time <= 0) {
            is_lost = true;
            is_won = false;
        }
    }

    @Override
    public void draw() {
        if (is_restarted) {
            restartGame();
        } else if (is_game_ended) {
            drawGameCompletedScreen();
        } else if (is_won) {
            drawWinScreen();
        } else if (is_lost) {
            drawLoseScreen();
        } else if (is_paused) {
            drawPausedScreen();
        } else {
            drawGame();
        }
        time_check();
    }

    /**
     * Restarts the game by resetting all game states and calling setup.
     */
    private void restartGame() {
        setup();
        draw();
        is_restarted = false;
        is_won = false;
        is_lost = false;
        score = 0;
    }

    /**
     * Draws the paused game screen, displaying a "PAUSED" message.
     */
    private void drawPausedScreen() {
        is_drawing = false;
        PImage pausedScreenshot = get();
        image(pausedScreenshot, 0, 0);
        fill(0);
        textSize(24);
        String pausedText = "*** PAUSED ***";
        float textWidth = textWidth(pausedText);
        text(pausedText, WIDTH / 2 - textWidth / 2, TOPBAR / 2 + 12);
    }

    /**
     * Draws the win screen and handles level progression or game completion.
     */
    private void drawWinScreen() {
        if (first_frame_flag_last_background) {
            last_background = get();
            first_frame_flag_last_background = false;
        }
        image(initial_background, 0, 0);
        fill(0);
        textSize(15);
        text("Score: " + String.format("%.1f", score), 480, 60);
        text("Time: " + time, 480, 30);
        image(last_background, 0, 64, 576, 576, 0, 64, 576, 640);

        if (time > 0) {
            drawCircularTile();
        } else {
            if (level_number < levels.size() - 1 && is_won == true) {
                level_number++;
                is_won = false;
                setup();
                draw();
            } else if (level_number >= levels.size() - 1 && is_won == true) {
                is_won = false;
                is_game_ended = true;
                is_lost = false;
            }
            return;
        }
    }

    /**
     * Draws the game completed screen when all levels are finished.
     */
    private void drawGameCompletedScreen() {
        fill(0);
        textSize(24);
        String pausedText = "=== ENDED ===";
        float textWidth = textWidth(pausedText);
        text(pausedText, WIDTH / 2 - textWidth / 2, TOPBAR / 2 + 12);
    }

    /**
     * Draws the lose screen when the player runs out of time.
     */
    private void drawLoseScreen() {
        is_drawing = false;
        PImage pausedScreenshot = get();
        image(pausedScreenshot, 0, 0);
        fill(0);
        textSize(24);
        String pausedText = "=== TIME'S UP ===";
        float textWidth = textWidth(pausedText);
        text(pausedText, WIDTH / 2 - textWidth / 2, TOPBAR / 2 + 12);
    }

    /**
     * Draws the main game screen, including all game elements.
     */
    private void drawGame() {
        loadTopBar();
        load_candidates_balls();
        drawBoard();
        drawHoles();
        drawBalls();
        drawLines();
    }

    /**
     * Updates the circular tile animation and game time.
     */
    public void updateCircularTile() {
        long current_time = millis();
        if (current_time - last_time_change_tile >= 67) {
            circular_tile_size++;
            time--;
            score++;
            last_time_change_tile = current_time;
        }
    }

    /**
     * The main method to start the Inkball game.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

    /**
     * The getter and setter methods for the game state.
     */
    public int get_time() {
        return this.time;
    }

    public ArrayList<Ball> get_balls() {
        return this.balls;
    }

    public Tile[][] get_tiles() {
        return this.board;
    }

    public float get_spawn_interval() {
        return this.spawn_interval;
    }

    public ArrayList<Hole> get_holes() {
        return this.holes;
    }

    public ArrayList<Line> get_lines() {
        return this.Linelist;
    }

    public ArrayList<Spawner> get_spawner() {
        return this.spawners;
    }

    public float get_score() {
        return this.score;
    }

    public void set_score(float score) {
        this.score += score;
    }

    public ArrayList<Ball> get_candidates_balls() {
        return this.candidates_balls;
    }

    public float getScoreHoleCapture(char ballType) {
        return scoreFromHoleCapture.get(ballType);
    }

    public float getScoreWrongHole(char ballType) {
        return scoreFromWrongHole.get(ballType);
    }

    public boolean get_is_paused() {
        return this.is_paused;
    }

    public boolean get_is_lost() {
        return this.is_lost;
    }

    public void set_time(int time) {
        this.time = time;
    }

    public float get_timeScale() {
        return this.timeScale;
    }

    public boolean get_is_game_ended() {
        return this.is_game_ended;
    }

    public void set_game_levels(JSONArray levels) {
        this.levels = levels;
    }

    public JSONArray get_game_levels() {
        return this.levels;
    }

    public int get_level_number() {
        return this.level_number;
    }

    public void set_level_number(int level_number) {
        this.level_number = level_number;
    }

    public int get_circular_tile_size() {
        return this.circular_tile_size;
    }

    public void set_is_paused(boolean is_paused) {
        this.is_paused = is_paused;
    }

    public void set_is_lost(boolean is_lost) {
        this.is_lost = is_lost;
    }

    public void set_is_won(boolean is_won) {
        this.is_won = is_won;
    }

    public boolean get_is_won() {
        return this.is_won;
    }

    public float get_spawn_timer() {
        return this.spawn_timer;
    }

    public float get_time_count() {
        return this.time_count;
    }

    public float get_scoreIncreaseFromHoleCaptureModifier() {
        return this.scoreIncreaseFromHoleCaptureModifier;
    }

    public float get_scoreDecreaseFromWrongHoleModifier() {
        return this.scoreDecreaseFromWrongHoleModifier;
    }

    public App get_this() {
        return this;
    }

}
