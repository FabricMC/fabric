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
import org.jetbrains.annotations.Nullable;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerResourceReloadEvents {
	/**
	 * Event to register resource reloaders for data packs (i.e. {@link ResourceType#SERVER_DATA}).
	 *
	 * <p>This event fires every time a server (either dedicated or integrated) is started, or when a data pack reload is requested.
	 * The recommended usage pattern is to register a new instance of your reload listeners every time this event fires.
	 *
	 * <p>Registered resource reloaders can be accessed through either {@link DataPackContents} (during the reload)
	 * or {@link MinecraftServer} (once the reload is complete, if successful) via {@link ResourceReloaderHolder#getResourceReloader}.
	 */
	public static final Event<RegisterReloaders> REGISTER_RELOADERS = EventFactory.createArrayBacked(RegisterReloaders.class, callbacks -> context -> {
		for (RegisterReloaders callback : callbacks) {
			callback.registerResourceReloaders(context);
		}
	});

	/**
	 * Event that fires after the {@linkplain #REGISTER_RELOADERS reloaders are registered}, right when the reload starts.
	 */
	public static final Event<StartReload> START_RELOAD = EventFactory.createArrayBacked(StartReload.class, callbacks -> context -> {
		for (StartReload callback : callbacks) {
			callback.onStartResourceReload(context);
		}
	});

	/**
	 * Event that fires right when the resource reloaders finish running, but before the result of the reload is applied.
	 * For example, tags are not bound yet when this event fires.
	 */
	public static final Event<EndReload> END_RELOAD = EventFactory.createArrayBacked(EndReload.class, callbacks -> (context, success) -> {
		for (EndReload callback : callbacks) {
			callback.onEndResourceReload(context, success);
		}
	});

	@FunctionalInterface
	public interface RegisterReloaders {
		void registerResourceReloaders(RegisterContext context);
	}

	@FunctionalInterface
	public interface StartReload {
		void onStartResourceReload(Context context);
	}

	@FunctionalInterface
	public interface EndReload {
		/**
		 * @param success {@code true} if the reload was successful, {@code false} otherwise
		 */
		void onEndResourceReload(Context context, boolean success);
	}

	/**
	 * Context available to all resource reload events.
	 */
	@ApiStatus.NonExtendable
	public interface Context {
		/**
		 * Provides access to the new resource manager.
		 */
		ResourceManager getResourceManager();

		/**
		 * Provides access to the dynamic registry manager.
		 */
		DynamicRegistryManager getRegistries();

		/**
		 * Provides access to both vanilla resource reloaders (via its public methods)
		 * and modded resource reloaders (via {@link ResourceReloaderHolder#getResourceReloader}),
		 * <b>only for use in the apply phase of resource reloading or once the reload succeeded</b>.
		 */
		DataPackContents getDataPackContents();

		/**
		 * Provides access to the minecraft server. {@code null} for the initial resource loading,
		 * and available for data pack reloads (using the {@code /reload} command).
		 */
		@Nullable
		MinecraftServer getMinecraftServer();
	}

	/**
	 * Extended context available to {@link RegisterReloaders} that allows registering reloaders.
	 */
	@ApiStatus.NonExtendable
	public interface RegisterContext extends Context {
		/**
		 * Register a new resource reloader.
		 */
		void addReloader(Identifier identifier, ResourceReloader reloader);

		/**
		 * Request that <b>the apply phase</b> of one reloader be executed before <b>the apply phase</b> of another reloader.
		 * Prepare phases happen in parallel, and cannot be ordered.
		 *
		 * <p>The identifiers in {@link ResourceReloaderKeys} can be used to refer to vanilla resource reloaders.
		 * Unless otherwise requested, reloaders will be ordered after vanilla resource reloaders.
		 *
		 * <p>Incompatible ordering constraints such as cycles will lead to inconsistent behavior:
		 * some constraints will be respected and some will be ignored. If this happens, a warning will be logged.
		 *
		 * @param firstReloader The identifier of the reloader that should run before the other.
		 * @param secondReloader The identifier of the reloader that should run after the other.
		 */
		void addReloaderOrdering(Identifier firstReloader, Identifier secondReloader);
	}

	private ServerResourceReloadEvents() {
	}
}
