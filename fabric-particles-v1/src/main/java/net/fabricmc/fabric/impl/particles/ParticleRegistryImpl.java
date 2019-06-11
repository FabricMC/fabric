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
