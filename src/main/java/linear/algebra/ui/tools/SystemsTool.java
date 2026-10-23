package linear.algebra.ui.tools;

import linear.algebra.core.Fraction;
import linear.algebra.core.Matrix;
import linear.algebra.core.RowReducer;
import linear.algebra.ui.view.PlotPanel;
import linear.algebra.ui.model.StepViewer;
import linear.algebra.ui.view.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Shows equations and their intersection for a two-dimensional system.
 */
public class SystemsTool extends JPanel implements Tool {
    private final Plot plot = new Plot();
    private final StepViewer viewer = new StepViewer();

    /**
     * Creates the two-dimensional system view.
     */
    public SystemsTool() {
        super(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plot, viewer);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Systems (2D)";
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
     * @param matrix the matrix to process
     * @param augmented whether the final column is the right-hand side
     */
    @Override
    public void update(Matrix matrix, boolean augmented) {
        if (!augmented || matrix.rows() != 2 || matrix.cols() != 3) {
            plot.setSystem(null);
            viewer.setContent("Enter a 2 x 3 augmented matrix [A | b] to see two lines in the plane.\n"
            + "Tick \"Augmented\" on the left.", "");
            return;
        }
        plot.setSystem(matrix);
        RowReducer.Result reduction = RowReducer.reduce(matrix, 2);
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            summary.append(equation(matrix, i)).append('\n');
        }
        summary.append('\n');
        switch (reduction.classify()) {
            case UNIQUE -> summary.append("The lines cross at one point: (").append(reduction.rref.get(0, 2)).append(", ")
            .append(reduction.rref.get(1, 2)).append(")\n");
            case NONE -> summary.append("The lines are parallel and distinct: no solution.\n");
            case INFINITE -> summary.append("Both equations describe the same line: infinitely many solutions.\n");
        }
        viewer.setContent(summary.toString(), StepViewer.formatSteps(reduction.steps, 2));
    }

    /**
     * Formats one row as a linear equation.
     * @param matrix the matrix to process
     * @param rowIndex the row index
     * @return the formatted equation
     */
    private static String equation(Matrix matrix, int rowIndex) {
        return "(" + matrix.get(rowIndex, 0) + ")x + (" + matrix.get(rowIndex, 1) + ")y = " + matrix.get(rowIndex, 2);
    }

    /**
     * Draws the system in the coordinate plane.
     */
    private static class Plot extends PlotPanel {
        private Matrix system;

        void setSystem(Matrix matrix) {
            system = matrix;
            repaint();
        }

        /**
         * Draws subclass content using world coordinates.
         * @param g the graphics context
         */
        @Override
        protected void paintContent(Graphics2D g) {
            if (system == null) {
                return;
            }
            Color[] colors = {Theme.accent(), Theme.fg()};
            for (int i = 0; i < 2; i++) {
                g.setColor(colors[i]);
                if (i == 0) {
                    g.setStroke(new BasicStroke(2.2f));
                } else {
                    g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                    new float[]{8f, 6f}, 0f));
                }
                double coefficientX = system.get(i, 0).toDouble();
                double coefficientY = system.get(i, 1).toDouble();
                double rightHandSide = system.get(i, 2).toDouble();
                double far = Math.max(getWidth(), getHeight()) / scale;
                if (coefficientY != 0) {
                    line(g, -far, (rightHandSide + coefficientX * far) / coefficientY, far, (rightHandSide - coefficientX * far) / coefficientY);
                } else if (coefficientX != 0) {
                    line(g, rightHandSide / coefficientX, -far, rightHandSide / coefficientX, far);
                }
            }
            RowReducer.Result reduction = RowReducer.reduce(system, 2);
            if (reduction.classify() == RowReducer.Kind.UNIQUE) {
                Fraction x = reduction.rref.get(0, 2);
                Fraction y = reduction.rref.get(1, 2);
                g.setColor(Theme.fg());
                g.fillOval((int) screenX(x.toDouble()) - 5, (int) screenY(y.toDouble()) - 5, 10, 10);
            }
        }
    }
}
