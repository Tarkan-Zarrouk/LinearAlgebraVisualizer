package linear.algebra.core;

/**
 * Computes a matrix inverse by reducing an augmented matrix.
 */
public final class Inverse {
    /**
     * The row-reduction result used to compute the inverse.
     */
    public final RowReducer.Result reduction;
    /**
     * The inverse matrix, or null when none exists.
     */
    public final Matrix inverse;

    /**
     * Stores the reduction and resulting inverse.
     */
    private Inverse(RowReducer.Result reduction, Matrix inverse) {
        this.reduction = reduction;
        this.inverse = inverse;
    }

    /**
     * Reports whether the matrix has an inverse.
     * @return true if an inverse exists
     */
    public boolean isInvertible() {
        return inverse != null;
    }

    /**
     * Computes an inverse by row-reducing the augmented matrix.
     * @param matrix the matrix to process
     * @return the inverse and its row-reduction steps
     * @throws IllegalArgumentException if the matrix is not square
     */
    public static Inverse of(Matrix matrix) {
        if (!matrix.isSquare()) {
            throw new IllegalArgumentException("inverse needs a square matrix");
        }
        int size = matrix.rows();
        RowReducer.Result reductionResult = RowReducer.reduce(matrix.augment(Matrix.identity(size)), size);
        Matrix inverseMatrix = null;
        if (reductionResult.rank() == size) {
            inverseMatrix = reductionResult.rref.columns(size, 2 * size);
        }
        return new Inverse(reductionResult, inverseMatrix);
    }
}
