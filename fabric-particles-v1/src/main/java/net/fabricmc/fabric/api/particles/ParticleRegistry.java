package net.fabricmc.fabric.api.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.particles.ParticleRegistryImpl;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public interface ParticleRegistry {
	ParticleRegistry INSTANCE = new ParticleRegistryImpl();

	DefaultParticleType createSimpleParticleType();
	DefaultParticleType createSimpleParticleType(boolean shouldAlwaysSpawn);

	<T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory);
	<T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn);

	@Environment(EnvType.CLIENT)
	<T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory);
}
