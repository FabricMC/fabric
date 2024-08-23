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

package net.fabricmc.fabric.test.resource.loader;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;

public class ResourceReloadListenerTestMod implements ModInitializer {
	public static final String MODID = "fabric-resource-loader-v0-testmod";

	private static boolean clientResources = false;
	private static boolean serverResources = false;

	@Override
	public void onInitialize() {
		setupClientReloadListeners();
		setupServerReloadListeners();

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			if (!clientResources && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				throw new AssertionError("Client reload listener was not called.");
			}

			if (!serverResources) {
				throw new AssertionError("Server reload listener was not called.");
			}
		});
	}

	private void setupClientReloadListeners() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Identifier.of(MODID, "client_second");
			}

			@Override
			public void reload(ResourceManager manager) {
				if (!clientResources) {
					throw new AssertionError("Second reload listener was called before the first!");
				}
			}

			@Override
			public Collection<Identifier> getFabricDependencies() {
				return Collections.singletonList(Identifier.of(MODID, "client_first"));
			}
		});

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Identifier.of(MODID, "client_first");
			}

			@Override
			public void reload(ResourceManager manager) {
				clientResources = true;
			}
		});
	}

	private void setupServerReloadListeners() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Identifier.of(MODID, "server_second");
			}

			@Override
			public void reload(ResourceManager manager) {
				if (!serverResources) {
					throw new AssertionError("Second reload listener was called before the first!");
				}
			}

			@Override
			public Collection<Identifier> getFabricDependencies() {
				return Collections.singletonList(Identifier.of(MODID, "server_first"));
			}
		});

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Identifier.of(MODID, "server_first");
			}

			@Override
			public void reload(ResourceManager manager) {
				serverResources = true;
			}
		});

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(RegistryReloader.ID, RegistryReloader::new);
	}

	private record RegistryReloader(RegistryWrapper.WrapperLookup wrapperLookup) implements SimpleSynchronousResourceReloadListener {
		private static final Identifier ID = Identifier.of(MODID, "registry_reloader");

		@Override
		public Identifier getFabricId() {
			return ID;
		}

		@Override
		public void reload(ResourceManager manager) {
			Objects.requireNonNull(wrapperLookup);
			wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
		}
	}
}
