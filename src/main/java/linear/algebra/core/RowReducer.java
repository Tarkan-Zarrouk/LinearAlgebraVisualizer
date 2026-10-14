package linear.algebra.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs Gauss-Jordan elimination and records each row operation.
 */
public final class RowReducer {
    /**
     * The solution type of a linear system.
     */
    public enum Kind { UNIQUE, NONE, INFINITE }

    /**
     * A row operation and the matrix state that follows it.
     * @param description the operation description
     * @param state the resulting matrix
     */
    public record Step(String description, Matrix state) {
    }

    /**
     * The location of a leading entry in a reduced matrix.
     * @param row the zero-based pivot row
     * @param col the zero-based pivot column
     */
    public record Pivot(int row, int col) {
    }

    /**
     * The echelon forms, pivots, and operations produced by reduction.
     */
    public static final class Result {
        /** Creates an empty row-reduction result. */
        public Result() {
        }
        /**
         * The recorded row operations.
         */
        public final List<Step> steps = new ArrayList<>();
        /**
         * The leading-entry positions.
         */
        public final List<Pivot> pivots = new ArrayList<>();
        /**
         * The row-echelon form.
         */
        public Matrix ref;
        /**
         * The reduced row-echelon form.
         */
        public Matrix rref;
        /**
         * The index of the final echelon-form step.
         */
        public int refStep;
        /**
         * The number of coefficient columns.
         */
        public int coefCols;

        /**
         * Returns the number of pivots.
         * @return the pivot count
         */
        public int rank() {
            return pivots.size();
        }

        /**
         * Classifies an augmented system by its solution set.
         * @return the solution type
         */
        public Kind classify() {
            for (int i = 0; i < rref.rows(); i++) {
                boolean zeroCoefficients = true;
                for (int j = 0; j < coefCols; j++) {
                    zeroCoefficients &= rref.get(i, j).isZero();
                }
                if (zeroCoefficients) {
                    for (int j = coefCols; j < rref.cols(); j++) {
                        if (!rref.get(i, j).isZero()) {
                            return Kind.NONE;
                        }
                    }
                }
            }
            if (pivots.size() == coefCols) {
                return Kind.UNIQUE;
            }
            return Kind.INFINITE;
        }

        /**
         * Returns the coefficient columns without pivots.
         * @return the zero-based indices of free columns
         */
        public List<Integer> freeColumns() {
            List<Integer> freeColumnIndices = new ArrayList<>();
            for (int c = 0; c < coefCols; c++) {
                boolean isPivotColumn = false;
                for (Pivot pivot : pivots) {
                    isPivotColumn |= pivot.col() == c;
                }
                if (!isPivotColumn) {
                    freeColumnIndices.add(c);
                }
            }
            return freeColumnIndices;
        }

