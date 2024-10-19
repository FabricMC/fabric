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

package net.fabricmc.fabric.api.entity.event.client;

import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ClientPlayerEvents {
	/**
	 * An event that is called when a player is moving during using an item.
	 */
	public static final Event<DisableUsingitemSlowdown> DISABLE_USINGITEM_SLOWDOWN = EventFactory.createArrayBacked(DisableUsingitemSlowdown.class, callbacks -> player -> {
		for (DisableUsingitemSlowdown callback : callbacks) {
			if (callback.disableUsingitemSlowdown(player)) {
				return true;
			}
		}

		return false;
	});

	@FunctionalInterface
	public interface DisableUsingitemSlowdown {
		/**
		 * Called when a player is moving during using an item.
		 *
		 * @param player the player is moving during using an item.
		 * @return true if the player can move without slowdown during using an item, false otherwise.
		 */
		boolean disableUsingitemSlowdown(ClientPlayerEntity player);
	}
}
