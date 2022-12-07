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

package net.fabricmc.fabric.test.registry.sync;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.slf4j.Logger;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryFinalizeCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryRegistry;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

public class DynamicRegistryTest implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String MODID = "fabric-registry-sync-v0-testmod";

	@Override
	public void onInitialize() {
		try {
			DynamicRegistryRegistry.registerBefore(RegistryKeys.BIOME, DynamicData.BEFORE_KEY, DynamicData.CODEC);
			DynamicRegistryRegistry.registerAfter(RegistryKeys.BIOME, DynamicData.AFTER_KEY, DynamicData.CODEC);
			DynamicRegistryRegistry.register(RegistryKey.ofRegistry(new Identifier(MODID, "worldgen/biome")), Codec.BOOL);
		} catch (IllegalStateException ignored) {
			LOGGER.info("DynamicRegistryRegistry path clash test passed!");
		}

		final AtomicBoolean setupCalled = new AtomicBoolean(false);
		final AtomicBoolean finalizeCalled = new AtomicBoolean(false);

		DynamicRegistrySetupCallback.EVENT.register(registryManager -> {
			setupCalled.set(true);
			registryManager.registerEntryAdded(RegistryKeys.BIOME, (rawId, id, object) -> LOGGER.info("Biome added: {}", id));
			registryManager.registerEntryAdded(DynamicData.BEFORE_KEY, (rawId, id, object) -> LOGGER.info("Before biome data: {}", id));
			registryManager.registerEntryAdded(DynamicData.AFTER_KEY, (rawId, id, object) -> LOGGER.info("After biome data: {}", id));
		});

		DynamicRegistryFinalizeCallback.EVENT.register(registryManager -> {
			finalizeCalled.set(true);
			registryManager.streamAllRegistries().forEach(entry -> LOGGER.info("[%s]:[%s]".formatted(entry.key().getValue(), entry.value().size())));
		});

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			if (!setupCalled.get()) {
				throw new IllegalStateException("DRM setup was not called before startup!");
			}

			if (!finalizeCalled.get()) {
				throw new IllegalStateException("DRM finalize was not called before startup!");
			}
		});
	}

	private record DynamicData(RegistryEntry<Biome> biome) {
		private static final RegistryKey<Registry<DynamicData>> BEFORE_KEY = RegistryKey.ofRegistry(new Identifier(MODID, "fabric-api/before_biome"));
		private static final RegistryKey<Registry<DynamicData>> AFTER_KEY = RegistryKey.ofRegistry(new Identifier(MODID, "fabric-api/after_biome"));
		private static final Codec<DynamicData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Biome.REGISTRY_CODEC.fieldOf("biome").forGetter(DynamicData::biome)
		).apply(instance, DynamicData::new));
	}
}
