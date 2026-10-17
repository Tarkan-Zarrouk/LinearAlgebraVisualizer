package linear.algebra.ui;

import linear.algebra.core.Determinant;
import linear.algebra.core.Fraction;
import linear.algebra.core.Matrix;

import javax.swing.*;
import java.awt.*;

/**
 * Displays a determinant and related properties.
 */
public class DeterminantTool extends JPanel implements Tool {
    private final StepViewer viewer = new StepViewer();

    /**
     * Creates the determinant view.
     */
    public DeterminantTool() {
        super(new BorderLayout());
        add(viewer, BorderLayout.CENTER);
    }

    /**
     * Returns the tab title.
     * @return the tab title
     */
    @Override
    public String title() {
        return "Determinant";
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
            viewer.setContent("The determinant needs a square matrix.", "");
            return;
        }
        int size = matrix.rows();
        Fraction determinant = Determinant.of(matrix);

        StringBuilder summary = new StringBuilder();
        summary.append("det A = ").append(determinant).append("\n\n");
        if (determinant.isZero()) {
            summary.append("det = 0, so A is singular.\n");
        } else if (size == 2 || size == 3) {
            String unit;
            if (size == 2) {
                unit = "areas";
            } else {
                unit = "volumes";
            }
            summary.append("|det| = ").append(determinant.abs()).append(": ").append(unit).append(" are scaled by ")
            .append(determinant.abs()).append(".\n");
            if (determinant.signum() > 0) {
                summary.append("Positive: orientation is kept.\n");
            } else {
                summary.append("Negative: orientation is flipped.\n");
            }
        }

        StringBuilder expansionSteps = new StringBuilder("Cofactor expansion\n");
        expansionSteps.append(Determinant.cofactorSteps(matrix)).append("\nProperty checks\n");
        expansionSteps.append("det(A^T) = ").append(Determinant.of(matrix.transpose())).append(" (same as det A)\n");
        expansionSteps.append("det(A*A) = ").append(Determinant.of(matrix.multiply(matrix))).append(", (det A)^2 = ")
        .append(determinant.mul(determinant)).append('\n');
        if (size >= 2) {
            Matrix swapped = swapFirstTwoRows(matrix);
            expansionSteps.append("Swap R1 and R2: det = ").append(Determinant.of(swapped)).append(" (sign flips)\n");
        }
        Matrix scaled = matrix;
        for (int j = 0; j < size; j++) {
            scaled = scaled.with(0, j, matrix.get(0, j).mul(Fraction.of(2)));
        }
        expansionSteps.append("Multiply R1 by 2: det = ").append(Determinant.of(scaled)).append(" (twice det A)\n");
        viewer.setContent(summary.toString(), expansionSteps.toString());
    }

    /**
     * Returns a copy with the first two rows exchanged.
     * @param matrix the matrix to process
     * @return the matrix with its first two rows exchanged
     */
    private static Matrix swapFirstTwoRows(Matrix matrix) {
        Matrix out = matrix;
        for (int j = 0; j < matrix.cols(); j++) {
            out = out.with(0, j, matrix.get(1, j)).with(1, j, matrix.get(0, j));
        }
        return out;
    }
}
