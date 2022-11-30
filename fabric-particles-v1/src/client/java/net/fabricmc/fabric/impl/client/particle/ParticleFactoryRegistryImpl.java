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

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;

public final class ParticleFactoryRegistryImpl implements ParticleFactoryRegistry {
	public static final ParticleFactoryRegistryImpl INSTANCE = new ParticleFactoryRegistryImpl();

	static class DeferredParticleFactoryRegistry implements ParticleFactoryRegistry {
		private final Map<ParticleType<?>, ParticleFactory<?>> factories = new IdentityHashMap<>();
		private final Map<ParticleType<?>, PendingParticleFactory<?>> constructors = new IdentityHashMap<>();

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
			factories.put(type, factory);
		}

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> factory) {
			constructors.put(type, factory);
		}

		@SuppressWarnings("unchecked")
		void applyTo(ParticleFactoryRegistry registry) {
			for (Map.Entry<ParticleType<?>, ParticleFactory<?>> entry : factories.entrySet()) {
				ParticleType type = entry.getKey();
				ParticleFactory factory = entry.getValue();
				registry.register(type, factory);
			}

			for (Map.Entry<ParticleType<?>, PendingParticleFactory<?>> entry : constructors.entrySet()) {
				ParticleType type = entry.getKey();
				PendingParticleFactory constructor = entry.getValue();
				registry.register(type, constructor);
			}
		}
	}

	static class DirectParticleFactoryRegistry implements ParticleFactoryRegistry {
		private final ParticleManager particleManager;

		DirectParticleFactoryRegistry(ParticleManager particleManager) {
			this.particleManager = particleManager;
		}

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
			((ParticleManagerAccessor) particleManager).getFactories().put(Registries.PARTICLE_TYPE.getRawId(type), factory);
		}

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> constructor) {
			SpriteProvider delegate = new ParticleManager.SimpleSpriteProvider();
			FabricSpriteProvider fabricSpriteProvider = new FabricSpriteProviderImpl(particleManager, delegate);
			((ParticleManagerAccessor) particleManager).getSpriteAwareFactories().put(Registries.PARTICLE_TYPE.getId(type), delegate);
			register(type, constructor.create(fabricSpriteProvider));
		}
	}

	ParticleFactoryRegistry internalRegistry = new DeferredParticleFactoryRegistry();

	private ParticleFactoryRegistryImpl() { }

	@Override
	public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
		internalRegistry.register(type, factory);
	}

	@Override
	public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> constructor) {
		internalRegistry.register(type, constructor);
	}

	public void initialize(ParticleManager particleManager) {
		ParticleFactoryRegistry newRegistry = new DirectParticleFactoryRegistry(particleManager);
		DeferredParticleFactoryRegistry oldRegistry = (DeferredParticleFactoryRegistry) internalRegistry;
		oldRegistry.applyTo(newRegistry);
		internalRegistry = newRegistry;
	}
}
