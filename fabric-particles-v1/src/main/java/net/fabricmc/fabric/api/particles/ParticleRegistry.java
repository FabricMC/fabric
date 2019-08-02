/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.particles.ParticleManagerHooks;
import net.fabricmc.fabric.impl.particles.ParticleRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

/** Core methods for registering particles with the Fabric API. */
public interface ParticleRegistry {
	ParticleRegistry INSTANCE = new ParticleRegistryImpl();

	/** Retrieve the appropriate sprite atlas for particle textures. */
	default SpriteAtlasTexture getParticleSpriteAtlas() {
		return ((ParticleManagerHooks)MinecraftClient.getInstance().particleManager).fabric_getSpriteAtlasTexture();
	}

	/** Create a basic particle type that requires no extra information on spawn. */
	default ParticleType<?> createSimpleParticleType() {
		return createSimpleParticleType(false);
	}

	/**
	 * Create a basic particle type that requires no extra information on spawn.
	 *
	 * @param shouldAlwaysSpawn Whether this particle should spawn regardless of distance or client settings (a la barrier particles).
	 */
	default ParticleType<?> createSimpleParticleType(boolean shouldAlwaysSpawn) {
		return new DefaultParticleType(shouldAlwaysSpawn) {}; // Anonymous class to bypass protected constructor
	}

	/**
	 * Create a particle type for a custom {@link ParticleEffect}. Use this if you need to pass extra data to your particle
	 * 	beyond the normal world/position/velocity.
	 * See {@link net.minecraft.particle.DustParticleEffect DustParticleEffect} for an example of a custom {@link ParticleEffect}
	 * 	and parameter factory.
	 *
	 * @param paramFactory The parameter factory for the {@link ParticleEffect}.
	 * @see net.minecraft.particle.DustParticleEffect DustParticleEffect
	 */
	default <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory) {
		return createParticleType(paramFactory, false);
	}

	/**
	 * Create a particle type for a custom {@link ParticleEffect}. Use this if you need to pass extra data to your particle
	 * 	beyond the normal world/position/velocity.
	 * See {@link net.minecraft.particle.DustParticleEffect DustParticleEffect} for an example of a custom {@link ParticleEffect}
	 * 	and parameter factory.
	 *
	 * @param paramFactory The parameter factory for the {@link ParticleEffect}.
	 * @param shouldAlwaysSpawn Whether this particle should spawn regardless of distance or client settings (a la barrier particles).
	 * @see net.minecraft.particle.DustParticleEffect DustParticleEffect
	 */
	default <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn) {
		return new ParticleType<T>(shouldAlwaysSpawn, paramFactory) {}; // Anonymous class to bypass protected constructor
	}

	/**
	 * Register a {@link ParticleFactory} for the given {@link ParticleType}.
	 *
	 * @param type The type to register a factory for.
	 * @param factory The factory method.
	 */
	@Environment(EnvType.CLIENT)
	<T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory);
}
