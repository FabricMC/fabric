package net.fabricmc.fabric.api.particles.common;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/**
 * Utility methods for creating {@link ParticleType}s.
 * @see net.fabricmc.fabric.api.particles.client.FabricParticles FabricParticles
 */
public final class FabricParticleTypes {
	private FabricParticleTypes() {}

	/** Create a basic particle type that requires no extra information on spawn. */
	public static DefaultParticleType createSimpleParticleType() {
		return createSimpleParticleType(false);
	}

	/**
	 * Create a basic particle type that requires no extra information on spawn.
	 * @param shouldAlwaysSpawn Whether this particle should spawn regardless of distance or client settings (a la barrier particles).
	 */
	public static DefaultParticleType createSimpleParticleType(boolean shouldAlwaysSpawn) {
		return new DefaultParticleType(shouldAlwaysSpawn) {}; // Anonymous class to bypass protected constructor
	}

	/**
	 * Create a particle type for a custom {@link ParticleEffect}. Use this if you need to be able to pass extra data to
	 *  your particle beyond the normal world/position/velocity. Otherwise, use {@link #createSimpleParticleType()}.
	 * See {@link net.minecraft.particle.DustParticleEffect DustParticleEffect} for an example of a custom {@link ParticleEffect}
	 * 	and parameter factory.
	 * @param paramFactory The parameter factory for the {@link ParticleEffect}.
	 * @see net.minecraft.particle.DustParticleEffect DustParticleEffect
	 */
	public static <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory) {
		return createParticleType(paramFactory, false);
	}

	/**
	 * Create a particle type for a custom {@link ParticleEffect}. Use this if you need to be able to pass extra data to
	 *  your particle beyond the normal world/position/velocity. Otherwise, use {@link #createSimpleParticleType(boolean)}.
	 * See {@link net.minecraft.particle.DustParticleEffect DustParticleEffect} for an example of a custom {@link ParticleEffect}
	 * 	and parameter factory.
	 * @param paramFactory The parameter factory for the {@link ParticleEffect}.
	 * @param shouldAlwaysSpawn Whether this particle should spawn regardless of distance or client settings (a la barrier particles).
	 * @see net.minecraft.particle.DustParticleEffect DustParticleEffect
	 */
	public static <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn) {
		return new ParticleType<T>(shouldAlwaysSpawn, paramFactory) {}; // Anonymous class to bypass protected constructor
	}
}
