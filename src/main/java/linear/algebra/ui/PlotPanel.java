package linear.algebra.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

/**
 * A coordinate plane with y pointing up; subclasses draw in world units.
 */
public abstract class PlotPanel extends JPanel {
    protected double scale = 40;

    /**
     * Creates the coordinate plane.
     */
    protected PlotPanel() {
        setPreferredSize(new Dimension(520, 420));
    }

    /**
     * Converts a world x-coordinate to a screen coordinate.
     * @param x the world x-coordinate
     * @return the screen x-coordinate
     */
    protected double screenX(double x) {
        return getWidth() / 2.0 + x * scale;
    }

    /**
     * Converts a world y-coordinate to a screen coordinate.
     * @param y the world y-coordinate
     * @return the screen y-coordinate
     */
    protected double screenY(double y) {
        return getHeight() / 2.0 - y * scale;
    }

    /**
     * Converts a screen x-coordinate to a world coordinate.
     * @param px the screen x-coordinate
     * @return the world x-coordinate
     */
    protected double worldX(double px) {
        return (px - getWidth() / 2.0) / scale;
    }

    /**
     * Converts a screen y-coordinate to a world coordinate.
     * @param py the screen y-coordinate
     * @return the world y-coordinate
     */
    protected double worldY(double py) {
        return (getHeight() / 2.0 - py) / scale;
    }

    /**
     * Draws subclass content using world coordinates.
     * @param g the graphics context
     */
    protected abstract void paintContent(Graphics2D g);

    /**
     * Paints the coordinate grid and subclass content.
     * @param g0 the graphics context supplied by Swing
     */
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Theme.bg());
        g.fillRect(0, 0, getWidth(), getHeight());

        int xmax = (int) (getWidth() / 2 / scale) + 1;
        int ymax = (int) (getHeight() / 2 / scale) + 1;
        g.setStroke(new BasicStroke(1f));
        g.setColor(Theme.grid());
        for (int i = -xmax; i <= xmax; i++) {
            g.draw(new Line2D.Double(screenX(i), 0, screenX(i), getHeight()));
        }
        for (int j = -ymax; j <= ymax; j++) {
            g.draw(new Line2D.Double(0, screenY(j), getWidth(), screenY(j)));
        }
        g.setColor(Theme.muted());
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new Line2D.Double(0, screenY(0), getWidth(), screenY(0)));
        g.draw(new Line2D.Double(screenX(0), 0, screenX(0), getHeight()));
        g.setFont(Theme.MONO.deriveFont(12f));
        g.drawString("x", getWidth() - 14, (float) screenY(0) - 6);
        g.drawString("y", (float) screenX(0) + 6, 14);

        paintContent(g);
        g.dispose();
    }

    /**
     * Draws a line between two world coordinates.
     * @param g the graphics context
     * @param x1 the first world x-coordinate
     * @param y1 the first world y-coordinate
     * @param x2 the second world x-coordinate
     * @param y2 the second world y-coordinate
     */
    protected void line(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(screenX(x1), screenY(y1), screenX(x2), screenY(y2)));
    }

    /**
     * Draws an arrow between two world coordinates.
     * @param g the graphics context
     * @param x1 the first world x-coordinate
     * @param y1 the first world y-coordinate
     * @param x2 the second world x-coordinate
     * @param y2 the second world y-coordinate
     */
    protected void arrow(Graphics2D g, double x1, double y1, double x2, double y2) {
        line(g, x1, y1, x2, y2);
        double angle = Math.atan2(screenY(y2) - screenY(y1), screenX(x2) - screenX(x1));
        double headX = screenX(x2);
        double headY = screenY(y2);
        Path2D head = new Path2D.Double();
        head.moveTo(headX, headY);
        head.lineTo(headX - 10 * Math.cos(angle - 0.4), headY - 10 * Math.sin(angle - 0.4));
        head.lineTo(headX - 10 * Math.cos(angle + 0.4), headY - 10 * Math.sin(angle + 0.4));
        head.closePath();
        g.fill(head);
    }

    /**
     * Creates a closed screen-space polygon from world points.
     * @param points the vertices in world coordinates
     * @return the closed polygon
     */
    protected Path2D polygon(double[][] points) {
        Path2D path = new Path2D.Double();
        for (int i = 0; i < points.length; i++) {
            if (i == 0) {
                path.moveTo(screenX(points[i][0]), screenY(points[i][1]));
            } else {
                path.lineTo(screenX(points[i][0]), screenY(points[i][1]));
            }
        }
        path.closePath();
        return path;
    }
}
