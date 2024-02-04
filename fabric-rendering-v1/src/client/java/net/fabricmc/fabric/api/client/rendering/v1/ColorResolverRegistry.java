package net.fabricmc.fabric.api.client.rendering.v1;

import java.util.Set;

import org.jetbrains.annotations.UnmodifiableView;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.ColorResolver;

import net.fabricmc.fabric.impl.client.rendering.ColorResolverRegistryImpl;

/**
 * The registry for custom {@link ColorResolver}s. Custom resolvers must be registered during client initialization for
 * them to be usable in {@link BlockRenderView#getColor}. Calling this method may throw an exception if the passed
 * resolver is not registered with this class. Vanilla resolvers found in {@link BiomeColors} are automatically
 * registered.
 *
 * <p>Other mods may also require custom resolvers to be registered if they provide additional functionality related to
 * color resolvers.
 */
public final class ColorResolverRegistry {
	private ColorResolverRegistry() {
	}

	/**
	 * Registers a custom {@link ColorResolver} for use in {@link BlockRenderView#getColor}. This method should be
	 * called during client initialization.
	 *
	 * @param resolver the resolver to register
	 */
	public static void register(ColorResolver resolver) {
		ColorResolverRegistryImpl.register(resolver);
	}

	/**
	 * Gets a view of all registered {@link ColorResolver}s. This includes all vanilla resolvers.
	 *
	 * @return a view of all registered resolvers
	 */
	@UnmodifiableView
	public static Set<ColorResolver> getAllRegistered() {
		return ColorResolverRegistryImpl.getAllRegistered();
	}

	/**
	 * Checks whether the given {@link ColorResolver} is registered. Vanilla resolvers are always registered.
	 *
	 * @param resolver the resolver
	 * @return whether the given resolver is registered
	 */
	public static boolean isRegistered(ColorResolver resolver) {
		return getAllRegistered().contains(resolver);
	}
}
