package net.fabricmc.fabric.impl.client.rendering;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.UnmodifiableView;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.biome.ColorResolver;

public final class ColorResolverRegistryImpl {
	private static final Set<ColorResolver> RESOLVERS = new HashSet<>();
	private static final Set<ColorResolver> RESOLVERS_VIEW = Collections.unmodifiableSet(RESOLVERS);

	static {
		register(BiomeColors.GRASS_COLOR);
		register(BiomeColors.FOLIAGE_COLOR);
		register(BiomeColors.WATER_COLOR);
	}

	private ColorResolverRegistryImpl() {
	}

	public static void register(ColorResolver resolver) {
		RESOLVERS.add(resolver);
	}

	@UnmodifiableView
	public static Set<ColorResolver> getAllRegistered() {
		return RESOLVERS_VIEW;
	}
}
