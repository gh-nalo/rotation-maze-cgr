package sample.GameObject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import sample.App.Game;
import sample.Controller.InputController;
import sample.Other.Config;

import java.util.ArrayList;

public class Circle {

    /**
     * Mid point of the canvas on x axis
     */
    private static final double midPointX = (double) Config.CANVAS_WIDTH / 2;

    /**
     * Mid point of the canvas on y axis
     */
    private static final double midPointY = (double) Config.CANVAS_HEIGHT / 2;

    /**
     * Amount of physics adjustments per update
     */
    private static final int nSimulationUpdates = 4;

    /**
     * Amount of steps for physics adjustments
     */
    private static final int maxSimulationSteps = 15;

    /**
     * Gravity factor
     */
    private static final double gravityFactor = 600.0f;

    /**
     * Input controller
     */
    private final InputController inputController = InputController.getInstance();

    /**
     * Color of the game object
     */
    private final Color color;

    /**
     * List of circles that collide with this circle
     */
    private final ArrayList<Circle> collidedCircles = new ArrayList<>();

    /**
     * List of simulated line-segment circles that collide with this circle
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Circle> fakeBalls = new ArrayList<>();

    /**
     * Mass of the player object
     */
    private final double mass;

    /**
     * Radius of the game object
     */
    private final double radius;

    /**
     * Boolean if circle physics should be updated
     */
    private final boolean isMarker;

    /**
     * x position of the circle
     */
    private double px;

    /**
     * y position of the circle
     */
    private double py;

    /**
     * old x position before updates
     */
    private double ox;

    /**
     * old y position before updates
     */
    private double oy;

    /**
     * x- and y-velocity
     */
    private double vx, vy;

    /**
     * x- and y-acceleration
     */
    private double ax, ay;

    /**
     * Remaining time for physics updates
     */
    private double simTimeRemaining;

    /**
     * Constructor
     *
     * @param x        x pos
     * @param y        y pos
     * @param radius   radius
     * @param color    color
     * @param vx       x velocity
     * @param vy       y velocity
     * @param mass     mass
     * @param isMarker updated physics
     */
    public Circle(double x, double y, double radius, Color color, double vx, double vy, double mass, boolean isMarker) {
        this.px = x;
        this.py = y;
        this.radius = radius;
        this.color = color;
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
        this.isMarker = isMarker;
    }

    /**
     * Updates per frame
     *
     * @param deltaTime Passed time since last frame
     */
    public void update(double deltaTime) {
        if (!inputController.getCurrentKeys().isEmpty()) {
            if (inputController.getCurrentKeys().contains(KeyCode.A))
                this.rotateAroundMidPoint(false, false);

            if (inputController.getCurrentKeys().contains(KeyCode.D))
                this.rotateAroundMidPoint(true, false);

            if (inputController.getCurrentKeys().contains(KeyCode.S) && ((Config.flipTimer > -0.01 && Config.flipTimer < 0.01) || Config.allowFlip)) {
                Config.flipTimer = 0.5;
                this.rotateAroundMidPoint(true, true);
            }
        }

        if (this.isMarker)
            return;

        double simElapsedTime = deltaTime / (double) nSimulationUpdates;

        for (int i = 0; i < nSimulationUpdates; i++) {
            this.simTimeRemaining = simElapsedTime;

            for (int j = 0; j < maxSimulationSteps; j++) {

                if (simTimeRemaining > 0.0) {
                    this.ox = this.px;
                    this.oy = this.py;

                    // Update vectors
                    this.vx += this.ax * this.simTimeRemaining;
                    this.vy += this.ay * this.simTimeRemaining;
                    this.px += this.vx * this.simTimeRemaining;
                    this.py += this.vy * this.simTimeRemaining;

                    // Wrap the balls around screen
                    if (this.px < 0) this.px += Config.CANVAS_WIDTH;
                    if (this.px >= Config.CANVAS_WIDTH) this.px -= Config.CANVAS_WIDTH;
                    if (this.py < 0) this.py += Config.CANVAS_HEIGHT;
                    if (this.py >= Config.CANVAS_HEIGHT) this.py -= Config.CANVAS_HEIGHT;

                    // Clamp velocity near zero
                    if (Math.abs(this.vx * this.vx + this.vy * this.vy) < 0.01f) {
                        this.vx = 0;
                        this.vy = 0;
                    }
                }

                // Update acceleration
                this.ax = -this.vx * 0.8;
                this.ay = -this.vy * 0.8 + gravityFactor;

                this.handleCollisions();
            }
        }

        if (this.px > Config.CANVAS_HEIGHT)
            Config.win = true;
    }

    /**
     * Draw circle
     *
     * @param gc Graphics context of canvas to draw on
     */
    public void draw(GraphicsContext gc) {
        gc.setFill(this.color);
        gc.fillOval(this.px - this.radius, this.py - radius, this.radius * 2, this.radius * 2);
    }

