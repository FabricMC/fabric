package net.fabricmc.fabric.test.tree.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;

import java.util.Random;

/**
 * A basic int provider returning the int you passed it
 */
public class BasicIntProvider extends IntProvider {
	// This codec only contains the value
	public static final Codec<BasicIntProvider> CODEC = RecordCodecBuilder
			.create((instance) ->
					instance.group(Codec.INT
							.fieldOf("value")
							.forGetter((self) -> self.value))
					.apply(instance, BasicIntProvider::new));

	/**
	 * The actual value
	 */
	private final int value;

	public BasicIntProvider(int value) {
		this.value = value;
	}

	@Override
	public int get(Random random) {
		// Just return the value
		return value;
	}

	// The min and max values of this provider are the actual value
	// You can set your own, though

	@Override
	public int getMin() {
		return value;
	}

	@Override
	public int getMax() {
		return value;
	}

	@Override
	public IntProviderType<?> getType() {
		return TreeTest.BASIC_INT_PROVIDER;
	}
}
