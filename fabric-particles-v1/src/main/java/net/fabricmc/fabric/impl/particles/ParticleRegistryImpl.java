package net.fabricmc.fabric.impl.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.particles.ParticleRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import java.util.HashMap;

public class ParticleRegistryImpl implements ParticleRegistry {
	public DefaultParticleType createSimpleParticleType() { return createSimpleParticleType(false); }
	public DefaultParticleType createSimpleParticleType(boolean shouldAlwaysSpawn) {
		return new DefaultParticleType(shouldAlwaysSpawn) {};
	}

	public <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory) {
		return createParticleType(paramFactory, false);
	}

	public <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn) {
		return new FabricParticleType<>(shouldAlwaysSpawn, paramFactory);
	}

	@Environment(EnvType.CLIENT)
	public final HashMap<ParticleType<?>, ParticleFactory<?>> factoriesAwaitingRegistry = new HashMap<>();

	@Environment(EnvType.CLIENT)
	public <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory) {
		ParticleManagerHooks manager = (ParticleManagerHooks)MinecraftClient.getInstance().particleManager;
		if(manager != null) manager.fabric_registerCustomFactory(type, factory);
		else factoriesAwaitingRegistry.put(type, factory);
	}

	private static class FabricParticleType<T extends ParticleEffect> extends ParticleType<T> {
		FabricParticleType(boolean shouldAlwaysSpawn, ParticleEffect.Factory<T> paramFactory) {
			super(shouldAlwaysSpawn, paramFactory);
		}
	}
}
