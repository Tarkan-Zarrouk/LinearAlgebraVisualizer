package linear.algebra.ui;

import linear.algebra.core.Determinant;
import linear.algebra.core.Inverse;
import linear.algebra.core.Matrix;

import javax.swing.*;
import java.awt.*;

/**
 * Displays an inverse and equivalent invertibility conditions.
 */
public class InverseTool extends JPanel implements Tool {
    private final StepViewer viewer = new StepViewer();

    /**
     * Creates the inverse view.
     */
    public InverseTool() {
        super(new BorderLayout());
        add(viewer, BorderLayout.CENTER);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Inverse";
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
        if (!matrix.isSquare()) {
            viewer.setContent("The inverse needs a square matrix.\nUntick \"Augmented\" and use n x n.", "");
            return;
        }
        int size = matrix.rows();
        Inverse inverseResult = Inverse.of(matrix);
        boolean invertible = inverseResult.isInvertible();
        boolean determinantNonzero = !Determinant.of(matrix).isZero();

        StringBuilder summary = new StringBuilder();
        if (invertible) {
            summary.append("A^-1 =\n").append(inverseResult.inverse).append("\nCheck: A * A^-1 =\n")
            .append(matrix.multiply(inverseResult.inverse));
        } else {
            summary.append("A is not invertible: [A | I] does not reduce to [I | A^-1].\n");
        }
        summary.append("\nInvertible Matrix Theorem for this A\n");
        summary.append(line("A has a pivot in every column", invertible));
        summary.append(line("A has a pivot in every row", invertible));
        summary.append(line("det A != 0", determinantNonzero));
        summary.append(line("Columns of A are linearly independent", invertible));
        summary.append(line("Columns of A span R^" + size, invertible));
        summary.append(line("Ax = 0 has only the trivial solution", invertible));
        summary.append(line("Ax = b is solvable for every b", invertible));
        summary.append(line("A^T is invertible", !Determinant.of(matrix.transpose()).isZero()));
        summary.append(line("rank A = " + size, invertible));
        viewer.setContent(summary.toString(), StepViewer.formatSteps(inverseResult.reduction.steps, size));
    }

    /**
     * Formats a theorem statement with its truth value.
     * @param statement the theorem statement
     * @param conditionHolds whether the statement holds
     * @return the formatted statement
     */
    private static String line(String statement, boolean conditionHolds) {
        String result = "  [false] ";
        if (conditionHolds) {
            result = "  [true]  ";
        }
        return result + statement + "\n";
    }
}
