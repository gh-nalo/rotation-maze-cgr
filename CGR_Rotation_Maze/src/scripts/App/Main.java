package scripts.App;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scripts.Other.Config;

public class Main extends Application {

    /**
     * Canvas
     */
    public static final Canvas canvas = new Canvas(Config.CANVAS_WIDTH, Config.CANVAS_HEIGHT);

    /**
     * Fps counter
     */
    public static Label fps;

    /**
     * Canvas' graphics context for drawing
     */
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    /**
     * Launch the application
     *
     * @param args startup arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Set up application layout and start application
     *
     * @param primaryStage stage to be shown that contains the graphics user interface
     */
    @Override
    public void start(Stage primaryStage) {

        // Layout
        BorderPane borderPane = new BorderPane();

        // Canvas
        borderPane.setCenter(canvas);
        borderPane.setBackground(new Background(new BackgroundFill(Config.BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));

        canvas.setFocusTraversable(true);

        // Top menu right side
        HBox topMenuRight = new HBox();
        topMenuRight.setPadding(new Insets(16, 12, 16, 12));
        topMenuRight.setSpacing(12);

        Button pauseButton = new Button("Pause");
        pauseButton.setOnMouseClicked(mouseEvent -> {
            Game.paused = !Game.paused;
            if (!Game.paused)
                canvas.requestFocus();
        });
        topMenuRight.getChildren().add(pauseButton);

        // Top menu left side
        HBox topMenuLeft = new HBox();
        topMenuLeft.setPadding(new Insets(16, 12, 16, 12));
        topMenuLeft.setSpacing(12);

        fps = new Label("FPS: ");
        fps.setFont(new Font("Arial", 20));
        topMenuLeft.getChildren().add(fps);

        // Top menu
        BorderPane topMenu = new BorderPane();
        topMenu.setBackground(new Background(new BackgroundFill(Config.BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        borderPane.setTop(topMenu);
        topMenu.setRight(topMenuRight);
        topMenu.setLeft(topMenuLeft);

        // Set up stage
        primaryStage.setTitle("Computer Graphics Project");
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();

        new Game(gc).start();
    }
}