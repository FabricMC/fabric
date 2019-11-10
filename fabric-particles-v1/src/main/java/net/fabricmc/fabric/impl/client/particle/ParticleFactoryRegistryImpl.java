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

package net.fabricmc.fabric.impl.client.particle;

import java.util.HashMap;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public final class ParticleFactoryRegistryImpl implements ParticleFactoryRegistry {
	public static final ParticleFactoryRegistryImpl INSTANCE = new ParticleFactoryRegistryImpl();

	final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap<>();
	final Int2ObjectMap<PendingParticleFactory<?>> constructors = new Int2ObjectOpenHashMap<>();
	final Map<Identifier, Integer> constructorsIdsMap = new HashMap<>();

	private ParticleFactoryRegistryImpl() { }

	@Override
	public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
		factories.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
	}

	@Override
	public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> factory) {
		constructors.put(Registry.PARTICLE_TYPE.getRawId(type), factory);
		constructorsIdsMap.put(Registry.PARTICLE_TYPE.getId(type), Registry.PARTICLE_TYPE.getRawId(type));
	}
}
