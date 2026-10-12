package linear.algebra.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FractionTest {
    @Test
    void reducesToLowestTerms() {
        assertEquals(Fraction.of(1, 2), Fraction.of(-3, -6));
        assertEquals("-1/2", Fraction.of(3, -6).toString());
        assertEquals("2", Fraction.of(4, 2).toString());
    }

    @Test
    void arithmeticIsExact() {
        Fraction third = Fraction.of(1, 3);
        assertEquals(Fraction.ONE, third.add(third).add(third));
        assertEquals(Fraction.of(1, 6), Fraction.of(1, 2).sub(third));
        assertEquals(Fraction.of(3, 2), Fraction.of(3).div(Fraction.of(2)));
    }

    @Test
    void parsesCommonFormats() {
        assertEquals(Fraction.of(-3, 4), Fraction.parse(" -3/4 "));
        assertEquals(Fraction.of(1, 4), Fraction.parse("0.25"));
        assertEquals(Fraction.of(7), Fraction.parse("7"));
        assertThrows(NumberFormatException.class, () -> Fraction.parse("abc"));
        assertThrows(NumberFormatException.class, () -> Fraction.parse("1/0"));
        assertThrows(NumberFormatException.class, () -> Fraction.parse(""));
    }

    @Test
    void comparesValues() {
        assertTrue(Fraction.of(1, 3).compareTo(Fraction.of(1, 2)) < 0);
        assertEquals(0, Fraction.of(2, 4).compareTo(Fraction.of(1, 2)));
    }
}