    /**
     * Handles collisions with other circles
     */
    private void handleCollisions() {
        // Edge collisions
        for (LineSegment ls : Game.lineSegments) {
            // Check that line formed by velocity vector, intersects with line segment
            double fLineX1 = ls.ex - ls.sx;
            double fLineY1 = ls.ey - ls.sy;

            double fLineX2 = this.px - ls.sx;
            double fLineY2 = this.py - ls.sy;

            double fEdgeLength = fLineX1 * fLineX1 + fLineY1 * fLineY1;

            double t = Math.max(0, Math.min(fEdgeLength, (fLineX1 * fLineX2 + fLineY1 * fLineY2))) / fEdgeLength;

            double fClosestPointX = ls.sx + t * fLineX1;
            double fClosestPointY = ls.sy + t * fLineY1;

            double fDistance = Math.sqrt((this.px - fClosestPointX) * (this.px - fClosestPointX) + (this.py - fClosestPointY) * (this.py - fClosestPointY));

            if (fDistance <= (this.radius + ls.radius)) {
                Circle fakeBall = new Circle(fClosestPointX, fClosestPointY, ls.radius, Color.BLACK, -this.vx, -this.vy, this.mass * 0.8, false);

                fakeBalls.add(fakeBall);

                collidedCircles.add(fakeBall);

                double fOverlap = 1.0f * (fDistance - this.radius - fakeBall.radius);

                this.px -= fOverlap * (this.px - fakeBall.px) / fDistance;
                this.py -= fOverlap * (this.py - fakeBall.py) / fDistance;
            }
        }

        // Static collisions
        for (Circle circle : Game.circles) {
            if (circle != this) {
                if (doCirclesCollide(circle.px, circle.py, circle.radius)) {
                    collidedCircles.add(circle);

                    // Distance between ball centers
                    double fDistance = Math.sqrt((this.px - circle.px) * (this.px - circle.px) + (this.py - circle.py) * (this.py - circle.py));

                    // Calculate displacement required
                    double fOverlap = 0.5f * (fDistance - this.radius - circle.radius);

                    // Displace Current Ball away from collision
                    this.px -= fOverlap * (this.px - circle.px) / fDistance;
                    this.py -= fOverlap * (this.py - circle.py) / fDistance;

                    // Displace Target Ball away from collision
                    circle.px += fOverlap * (this.px - circle.px) / fDistance;
                    circle.py += fOverlap * (this.py - circle.py) / fDistance;

                }
            }

            // Time displacement
            double intendedSpeed = Math.sqrt(this.vx * this.vx + this.vy * this.vy);
            double actualDistance = Math.sqrt((this.px - this.ox) * (this.px - this.ox) + (this.py - this.oy) * (this.py - this.oy));
            double actualTime = actualDistance / intendedSpeed;

            this.simTimeRemaining = this.simTimeRemaining - actualTime;
        }

        // Dynamic collisions
        for (Circle circle : collidedCircles) {
            // Distance between balls
            double fDistance = Math.sqrt((this.px - circle.px) * (this.px - circle.px) + (this.py - circle.py) * (this.py - circle.py));

            // Normal
            double nx = (circle.px - this.px) / fDistance;
            double ny = (circle.py - this.py) / fDistance;

            // Tangent
            double tx = -ny;
            double ty = nx;

            // Dot Product Tangent
            double dpTan1 = this.vx * tx + this.vy * ty;
            double dpTan2 = circle.vx * tx + circle.vy * ty;

            // Dot Product Normal
            double dpNorm1 = this.vx * nx + this.vy * ny;
            double dpNorm2 = circle.vx * nx + circle.vy * ny;

            // Conservation of momentum in 1D
            double m1 = (dpNorm1 * (this.mass - circle.mass) + 1.35f * circle.mass * dpNorm2) / (this.mass + circle.mass);
            double m2 = (dpNorm2 * (circle.mass - this.mass) + 1.35f * this.mass * dpNorm1) / (this.mass + circle.mass);

            // Update ball velocities
            this.vx = tx * dpTan1 + nx * m1;
            this.vy = ty * dpTan1 + ny * m1;
            circle.vx = tx * dpTan2 + nx * m2;
            circle.vy = ty * dpTan2 + ny * m2;
        }

        collidedCircles.clear();
        fakeBalls.clear();
    }

    /**
     * Helper method for collisions
     *
     * @param xC x position of other circle
     * @param xY y position of other circle
     * @param rC radius of other circle
     * @return boolean - collision detected
     */
    private boolean doCirclesCollide(double xC, double xY, double rC) {
        return Math.abs((xC - this.px) * (xC - this.px) + (xY - this.py) * (xY - this.py)) <= (this.radius + rC) * (this.radius + rC);
    }

    /**
     * Rotate player around midpoint
     *
     * @param clockWise Rotation direction
     * @param flip      Flip or rotate
     */
    private void rotateAroundMidPoint(boolean clockWise, boolean flip) {
        double rotation = 0.01;

        if (!clockWise)
            rotation *= (-1);

        if (flip)
            rotation = Math.PI;

        this.px = midPointX + (this.px - midPointX) * Math.cos(rotation) - (this.py - midPointY) * Math.sin(rotation);
        this.py = midPointY + (this.px - midPointX) * Math.sin(rotation) + (this.py - midPointY) * Math.cos(rotation);
    }
}