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

/** Core methods for registering particles with the Fabric API. */
public class ParticleRegistryImpl implements ParticleRegistry {
	public DefaultParticleType createSimpleParticleType() { return createSimpleParticleType(false); }
	public DefaultParticleType createSimpleParticleType(boolean shouldAlwaysSpawn) {
		return new DefaultParticleType(shouldAlwaysSpawn) {}; // Anonymous class to bypass protected constructor
	}

	public <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory) { return createParticleType(paramFactory, false); }
	public <T extends ParticleEffect> ParticleType<T> createParticleType(ParticleEffect.Factory<T> paramFactory, boolean shouldAlwaysSpawn) {
		return new ParticleType<T>(shouldAlwaysSpawn, paramFactory) {}; // Anonymous class to bypass protected constructor
	}

	/**
	 * Cache of particle factories awaiting registration.
	 * Used to allow for registering particles with the API before {@link MinecraftClient#particleManager} is ready.
	 * Don't access this directly. Just don't.
	 */
	@Environment(EnvType.CLIENT)
	public final HashMap<ParticleType<?>, ParticleFactory<?>> factoriesAwaitingRegistry = new HashMap<>();

	@Environment(EnvType.CLIENT)
	public <T extends ParticleEffect> void registerParticleFactory(ParticleType<T> type, ParticleFactory<T> factory) {
		ParticleManagerHooks manager = (ParticleManagerHooks)MinecraftClient.getInstance().particleManager;
		if(manager != null) manager.fabric_registerCustomFactory(type, factory);
		else factoriesAwaitingRegistry.put(type, factory);
	}
}
