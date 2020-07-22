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

import java.lang.reflect.Constructor;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

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
		private static final Constructor<? extends SpriteProvider> SIMPLE_SPRITE_PROVIDER_CONSTRUCTOR;
		static {
			try {
				String intermediaryClassName = "net.minecraft.class_702$class_4090";
				String currentClassName = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", intermediaryClassName);
				@SuppressWarnings("unchecked")
				Class<? extends SpriteProvider> clazz = (Class<? extends SpriteProvider>) Class.forName(currentClassName);
				SIMPLE_SPRITE_PROVIDER_CONSTRUCTOR = clazz.getDeclaredConstructor(ParticleManager.class);
				SIMPLE_SPRITE_PROVIDER_CONSTRUCTOR.setAccessible(true);
			} catch (Exception e) {
				throw new RuntimeException("Unable to register particles", e);
			}
		}

		private static SpriteProvider createSimpleSpriteProvider(ParticleManager particleManager) {
			try {
				return SIMPLE_SPRITE_PROVIDER_CONSTRUCTOR.newInstance(particleManager);
			} catch (Exception e) {
				throw new RuntimeException("Unable to create SimpleSpriteProvider", e);
			}
		}

		private final ParticleManager particleManager;

		DirectParticleFactoryRegistry(ParticleManager particleManager) {
			this.particleManager = particleManager;
		}

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, ParticleFactory<T> factory) {
			((ParticleManagerAccessor) particleManager).getFactories().put(Registry.PARTICLE_TYPE.getRawId(type), factory);
		}

		@Override
		public <T extends ParticleEffect> void register(ParticleType<T> type, PendingParticleFactory<T> constructor) {
			SpriteProvider delegate = createSimpleSpriteProvider(particleManager);
			FabricSpriteProvider fabricSpriteProvider = new FabricSpriteProviderImpl(particleManager, delegate);
			((ParticleManagerAccessor) particleManager).getSpriteAwareFactories().put(Registry.PARTICLE_TYPE.getId(type), delegate);
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
