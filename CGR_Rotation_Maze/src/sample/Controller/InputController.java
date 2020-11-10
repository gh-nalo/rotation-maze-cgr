package sample.Controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.App.Main;
import sample.Other.Config;

import java.util.HashSet;

@SuppressWarnings("ALL")
public class InputController {

    /**
     * Instance
     */
    private static InputController inputController;

    /**
     * Currently pressed keys
     */
    private final HashSet<KeyCode> currentKeys = new HashSet<>();

    /**
     * Event handler for key press
     */
    public EventHandler onKeyPressed = (EventHandler<KeyEvent>) event -> {
        if (Config.keySet.contains(event.getCode())) {
            currentKeys.add(event.getCode());
        }
    };

    /**
     * Event handler for key release
     */
    public EventHandler onKeyReleased = (EventHandler<KeyEvent>) event -> currentKeys.remove(event.getCode());

    /**
     * Constructor
     */
    private InputController() {
        Main.canvas.setOnKeyPressed(onKeyPressed);
        Main.canvas.setOnKeyReleased(onKeyReleased);
    }

    /**
     * Return instance of singleton class
     *
     * @return singleton instance
     */
    public static InputController getInstance() {
        if (inputController == null)
            inputController = new InputController();
        return inputController;
    }

    /**
     * Return Set of currently pressed keys
     *
     * @return currently pressed keys
     */
    public HashSet<KeyCode> getCurrentKeys() {
        return currentKeys;
    }
}
