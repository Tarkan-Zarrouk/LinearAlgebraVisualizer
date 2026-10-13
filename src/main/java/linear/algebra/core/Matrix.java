package linear.algebra.core;

/**
 * An immutable matrix of exact fractions.
 */
public final class Matrix {
    private final Fraction[][] entries;
    private final int rows;
    private final int cols;

    /**
     * Copies the supplied matrix entries.
     */
    /**
     * Copies the supplied matrix entries.
     * @param data the matrix entries to copy
     */
    public Matrix(Fraction[][] data) {
        this.rows = data.length;
        if (rows == 0) {
            this.cols = 0;
        } else {
            this.cols = data[0].length;
        }
        this.entries = new Fraction[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, entries[i], 0, cols);
        }
    }

    /**
     * Creates a matrix filled with zeros.
     * @param rows the number of rows
     * @param cols the number of columns
     * @return the zero matrix
     */
    public static Matrix zero(int rows, int cols) {
        Fraction[][] data = new Fraction[rows][cols];
        for (Fraction[] rowEntries : data) {
            java.util.Arrays.fill(rowEntries, Fraction.ZERO);
        }
        return new Matrix(data);
    }

    /**
     * Creates a matrix from integer entries.
     * @param values the entries to convert
     * @return the resulting matrix
     */
    public static Matrix ofInts(int[][] values) {
        Fraction[][] data = new Fraction[values.length][];
        for (int i = 0; i < values.length; i++) {
            data[i] = new Fraction[values[i].length];
            for (int j = 0; j < values[i].length; j++) {
                data[i][j] = Fraction.of(values[i][j]);
            }
        }
        return new Matrix(data);
    }

    /**
     * Creates an identity matrix of the requested size.
     * @param size the matrix size
     * @return the identity matrix
     */
    public static Matrix identity(int size) {
        Fraction[][] data = zero(size, size).entries;
        for (int i = 0; i < size; i++) {
            data[i][i] = Fraction.ONE;
        }
        return new Matrix(data);
    }

    /**
     * Returns the number of rows.
     * @return the row count
     */
    public int rows() {
        return rows;
    }

    /**
     * Returns the number of columns.
     * @return the column count
     */
    public int cols() {
        return cols;
    }

    /**
     * Reports whether the matrix is square.
     * @return true if the matrix is square
     */
    public boolean isSquare() {
        return rows == cols;
    }

    /**
     * Returns the entry at the given row and column.
     * @param rowIndex the zero-based row index
     * @param columnIndex the zero-based column index
     * @return the selected entry
     */
    public Fraction get(int rowIndex, int columnIndex) {
        return entries[rowIndex][columnIndex];
    }

    /**
     * Returns a copy with one entry replaced.
     * @param rowIndex the zero-based row index
     * @param columnIndex the zero-based column index
     * @param value the replacement value
     * @return the updated matrix
     */
    public Matrix with(int rowIndex, int columnIndex, Fraction value) {
        Matrix copy = new Matrix(entries);
        copy.entries[rowIndex][columnIndex] = value;
        return copy;
    }

    /**
     * Multiplies this matrix by another matrix.
     * @param other the other value
     * @return the matrix product
     * @throws IllegalArgumentException if the dimensions do not match
     */
    public Matrix multiply(Matrix other) {
        if (cols != other.rows) {
            throw new IllegalArgumentException("dimension mismatch");
        }
        Fraction[][] data = zero(rows, other.cols).entries;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                Fraction sum = Fraction.ZERO;
                for (int k = 0; k < cols; k++) {
                    sum = sum.add(entries[i][k].mul(other.entries[k][j]));
                }
                data[i][j] = sum;
            }
        }
        return new Matrix(data);
    }

    /**
     * Returns the transpose of this matrix.
     * @return the transposed matrix
     */
    public Matrix transpose() {
        Fraction[][] data = new Fraction[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[j][i] = entries[i][j];
            }
        }
        return new Matrix(data);
    }

    /**
     * Returns the columns in the requested half-open range.
     * @param from the first included column
     * @param to the first excluded column
     * @return the requested columns
     */
    public Matrix columns(int from, int to) {
        Fraction[][] data = new Fraction[rows][to - from];
        for (int i = 0; i < rows; i++) {
            for (int j = from; j < to; j++) {
                data[i][j - from] = entries[i][j];
            }
        }
        return new Matrix(data);
    }

    /**
     * Appends another matrix to the right.
     * @param other the other value
     * @return the augmented matrix
     */
    public Matrix augment(Matrix other) {
        Fraction[][] data = new Fraction[rows][cols + other.cols];
        for (int i = 0; i < rows; i++) {
            if (cols >= 0) System.arraycopy(entries[i], 0, data[i], 0, cols);
            if (other.cols >= 0) System.arraycopy(other.entries[i], 0, data[i], cols, other.cols);
        }
        return new Matrix(data);
    }

    /**
     * Returns the matrix formed by removing one row and column.
     * @param rowIndex the zero-based row index
     * @param columnIndex the zero-based column index
     * @return the matrix with the selected row and column removed
     */
    public Matrix minor(int rowIndex, int columnIndex) {
        Fraction[][] data = new Fraction[rows - 1][cols - 1];
        for (int sourceRow = 0, resultRow = 0; sourceRow < rows; sourceRow++) {
            if (sourceRow == rowIndex) {
                continue;
            }
            for (int sourceColumn = 0, resultColumn = 0; sourceColumn < cols; sourceColumn++) {
                if (sourceColumn != columnIndex) {
                    data[resultRow][resultColumn++] = entries[sourceRow][sourceColumn];
                }
            }
            resultRow++;
        }
        return new Matrix(data);
    }

    /** Returns a defensive copy of the matrix entries. */
    Fraction[][] copyData() {
        return new Matrix(entries).entries;
    }

    /**
     * Formats aligned rows, optionally marking an augmented divider.
     * @param divider the number of columns before the divider, or -1 for none
     * @return the formatted matrix
     */
    public String format(int divider) {
        int[] widths = new int[cols];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                widths[j] = Math.max(widths[j], entries[i][j].toString().length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < cols; j++) {
                if (j == divider) {
                    sb.append("| ");
                }
                sb.repeat(" ", widths[j] - entries[i][j].toString().length()).append(entries[i][j]).append(' ');
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    /**
     * Returns the standard integer or fraction representation.
     * @return the standard representation
     */
    @Override
    public String toString() {
        return format(-1);
    }

    /**
     * Compares this value with another object.
     * @param other the other value
     * @return true if the objects represent the same value
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Matrix m && java.util.Arrays.deepEquals(entries, m.entries);
    }

    /**
     * Returns a hash code consistent with equality.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return java.util.Arrays.deepHashCode(entries);
    }
}
