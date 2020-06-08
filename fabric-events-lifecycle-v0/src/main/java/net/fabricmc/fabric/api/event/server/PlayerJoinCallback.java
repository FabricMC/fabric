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

package net.fabricmc.fabric.api.event.server;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is called when a player joins the server.
 *
 * <p>It hooks in at the end of {@link net.minecraft.server.PlayerManager#onPlayerConnect(ClientConnection, ServerPlayerEntity)} through {@link net.fabricmc.fabric.mixin.event.lifecycle.MixinPlayerManager}.
 */
public interface PlayerJoinCallback {
	Event<PlayerJoinCallback> EVENT = EventFactory.createArrayBacked(PlayerJoinCallback.class,
			(listeners) -> (playerEntity) -> {
				for (PlayerJoinCallback event : listeners) {
					event.onPlayerJoin(playerEntity);
				}
			}
	);

	void onPlayerJoin(ServerPlayerEntity player);
}
