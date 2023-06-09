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

package net.fabricmc.fabric.api.resource.loader.v1.client;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderHolder;

public final class ClientResourceReloadEvents {
	/**
	 * Event to register resource reloaders for client resource packs (i.e. {@link ResourceType#CLIENT_RESOURCES}).
	 *
	 * <p>This event fires <b>a single time</b> when the client is started.
	 * The recommended usage pattern is to register a new instance of your reload listeners when this event fires.
	 *
	 * <p>Registered resource reloaders can be accessed through the {@link MinecraftClient} (during and after the reload)
	 * via {@link ResourceReloaderHolder#getResourceReloader}.
	 */
	public static final Event<RegisterReloaders> REGISTER_RELOADERS = EventFactory.createArrayBacked(RegisterReloaders.class, callbacks -> context -> {
		for (RegisterReloaders callback : callbacks) {
			callback.registerResourceReloaders(context);
		}
	});

	/**
	 * Event that fires at the start of each resource reload, after the resource manager is updated, but before resource reloaders run.
	 */
	public static final Event<StartReload> START_RELOAD = EventFactory.createArrayBacked(StartReload.class, callbacks -> context -> {
		for (StartReload callback : callbacks) {
			callback.onStartResourceReload(context);
		}
	});

	/**
	 * Event that fires right when the resource reloaders finish running, but before subsequent cleanup has been performed.
	 * For example, the world renderer was not refreshed yet when this event fires.
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
		 * Provides access to the minecraft client.
		 */
		MinecraftClient getMinecraftClient();
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
		 * <p>The identifiers in {@link net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderKeys} can be used to refer to vanilla resource reloaders.
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

	private ClientResourceReloadEvents() {
	}
}
