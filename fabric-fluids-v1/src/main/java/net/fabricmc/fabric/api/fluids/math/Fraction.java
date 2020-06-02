package net.fabricmc.fabric.api.fluids.math;

import com.google.common.math.LongMath;

/**
 * a utility class for displaying drops as readable fractions to users
 *
 * @see Drops#asFraction(long)
 * @see #toString()
 */
public final class Fraction {
	public final long proper, numerator, denominator;

	public Fraction(long proper, long numerator, long denominator) {
		this.proper = proper;
		long gcd = LongMath.gcd(numerator, denominator);
		this.numerator = numerator / gcd;
		this.denominator = denominator / gcd;
	}

	@Override
	public int hashCode() {
		int result = (int) (this.proper ^ (this.proper >>> 32));
		result = 31 * result + (int) (this.numerator ^ (this.numerator >>> 32));
		result = 31 * result + (int) (this.denominator ^ (this.denominator >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		Fraction fraction = (Fraction) o;

		if (this.proper != fraction.proper) return false;
		if (this.numerator != fraction.numerator) return false;
		return this.denominator == fraction.denominator;
	}

	@Override
	public String toString() {
		return this.proper + " " + this.numerator + "/" + this.denominator;
	}
}
