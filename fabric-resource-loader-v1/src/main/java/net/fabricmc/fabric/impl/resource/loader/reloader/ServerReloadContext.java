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

package net.fabricmc.fabric.impl.resource.loader.reloader;

import java.util.Map;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.loader.v1.ServerResourceReloadEvents;

public class ServerReloadContext implements ServerResourceReloadEvents.RegisterContext {
	public static final Map<DynamicRegistryManager.Immutable, MinecraftServer> SERVER_BY_DRM = new WeakHashMap<>();

	public final ReloaderSorting reloaderSorting = new ServerReloaderSorting();
	private final ResourceManager resourceManager;
	private final DynamicRegistryManager registries;
	private final DataPackContents dataPackContents;
	@Nullable
	private final MinecraftServer minecraftServer;

	public ServerReloadContext(ResourceManager resourceManager, DynamicRegistryManager registries, DataPackContents dataPackContents) {
		this.resourceManager = resourceManager;
		this.registries = registries;
		this.dataPackContents = dataPackContents;
		this.minecraftServer = SERVER_BY_DRM.remove(registries);
	}

	@Override
	public void addReloader(Identifier identifier, ResourceReloader reloader) {
		reloaderSorting.addReloader(identifier, reloader);
	}

	@Override
	public void addReloaderOrdering(Identifier firstReloader, Identifier secondReloader) {
		reloaderSorting.addReloaderOrdering(firstReloader, secondReloader);
	}

	@Override
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	@Override
	public DynamicRegistryManager getRegistries() {
		return registries;
	}

	@Override
	public DataPackContents getDataPackContents() {
		return dataPackContents;
	}

	@Override
	@Nullable
	public MinecraftServer getMinecraftServer() {
		return minecraftServer;
	}
}
