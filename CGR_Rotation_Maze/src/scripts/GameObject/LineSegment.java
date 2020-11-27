package scripts.GameObject;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import scripts.Controller.InputController;
import scripts.Other.Config;

public class LineSegment {

    /**
     * Canvas x mid point
     */
    private static final double midPointX = (double) Config.CANVAS_WIDTH / 2;

    /**
     * Canvas y mid point
     */
    private static final double midPointY = (double) Config.CANVAS_HEIGHT / 2;

    /**
     * Input controller
     */
    private final InputController inputController = InputController.getInstance();

    /**
     * Line color
     */
    private final Color color;

    /**
     * Line starting position
     */
    public double sx, sy;

    /**
     * Line end position
     */
    public double ex, ey;

    /**
     * Line radius
     */
    public double radius;

    /**
     * Constructor
     *
     * @param sx     start x position
     * @param sy     start y position
     * @param ex     end x position
     * @param ey     end y position
     * @param radius line radius
     * @param lab    part of labyrinth
     * @param color  color
     */
    public LineSegment(double sx, double sy, double ex, double ey, double radius, boolean lab, Color color) {
        if (lab) {
            double midOffsetX = 210;
            double midOffsetY = 210;
            this.sx = sx * 2 + midOffsetX;
            this.sy = sy * 2 + midOffsetY;
            this.ex = ex * 2 + midOffsetX;
            this.ey = ey * 2 + midOffsetY;
        } else {
            this.sx = sx;
            this.sy = sy;
            this.ex = ex;
            this.ey = ey;
        }
        this.color = color;
        this.radius = radius;
    }

    /**
     * Draw line segments
     *
     * @param gc Graphics context of the canvas
     */
    public void draw(GraphicsContext gc) {
        double width = this.radius * 2;

        gc.setStroke(this.color);
        gc.setFill(this.color);

        gc.fillOval(this.sx - this.radius, this.sy - this.radius, width, width);
        gc.fillOval(this.ex - this.radius, this.ey - this.radius, width, width);

        double nx = -(this.ey - this.sy);
        double ny = (this.ex - this.sx);
        double d = Math.sqrt(nx * nx + ny * ny);
        nx /= d;
        ny /= d;

        gc.strokeLine((this.sx + nx * this.radius), (this.sy + ny * this.radius), (this.ex + nx * this.radius), (this.ey + ny * this.radius));
        gc.strokeLine((this.sx - nx * this.radius), (this.sy - ny * this.radius), (this.ex - nx * this.radius), (this.ey - ny * this.radius));
    }

    /**
     * Update rotations
     *
     * @param deltaTime Time passed since last frame
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
    }

    /**
     * Rotate maze around midpoint
     *
     * @param clockWise Clockwise rotation
     * @param flip      Flip or rotate
     */
    private void rotateAroundMidPoint(boolean clockWise, boolean flip) {
        double rotation = 0.01;

        if (!clockWise)
            rotation *= (-1);

        if (flip)
            rotation = Math.PI;

        this.sx = midPointX + (this.sx - midPointX) * Math.cos(rotation) - (this.sy - midPointY) * Math.sin(rotation);
        this.sy = midPointY + (this.sx - midPointX) * Math.sin(rotation) + (this.sy - midPointY) * Math.cos(rotation);

        this.ex = midPointX + (this.ex - midPointX) * Math.cos(rotation) - (this.ey - midPointY) * Math.sin(rotation);
        this.ey = midPointY + (this.ex - midPointX) * Math.sin(rotation) + (this.ey - midPointY) * Math.cos(rotation);
    }
}