        /**
         * Formats one equation for each variable.
         * @return the formatted variable equations
         */
        public String solutionText() {
            List<Integer> freeColumnIndices = freeColumns();
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < coefCols; c++) {
                Pivot found = null;
                for (Pivot pivot : pivots) {
                    if (pivot.col() == c) {
                        found = pivot;
                    }
                }
                sb.append("x").append(c + 1).append(" = ");
                if (found == null) {
                    sb.append("free\n");
                    continue;
                }
                Fraction rightHandSide = rref.get(found.row(), coefCols);
                StringBuilder expr = new StringBuilder();
                if (!rightHandSide.isZero()) {
                    expr.append(rightHandSide);
                }
                for (int freeColumn : freeColumnIndices) {
                    Fraction coefficient = rref.get(found.row(), freeColumn).negate();
                    if (coefficient.isZero()) {
                        continue;
                    }
                    String magnitude = "";
                    if (!coefficient.abs().isOne()) {
                        magnitude = coefficient.abs() + " ";
                    }
                    if (expr.isEmpty()) {
                        if (coefficient.signum() < 0) {
                            expr.append("-");
                        }
                        expr.append(magnitude);
                    } else {
                        if (coefficient.signum() < 0) {
                            expr.append(" - ");
                        } else {
                            expr.append(" + ");
                        }
                        expr.append(magnitude);
                    }
                    expr.append("x").append(freeColumn + 1);
                }
                if (expr.isEmpty()) {
                    expr.append("0");
                }
                sb.append(expr).append('\n');
            }
            return sb.toString();
        }
    }

    /** Prevents instances of this utility class. */
    private RowReducer() {
    }

    /**
     * Computes echelon forms while recording row operations.
     * @param matrix the matrix to process
     * @param coefficientColumns the number of coefficient columns
     * @return the reduction result
     */
    public static Result reduce(Matrix matrix, int coefficientColumns) {
        Result result = new Result();
        result.coefCols = coefficientColumns;
        Fraction[][] entries = matrix.copyData();
        int rowCount = matrix.rows();
        result.steps.add(new Step("Start", new Matrix(entries)));

        int pivotRow = 0;
        for (int columnIndex = 0; columnIndex < coefficientColumns && pivotRow < rowCount; columnIndex++) {
            int pivotCandidate = -1;
            for (int rowIndex = pivotRow; rowIndex < rowCount; rowIndex++) {
                if (!entries[rowIndex][columnIndex].isZero()) {
                    pivotCandidate = rowIndex;
                    break;
                }
            }
            if (pivotCandidate < 0) {
                continue;
            }
            if (pivotCandidate != pivotRow) {
                Fraction[] temporaryRow = entries[pivotCandidate];
                entries[pivotCandidate] = entries[pivotRow];
                entries[pivotRow] = temporaryRow;
                result.steps.add(new Step("R" + (pivotRow + 1) + " <-> R" + (pivotCandidate + 1), new Matrix(entries)));
            }
            result.pivots.add(new Pivot(pivotRow, columnIndex));
            for (int rowIndex = pivotRow + 1; rowIndex < rowCount; rowIndex++) {
                if (!entries[rowIndex][columnIndex].isZero()) {
                    addMultiple(entries, rowIndex, pivotRow,
                    entries[rowIndex][columnIndex].div(entries[pivotRow][columnIndex]).negate(), result);
                }
            }
            pivotRow++;
        }
        result.ref = new Matrix(entries);
        result.refStep = result.steps.size() - 1;

        for (int pivotIndex = result.pivots.size() - 1; pivotIndex >= 0; pivotIndex--) {
            Pivot pivot = result.pivots.get(pivotIndex);
            Fraction leadingEntry = entries[pivot.row()][pivot.col()];
            if (!leadingEntry.isOne()) {
                Fraction inverseFactor = Fraction.ONE.div(leadingEntry);
                for (int columnIndex = 0; columnIndex < matrix.cols(); columnIndex++) {
                    entries[pivot.row()][columnIndex] = entries[pivot.row()][columnIndex].mul(inverseFactor);
                }
                result.steps.add(new Step("R" + (pivot.row() + 1) + " -> " + coef(inverseFactor) + "R" + (pivot.row() + 1),
                new Matrix(entries)));
            }
            for (int rowIndex = 0; rowIndex < pivot.row(); rowIndex++) {
                if (!entries[rowIndex][pivot.col()].isZero()) {
                    addMultiple(entries, rowIndex, pivot.row(), entries[rowIndex][pivot.col()].negate(), result);
                }
            }
        }
        result.rref = new Matrix(entries);
        return result;
    }

    /**
     * Adds a multiple of one row to another and records the operation.
     * @param entries the entries
     * @param target the row to update
     * @param source the row to add
     * @param coefficient the row-operation coefficient
     * @param result the reduction result
     */
    private static void addMultiple(Fraction[][] entries, int target, int source, Fraction coefficient, Result result) {
        for (int columnIndex = 0; columnIndex < entries[target].length; columnIndex++) {
            entries[target][columnIndex] = entries[target][columnIndex].add(coefficient.mul(entries[source][columnIndex]));
        }
        String rowLabel = "R" + (target + 1);
        String magnitude = "";
        if (!coefficient.abs().isOne()) {
            magnitude = coef(coefficient.abs());
        }
        String operation = " + ";
        if (coefficient.signum() < 0) {
            operation = " - ";
        }
        result.steps.add(new Step(rowLabel + " -> " + rowLabel + operation + magnitude + "R" + (source + 1),
        new Matrix(entries)));
    }

    /**
     * Formats a row-operation coefficient.
     * @param coefficient the row-operation coefficient
     * @return the formatted coefficient
     */
    private static String coef(Fraction coefficient) {
        if (coefficient.isInteger()) {
            return coefficient.toString();
        }
        return "(" + coefficient + ")";
    }
}
