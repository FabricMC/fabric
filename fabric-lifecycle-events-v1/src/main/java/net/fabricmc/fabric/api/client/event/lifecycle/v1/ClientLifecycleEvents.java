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

package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientLifecycleEvents {
	private ClientLifecycleEvents() {
	}

	/**
	 * Called when Minecraft has started and it's client about to tick for the first time.
	 *
	 * <p>This occurs while the splash screen is displayed.
	 */
	public static final Event<ClientStarted> CLIENT_STARTED = EventFactory.createArrayBacked(ClientStarted.class, callbacks -> client -> {
		for (ClientStarted callback : callbacks) {
			callback.onClientStarted(client);
		}
	});

	/**
	 * Called when Minecraft's client begins to stop.
	 * This is caused by quitting while in game, or closing the game window.
	 *
	 * <p>This will be called before the integrated server is stopped if it is running.
	 */
	public static final Event<ClientStopping> CLIENT_STOPPING = EventFactory.createArrayBacked(ClientStopping.class, callbacks -> client -> {
		for (ClientStopping callback : callbacks) {
			callback.onClientStopping(client);
		}
	});

	/**
	 * Called when the current player in the Minecraft game has moved to a different world.
	 * This occurs when a new world has been created, but before the client's {@link MinecraftClient#world current world} is disposed.
	 */
	public static final Event<ChangeWorld> CHANGE_WORLD = EventFactory.createArrayBacked(ChangeWorld.class, callbacks -> (client, world, destination) -> {
		for (ChangeWorld callback : callbacks) {
			callback.onChangeWorld(client, world, destination);
		}
	});

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface ClientStarted {
		void onClientStarted(MinecraftClient client);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface ClientStopping {
		void onClientStopping(MinecraftClient client);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface ChangeWorld {
		/**
		 * Called when the current player in the Minecraft game has moved to a different world.
		 * A mod may use this event for reference cleanup on the current world.
		 *
		 * @param client the Minecraft client
		 * @param origin the world the player is currently in
		 * @param destination the world the player is being moved to
		 */
		void onChangeWorld(MinecraftClient client, ClientWorld origin, ClientWorld destination);
	}
}
