package net.fabricmc.fabric.impl.biome;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import java.util.Random;

public class SimpleLayerRandomnessSource implements LayerRandomnessSource {
	private final long seed;
	private final Random random;
	private final PerlinNoiseSampler sampler;

	public SimpleLayerRandomnessSource(long seed) {
		this.seed = seed;
		this.random = new Random(seed);
		this.sampler = new PerlinNoiseSampler(new Random(seed));
	}

	@Override
	public int nextInt(int bound) {
		return random.nextInt(bound);
	}

	@Override
	public PerlinNoiseSampler getNoiseSampler() {
		return sampler;
	}
}
