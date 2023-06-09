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

package net.fabricmc.fabric.api.resource.loader.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

/**
 * Injected interface to grant access to modded resource reloaders by id.
 *
 * <p>When implemented on {@link DataPackContents} or {@link MinecraftServer},
 * provides access to the reloaders registered through {@link ServerResourceReloadEvents.RegisterReloaders}.
 *
 * <p>When implemented on {@code MinecraftClient},
 * provides access to the reloaders registered through {@code ServerResourceReloadEvents.RegisterReloaders}.
 */
@ApiStatus.NonExtendable
public interface ResourceReloaderHolder {
	/**
	 * Retrieves a registered resource reloader by id, if it exists, or {@code null} if it doesn't.
	 */
	// No Nullable annotation to match Map.get
	default ResourceReloader getResourceReloader(Identifier identifier) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
