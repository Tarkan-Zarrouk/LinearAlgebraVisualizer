package linear.algebra.ui.tools;

import linear.algebra.core.Fraction;
import linear.algebra.core.Matrix;
import linear.algebra.ui.view.PlotPanel;
import linear.algebra.ui.view.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Shows the effect of the matrix top-left block on the plane.
 */
public class TransformTool extends JPanel implements Tool {
    private final Plot plot;
    private final JLabel info = new JLabel(" ");
    private Matrix matrix;

    /**
     * Creates the transformation view and forwards edits to the input.
     * @param onEdit callback for an edited matrix
     */
    public TransformTool(Consumer<Matrix> onEdit) {
        super(new BorderLayout());
        plot = new Plot(onEdit);
        info.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        add(plot, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Transformations";
    }

    /**
     * Returns the component displayed in the tab.
     * @return the component to display
     */
    @Override
    public JComponent view() {
        return this;
    }

    /**
     * Refreshes this tool for the supplied matrix.
     * @param matrix the matrix to display
     * @param augmented whether the final column is the right-hand side
     */
    @Override
    public void update(Matrix matrix, boolean augmented) {
        this.matrix = matrix;
        if (matrix.rows() < 2 || matrix.cols() < 2) {
            info.setText("Needs at least a 2 x 2 matrix.");
        } else {
            Fraction determinant = matrix.get(0, 0).mul(matrix.get(1, 1)).sub(matrix.get(0, 1).mul(matrix.get(1, 0)));
            String orientation = "";
            if (determinant.signum() < 0) {
                orientation = ", orientation flipped";
            } else if (determinant.isZero()) {
                orientation = ", plane collapses";
            }
            info.setText("Using the top-left 2 x 2 block.  det = " + determinant + "  (areas scale by " + determinant.abs()
            + orientation + ").  Drag the arrow tips.");
        }
        plot.repaint();
    }

    /**
     * Draws the system in the coordinate plane.
     */
    private class Plot extends PlotPanel {
        private final Consumer<Matrix> onEdit;
        private int dragging = -1;

        Plot(Consumer<Matrix> onEdit) {
            this.onEdit = onEdit;
            MouseAdapter mouseAdapter = new MouseAdapter() {
                /**
                 * Selects a vector tip or updates it from a drag event.
                 * @param e the mouse event
                 */
                @Override
                public void mousePressed(MouseEvent e) {
                    dragging = pick(e.getX(), e.getY());
                }

                /**
                 * Moves the selected vector tip with the pointer.
                 * @param e the mouse event
                 */
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (dragging < 0 || matrix == null) {
                        return;
                    }
                    Fraction x = quarter(worldX(e.getX()));
                    Fraction y = quarter(worldY(e.getY()));
                    Matrix next = matrix.with(0, dragging, x).with(1, dragging, y);
                    if (!next.equals(matrix)) {
                        Plot.this.onEdit.accept(next);
                    }
                }

                /**
                 * Stops tracking the dragged vector tip.
                 * @param e the mouse event
                 */
                @Override
                public void mouseReleased(MouseEvent e) {
                    dragging = -1;
                }
            };
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        /**
         * Rounds a coordinate to the nearest quarter unit.
         * @param worldCoordinate the world coordinate
         * @return the nearest quarter-unit fraction
         */
        private Fraction quarter(double worldCoordinate) {
            return Fraction.of(Math.round(worldCoordinate * 4), 4);
        }

        /**
         * Finds an editable vector tip near a screen point.
         * @param screenX the screen x
         * @param screenY the screen y
         * @return the selected vector index, or -1 if no tip is close enough
         */
        private int pick(int screenX, int screenY) {
            if (matrix == null || matrix.rows() < 2 || matrix.cols() < 2) {
                return -1;
            }
            for (int columnIndex = 0; columnIndex < 2; columnIndex++) {
                double distanceX = screenX(matrix.get(0, columnIndex).toDouble()) - screenX;
                double distanceY = screenY(matrix.get(1, columnIndex).toDouble()) - screenY;
                if (Math.hypot(distanceX, distanceY) < 14) {
                    return columnIndex;
                }
            }
            return -1;
        }

        /**
         * Draws subclass content using world coordinates.
         * @param g the graphics context
         */
        @Override
        protected void paintContent(Graphics2D g) {
            if (matrix == null || matrix.rows() < 2 || matrix.cols() < 2) {
                return;
            }
            double firstColumnX = matrix.get(0, 0).toDouble();
            double secondColumnX = matrix.get(0, 1).toDouble();
            double firstColumnY = matrix.get(1, 0).toDouble();
            double secondColumnY = matrix.get(1, 1).toDouble();
            int gridLineLimit = 10;

            Color accentColor = Theme.accent();
            g.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 90));
            g.setStroke(new BasicStroke(1f));
            for (int i = -gridLineLimit; i <= gridLineLimit; i++) {
                line(g, firstColumnX * i - secondColumnX * gridLineLimit, firstColumnY * i - secondColumnY * gridLineLimit, firstColumnX * i + secondColumnX * gridLineLimit, firstColumnY * i + secondColumnY * gridLineLimit);
                line(g, firstColumnX * -gridLineLimit + secondColumnX * i, firstColumnY * -gridLineLimit + secondColumnY * i, firstColumnX * gridLineLimit + secondColumnX * i, firstColumnY * gridLineLimit + secondColumnY * i);
            }

            g.setColor(Theme.muted());
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
            new float[]{5f, 5f}, 0f));
            g.draw(polygon(new double[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}}));

            Shape image = polygon(new double[][]{{0, 0}, {firstColumnX, firstColumnY}, {firstColumnX + secondColumnX, firstColumnY + secondColumnY}, {secondColumnX, secondColumnY}});
            g.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 50));
            g.fill(image);
            g.setColor(accentColor);
            g.setStroke(new BasicStroke(2f));
            g.draw(image);

            g.setColor(Theme.fg());
            g.setStroke(new BasicStroke(2.5f));
            arrow(g, 0, 0, firstColumnX, firstColumnY);
            arrow(g, 0, 0, secondColumnX, secondColumnY);
            g.setFont(Theme.MONO.deriveFont(Font.BOLD, 13f));
            g.drawString("Ae1", (float) screenX(firstColumnX) + 8, (float) screenY(firstColumnY) - 8);
            g.drawString("Ae2", (float) screenX(secondColumnX) + 8, (float) screenY(secondColumnY) - 8);
        }
    }
}
