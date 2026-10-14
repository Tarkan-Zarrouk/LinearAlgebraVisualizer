package linear.algebra.core;

/**
 * Computes determinants and formats cofactor expansions.
 */
public final class Determinant {
    /** Creates a determinant utility. */
    public Determinant() {
    }

    /**
     * Computes a determinant by cofactor expansion.
     * @param matrix the matrix to process
     * @return the determinant
     * @throws IllegalArgumentException if the matrix is not square
     */
    public static Fraction of(Matrix matrix) {
        requireSquare(matrix);
        int size = matrix.rows();
        if (size == 1) {
            return matrix.get(0, 0);
        }
        Fraction determinant = Fraction.ZERO;
        for (int j = 0; j < size; j++) {
            Fraction cofactorTerm = matrix.get(0, j).mul(of(matrix.minor(0, j)));
            if (j % 2 == 0) {
                determinant = determinant.add(cofactorTerm);
            } else {
                determinant = determinant.sub(cofactorTerm);
            }
        }
        return determinant;
    }

    /**
     * Formats the first-row cofactor expansion as worked steps.
     * @param matrix the matrix to process
     * @return the formatted expansion
     * @throws IllegalArgumentException if the matrix is not square
     */
    public static String cofactorSteps(Matrix matrix) {
        requireSquare(matrix);
        int size = matrix.rows();
        if (size == 1) {
            return "det = " + matrix.get(0, 0) + "\n";
        }
        if (size == 2) {
            Fraction diagonalProduct = matrix.get(0, 0).mul(matrix.get(1, 1));
            Fraction offDiagonalProduct = matrix.get(0, 1).mul(matrix.get(1, 0));
            return "det = ad - bc = (" + matrix.get(0, 0) + ")(" + matrix.get(1, 1) + ") - (" + matrix.get(0, 1) + ")("
            + matrix.get(1, 0) + ") = " + diagonalProduct.sub(offDiagonalProduct) + "\n";
        }
        StringBuilder steps = new StringBuilder("Expand along row 1:\n");
        Fraction total = Fraction.ZERO;
        for (int j = 0; j < size; j++) {
            Fraction minorDeterminant = of(matrix.minor(0, j));
            Fraction cofactorTerm = matrix.get(0, j).mul(minorDeterminant);
            boolean positiveTerm = j % 2 == 0;
            steps.append("  ");
            if (positiveTerm) {
                steps.append("+");
            } else {
                steps.append("-");
            }
            steps.append(" (").append(matrix.get(0, j)).append(") * det(M1").append(j + 1).append(") = ");
            if (!positiveTerm) {
                steps.append("-");
            }
            steps.append("(").append(matrix.get(0, j)).append(")(").append(minorDeterminant).append(")\n");
            if (positiveTerm) {
                total = total.add(cofactorTerm);
            } else {
                total = total.sub(cofactorTerm);
            }
        }
        return steps.append("det = ").append(total).append("\n").toString();
    }

    /**
     * Rejects a matrix that is not square.
     * @param matrix the matrix to process
     */
    private static void requireSquare(Matrix matrix) {
        if (!matrix.isSquare()) {
            throw new IllegalArgumentException("determinant needs a square matrix");
        }
    }
}
