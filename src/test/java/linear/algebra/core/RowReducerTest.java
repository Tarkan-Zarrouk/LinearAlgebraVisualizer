package linear.algebra.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RowReducerTest {
    private static Matrix m(int[][] v) {
        return Matrix.ofInts(v);
    }

    @Test
    void uniqueSolution() {
        var r = RowReducer.reduce(m(new int[][]{{1, 1, 3}, {1, -1, 1}}), 2);
        assertEquals(RowReducer.Kind.UNIQUE, r.classify());
        assertEquals(m(new int[][]{{1, 0, 2}, {0, 1, 1}}), r.rref);
        assertEquals("x1 = 2\nx2 = 1\n", r.solutionText());
    }

    @Test
    void inconsistentSystem() {
        var r = RowReducer.reduce(m(new int[][]{{1, 1, 2}, {2, 2, 5}}), 2);
        assertEquals(RowReducer.Kind.NONE, r.classify());
    }

    @Test
    void infiniteSolutionsWithFreeVariable() {
        var r = RowReducer.reduce(m(new int[][]{{1, 2, -1, 3}, {2, 4, 1, 3}}), 3);
        assertEquals(RowReducer.Kind.INFINITE, r.classify());
        assertEquals(java.util.List.of(1), r.freeColumns());
        assertEquals("x1 = 2 - 2 x2\nx2 = free\nx3 = -1\n", r.solutionText());
    }

    @Test
    void swapsWhenLeadingEntryIsZero() {
        var r = RowReducer.reduce(m(new int[][]{{0, 1, 2}, {1, 0, 3}}), 2);
        assertEquals("R1 <-> R2", r.steps.get(1).description());
        assertEquals(m(new int[][]{{1, 0, 3}, {0, 1, 2}}), r.rref);
    }

    @Test
    void refKeepsFractionsExact() {
        var r = RowReducer.reduce(m(new int[][]{{2, 1}, {1, 3}}), 2);
        assertEquals(Fraction.of(5, 2), r.ref.get(1, 1));
        assertEquals(Matrix.identity(2), r.rref);
    }

    @Test
    void rankOfSingularMatrix() {
        var r = RowReducer.reduce(m(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 3);
        assertEquals(2, r.rank());
    }

    @Test
    void determinantMatchesHandWork() {
        assertEquals(Fraction.of(-1), Determinant.of(m(new int[][]{{2, 1, 1}, {1, 3, 2}, {1, 0, 0}})));
        assertEquals(Fraction.ZERO, Determinant.of(m(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})));
        assertEquals(Fraction.of(-2), Determinant.of(m(new int[][]{{1, 2}, {3, 4}})));
    }

    @Test
    void determinantOfProductAndTranspose() {
        Matrix a = m(new int[][]{{2, 1, 0}, {1, 3, 1}, {0, 1, 4}});
        Matrix b = m(new int[][]{{1, 2, 3}, {0, 1, 4}, {5, 6, 0}});
        assertEquals(Determinant.of(a).mul(Determinant.of(b)), Determinant.of(a.multiply(b)));
        assertEquals(Determinant.of(a), Determinant.of(a.transpose()));
    }

    @Test
    void inverseMultipliesToIdentity() {
        Matrix a = m(new int[][]{{2, 1, 1}, {1, 3, 2}, {1, 0, 0}});
        Inverse inv = Inverse.of(a);
        assertTrue(inv.isInvertible());
        assertEquals(Matrix.identity(3), a.multiply(inv.inverse));
    }

    @Test
    void singularMatrixHasNoInverse() {
        assertFalse(Inverse.of(m(new int[][]{{1, 2}, {2, 4}})).isInvertible());
    }
}
