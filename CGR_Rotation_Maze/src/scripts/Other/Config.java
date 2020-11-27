package scripts.Other;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.HashSet;

public class Config {

    /**
     * Canvas Width
     */
    public static final int CANVAS_WIDTH = 1000;

    /**
     * Canvas Height
     */
    public static final int CANVAS_HEIGHT = 1000;

    /**
     * KeySet
     */
    public static final HashSet<KeyCode> keySet = new HashSet<>(Arrays.asList(KeyCode.A, KeyCode.D, KeyCode.S));

    /**
     * Maze color
     */
    public static final Color MAZE_COLOR = new Color(0, 0, 0, 1);

    /**
     * Background color
     */
    public static final Color BACKGROUND_COLOR = new Color(111.0 / 255.0, 183.0 / 255.0, 214.0 / 255.0, 1);

    /**
     * Frame color
     */
    public static final Color FRAME_COLOR = new Color(0, 0, 0, 1);

    /**
     * Flip timer Circle
     */
    public static double flipTimer = 0.0;

    /**
     * Timer variable
     */
    public static boolean allowFlip = true;

    /**
     * Winning condition
     */
    public static boolean win = false;
}
