package sample.App;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import sample.Controller.InputController;
import sample.GameObject.Circle;
import sample.GameObject.LineSegment;
import sample.Other.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Game {

    /**
     * Collection of balls
     */
    public static final HashSet<Circle> circles = new HashSet<>();

    /**
     * Collection of line segments
     */
    public static final HashSet<LineSegment> lineSegments = new HashSet<>();

    /**
     * Game state
     */
    public static boolean paused;

    /**
     * Canvas graphics context
     */
    private final GraphicsContext gc;

    /**
     * Background image
     */
    private final Image image;

    /**
     * Initialize game objects and components
     *
     * @param gc Graphics context of the canvas
     */
    public Game(GraphicsContext gc) {
        this.gc = gc;
        paused = false;

        // Initialize InputController
        InputController.getInstance();

        image = LoadBackgroundImage();

        InitializeMap();
        InitializeBalls();
        InitializeLineSegments();
    }

    /**
     * Game Loop
     */
    public void start() {
        new AnimationTimer() {

            double pastTick = System.nanoTime();

            double secondCheck = 0;

            int frameCount = 0;

            @Override
            public void handle(long currentNanoTime) {
                double delta = ((double) currentNanoTime - pastTick) / 1e9;

                // --- Clear Screen ---
                gc.clearRect(0, 0, Config.CANVAS_WIDTH, Config.CANVAS_HEIGHT);
                gc.drawImage(image, 0, 0, Config.CANVAS_WIDTH, Config.CANVAS_HEIGHT);

                // --- Update ---
                if (!paused && !Config.win) {
                    for (Circle pb : circles)
                        pb.update(delta);

                    for (LineSegment ls : lineSegments)
                        ls.update(delta);

                    if (Config.flipTimer > 0.01)
                        Config.allowFlip = false;
                }

                // --- Render ---
                for (Circle pb : circles)
                    pb.draw(gc);

                for (LineSegment ls : lineSegments)
                    ls.draw(gc);

                // --- Frame Count And Update Flip Timer ---
                secondCheck += delta;
                if (secondCheck >= 1.0f) {
                    Main.fps.setText("FPS: " + frameCount);
                    secondCheck = 0;
                    frameCount = 0;
                }
                Config.flipTimer = (Config.flipTimer < 0.0) ? 0.0 : Config.flipTimer - delta;
                if (Config.flipTimer > -0.01 && Config.flipTimer < 0.01)
                    Config.allowFlip = true;
                pastTick = currentNanoTime;
                frameCount++;
            }
        }.start();
    }

    /**
     * Initialize player ball and exit dot
     */
    private void InitializeBalls() {
        circles.add(new Circle(216, 216, 7, Color.RED, 0, 0, 100.0, false));
        circles.add(new Circle(1000 - 225, 1000 - 219, 7, Color.GREEN, 0, 0, 100.0, true));
    }

    /**
     * Initialize borders
     */
    private void InitializeLineSegments() {
        // Borders
        double limiter = (double) Config.CANVAS_WIDTH / 5;
        double frame_radius = 1;
        lineSegments.add(new LineSegment(0 + limiter, 0 + limiter, Config.CANVAS_WIDTH - limiter, 0 + limiter, frame_radius, false, Config.FRAME_COLOR)); // Top
        lineSegments.add(new LineSegment(Config.CANVAS_WIDTH - limiter, 0 + limiter, Config.CANVAS_WIDTH - limiter, Config.CANVAS_HEIGHT - limiter, frame_radius, false, Config.FRAME_COLOR)); // Right
        lineSegments.add(new LineSegment(0 + limiter, Config.CANVAS_HEIGHT - limiter, Config.CANVAS_WIDTH - limiter - 50, Config.CANVAS_HEIGHT - limiter, frame_radius, false, Config.FRAME_COLOR)); // Bottom
        lineSegments.add(new LineSegment(0 + limiter, 0 + limiter, 0 + limiter, Config.CANVAS_HEIGHT - limiter, frame_radius, false, Config.FRAME_COLOR)); // Left
    }

    /**
     * Initialize maze (290x290)
     */
    private void InitializeMap() {
        String path = "src/maze.png";
        BufferedImage bf = null;

        try {
            bf = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bf != null) {
            int val1 = -1;
            int val2 = -1;
            for (int row = 0; row < bf.getHeight(); row++) {
                for (int col = 0; col < bf.getWidth(); col++) {
                    // Get horizontal lines
                    if (bf.getRGB(col, row) == -16777216 && val1 != -1) {
                        val2 = col;
                    }

                    if (bf.getRGB(col, row) == -16777216 && val1 == -1 && col != bf.getWidth() - 1 && bf.getRGB(col + 1, row) != 0) {
                        val1 = col;
                    }

                    if (bf.getRGB(col, row) == 0 && val2 != -1) {
                        lineSegments.add(new LineSegment(val1, row, val2, row, 0.1, true, Config.MAZE_COLOR));
                        val1 = -1;
                        val2 = -1;
                    }
                }
                // Full horizontal line for row
                if (val1 != val2 && val1 * val2 >= 0) {
                    lineSegments.add(new LineSegment(val1, row, val2, row, 0.1, true, Config.MAZE_COLOR));
                    val1 = -1;
                    val2 = -1;
                }
            }

            val1 = -1;
            val2 = -1;

            for (int col = 0; col < bf.getWidth(); col++) {
                for (int row = 0; row < bf.getHeight(); row++) {
                    // Get vertical lines
                    if (bf.getRGB(col, row) == -16777216 && val1 != -1) {
                        val2 = row;
                    }

                    if (bf.getRGB(col, row) == -16777216 && val1 == -1 && row != bf.getHeight() - 1 && bf.getRGB(col, row + 1) != 0) {
                        val1 = row;
                    }

                    if (bf.getRGB(col, row) == 0 && val2 != -1) {
                        lineSegments.add(new LineSegment(col, val1, col, val2, 0.1, true, Config.MAZE_COLOR));
                        val1 = -1;
                        val2 = -1;
                    }
                }
                // Full vertical line for row
                if (val1 != val2 && val1 * val2 >= 0) {
                    lineSegments.add(new LineSegment(col, val1, col, val2, 0.1, true, Config.MAZE_COLOR));
                    val1 = -1;
                    val2 = -1;
                }
            }
        }
    }

    /**
     * Load background image
     *
     * @return Image as JavaFX image
     */
    private Image LoadBackgroundImage() {
        String path = "src/dem_arrows.png";
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image image = null;

        if (bi != null) {
            image = SwingFXUtils.toFXImage(bi, null);
        }

        return image;
    }
}
