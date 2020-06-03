package net.fabricmc.fabric.api.fluids.v1.math;

import net.minecraft.nbt.CompoundTag;

/**
 * Drops are the standard unit of fluid for the fluid api.
 * 1/{@link #BUCKET} is the smallest amount of fluid that can be described with the Drops system.
 * Stored in a long, at one bucket per tick, would take around 11.6 million years to overflow!
 */
public class Drops {
	/**
	 * because of constant inlining use {@link #getBucket()} and {@link #getBuckets(long)}.
	 */
	private static final long BUCKET = 2520L /*this number is divisible by all numbers from 1 to 10 inclusive.*/;

	public static long getBuckets(long number) {
		return BUCKET * number;
	}

	public static long fraction(long numerator, long denominator) {
		if (getBucket() % denominator == 0) {
			return numerator * (getBucket() / denominator);
		}
		throw new UnsupportedOperationException(getBucket() + "is not divisible by " + denominator);
	}

	/**
	 * the standard unit of fluid, this number may change so make sure to use {@link #toTag(CompoundTag, long)} and {@link #fromTag(CompoundTag)}.
	 */
	public static long getBucket() {
		return BUCKET;
	}

	/**
	 * an underflow-conscious way to distribute drops.
	 *
	 * @param value the drops
	 * @param parts the parts
	 */
	public static void ration(long value, long[] parts) {
		long remainder = value % parts.length;
		long ration = value / parts.length;
		for (int i = 0; i < parts.length; i++) {
			if (remainder-- > 0) {
				parts[i] = ration + 1;
			} else {
				parts[i] = ration;
			}
		}
	}

	public static Fraction asFraction(long value) {
		return new Fraction(value / getBucket(), value % getBucket(), getBucket());
	}

	public static void toTag(CompoundTag tag, long amount) {
		tag.putLong("denominator", getBucket());
		tag.putLong("amount", amount);
	}

	public static long fromTag(CompoundTag tag) {
		long denominator = tag.getLong("denominator");
		long amount = tag.getLong("amount");
		double ratio = getBucket() / (double) denominator;
		return (long) (amount * ratio);
	}

	public static long floor(long amount, long fraction) {
		return amount / fraction * fraction;
	}


}
