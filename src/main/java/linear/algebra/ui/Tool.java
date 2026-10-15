package linear.algebra.ui;

import linear.algebra.core.Matrix;

import javax.swing.*;

/**
 * A visualizer tab refreshed whenever the shared matrix changes.
 */
public interface Tool {
    /**
     * Returns the tab title.
     * @return the tab title
     */
    String title();

    /**
     * Returns the component displayed in the tab.
     * @return the component to display
     */
    JComponent view();

    /**
     * Refreshes this tool for the supplied matrix.
     * @param matrix the matrix to display
     * @param augmented whether the final column is the right-hand side
     */
    void update(Matrix matrix, boolean augmented);
}
