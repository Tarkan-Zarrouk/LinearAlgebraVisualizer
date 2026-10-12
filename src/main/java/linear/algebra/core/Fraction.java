package linear.algebra.core;

import java.math.BigDecimal;

/**
 * An exact rational number stored in lowest terms with a positive denominator.
 */
public final class Fraction implements Comparable<Fraction> {
    /**
     * The exact fraction zero.
     */
    public static final Fraction ZERO = new Fraction(0, 1);
    /**
     * The exact fraction one.
     */
    public static final Fraction ONE = new Fraction(1, 1);

    private final long numerator;
    private final long denominator;

    /**
     * Creates a fraction from reduced numerator and denominator values.
     * @param numerator the reduced numerator
     * @param denominator the positive denominator
     */
    private Fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * Creates a fraction from an integer.
     * @param numeratorValue the integer value
     * @return the exact integer fraction
     */
    public static Fraction of(long numeratorValue) {
        return new Fraction(numeratorValue, 1);
    }

    /**
     * Creates a reduced fraction from its numerator and denominator.
     * @param numeratorValue the numerator
     * @param denominatorValue the denominator
     * @return the reduced fraction
     * @throws ArithmeticException if the denominator is zero.
     */
    public static Fraction of(long numeratorValue, long denominatorValue) {
        if (denominatorValue == 0) {
            throw new ArithmeticException("denominator is zero");
        }
        if (denominatorValue < 0) {
            numeratorValue = -numeratorValue;
            denominatorValue = -denominatorValue;
        }
        long greatestCommonDivisor = gcd(Math.abs(numeratorValue), denominatorValue);
        return new Fraction(numeratorValue / greatestCommonDivisor, denominatorValue / greatestCommonDivisor);
    }

    /**
     * Parses an integer, fraction, or decimal representation.
     * @param text the numeric text to parse
     * @return the parsed fraction
     * @throws NumberFormatException if the text is not a supported number.
     */
    public static Fraction parse(String text) {
        text = text.trim();
        if (text.isEmpty()) {
            throw new NumberFormatException("empty entry");
        }
        int slash = text.indexOf('/');
        if (slash >= 0) {
            long numeratorValue = Long.parseLong(text.substring(0, slash).trim());
            long denominatorValue = Long.parseLong(text.substring(slash + 1).trim());
            if (denominatorValue == 0) {
                throw new NumberFormatException("denominator is zero");
            }
            return of(numeratorValue, denominatorValue);
        }
        if (text.contains(".")) {
            BigDecimal decimal = new BigDecimal(text);
            long scale = 1;
            for (int i = 0; i < decimal.scale(); i++) {
                scale *= 10;
            }
            return of(decimal.movePointRight(decimal.scale()).longValueExact(), scale);
        }
        return of(Long.parseLong(text));
    }

    /**
     * Returns the greatest common divisor, using one for a zero numerator.
     * @param left the first Euclidean operand
     * @param right the second Euclidean operand
     * @return the greatest common divisor
     */
    private static long gcd(long left, long right) {
        while (right != 0) {
            long remainder = left % right;
            left = right;
            right = remainder;
        }
        if (left == 0) {
            return 1;
        }
        return left;
    }

    /**
     * Returns the reduced numerator.
     * @return the numerator
     */
    public long numerator() {
        return numerator;
    }

    /**
     * Returns the positive denominator.
     * @return the positive denominator
     */
    public long denominator() {
        return denominator;
    }

    /**
     * Adds another fraction exactly.
     * @param other the other value
     * @return the exact sum
     */
    public Fraction add(Fraction other) {
        return of(Math.addExact(Math.multiplyExact(numerator, other.denominator), Math.multiplyExact(other.numerator, denominator)),
        Math.multiplyExact(denominator, other.denominator));
    }

    /**
     * Subtracts another fraction exactly.
     * @param other the other value
     * @return the exact difference
     */
    public Fraction sub(Fraction other) {
        return add(other.negate());
    }

    /**
     * Multiplies by another fraction exactly.
     * @param other the other value
     * @return the exact product
     */
    public Fraction mul(Fraction other) {
        return of(Math.multiplyExact(numerator, other.numerator), Math.multiplyExact(denominator, other.denominator));
    }

    /**
     * Divides by another fraction exactly.
     * @param other the other value
     * @return the exact quotient
     * @throws ArithmeticException if the divisor is zero.
     */
    public Fraction div(Fraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("division by zero");
        }
        return of(Math.multiplyExact(numerator, other.denominator), Math.multiplyExact(denominator, other.numerator));
    }

    /**
     * Returns the additive inverse.
     * @return the additive inverse
     */
    public Fraction negate() {
        return new Fraction(-numerator, denominator);
    }

    /**
     * Returns the non-negative magnitude.
     * @return the non-negative fraction
     */
    public Fraction abs() {
        if (numerator < 0) {
            return negate();
        }
        return this;
    }

    /**
     * Reports whether this fraction is zero.
     * @return true if this fraction is zero
     */
    public boolean isZero() {
        return numerator == 0;
    }

    /**
     * Reports whether this fraction is one.
     * @return true if this fraction is one
     */
    public boolean isOne() {
        return numerator == 1 && denominator == 1;
    }

    /**
     * Reports whether this fraction is an integer.
     * @return true if this fraction is an integer
     */
    public boolean isInteger() {
        return denominator == 1;
    }

    /**
     * Returns the sign of this fraction.
     * @return a negative, zero, or positive value
     */
    public int signum() {
        return Long.signum(numerator);
    }

    /**
     * Converts this fraction to a floating-point value.
     * @return the approximate numeric value
     */
    public double toDouble() {
        return (double) numerator / denominator;
    }

    /**
     * Compares this fraction with another fraction.
     * @param other the other value
     * @return a negative, zero, or positive value according to the ordering
     */
    @Override
    public int compareTo(Fraction other) {
        return Long.compare(Math.multiplyExact(numerator, other.denominator), Math.multiplyExact(other.numerator, denominator));
    }

    /**
     * Compares this value with another object.
     * @param other the other value
     * @return true if the objects represent the same value
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Fraction f && f.numerator == numerator && f.denominator == denominator;
    }

    /**
     * Returns a hash code consistent with equality.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Long.hashCode(numerator) * 31 + Long.hashCode(denominator);
    }

    /**
     * Returns the standard integer or fraction representation.
     * @return the standard representation
     */
    @Override
    public String toString() {
        if (denominator == 1) {
            return Long.toString(numerator);
        }
        return numerator + "/" + denominator;
    }
}
