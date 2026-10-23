package linear.algebra.ui.tools;

import linear.algebra.core.Matrix;
import linear.algebra.core.RowReducer;
import linear.algebra.ui.model.StepViewer;

import javax.swing.*;
import java.awt.*;

/**
 * Displays row-echelon forms, pivots, and system solutions.
 */
public class RrefTool extends JPanel implements Tool {
    private final StepViewer viewer = new StepViewer();

    /**
     * Creates the row-reduction view.
     */
    public RrefTool() {
        super(new BorderLayout());
        add(viewer, BorderLayout.CENTER);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Row reduction";
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
        int coefficientColumns;
        int divider;
        if (augmented) {
            coefficientColumns = matrix.cols() - 1;
            divider = coefficientColumns;
        } else {
            coefficientColumns = matrix.cols();
            divider = -1;
        }
        RowReducer.Result reduction = RowReducer.reduce(matrix, coefficientColumns);

        StringBuilder summary = new StringBuilder();
        summary.append("Echelon form (step ").append(reduction.refStep).append("):\n").append(reduction.ref.format(divider));
        summary.append("\nReduced echelon form:\n").append(reduction.rref.format(divider)).append('\n');
        summary.append("Pivots: ");
        if (reduction.pivots.isEmpty()) {
            summary.append("none");
        }
        for (RowReducer.Pivot pivot : reduction.pivots) {
            summary.append("(row ").append(pivot.row() + 1).append(", col ").append(pivot.col() + 1).append(") ");
        }
        summary.append("\nRank: ").append(reduction.rank()).append('\n');
        var freeColumns = reduction.freeColumns();
        if (!freeColumns.isEmpty()) {
            summary.append("Free variables: ");
            freeColumns.forEach(freeColumn -> summary.append("x").append(freeColumn + 1).append(' '));
            summary.append('\n');
        }
        if (augmented && matrix.cols() >= 2) {
            RowReducer.Kind kind = reduction.classify();
            summary.append("\nSystem has ").append(switch (kind) {
                case UNIQUE -> "a unique solution";
                case NONE -> "no solution (a row reads 0 = nonzero)";
                case INFINITE -> "infinitely many solutions";
            }).append('\n');
            if (kind != RowReducer.Kind.NONE) {
                summary.append(reduction.solutionText());
            }
        }
        viewer.setContent(summary.toString(), StepViewer.formatSteps(reduction.steps, divider));
    }
}
