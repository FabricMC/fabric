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

package net.fabricmc.fabric.api.client.entity.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.client.network.ClientPlayerEntity;

public final class ClientPlayerEvents {
	/*
	 * An event that is called when a player is moving during using an item.
	 */
	public static final Event<ModifyPlayerMovementDuringUsingitem> MODIFY_PLAYER_MOVEMENT_DURING_USINGITEM = EventFactory.createArrayBacked(ModifyPlayerMovementDuringUsingitem.class, callbacks -> player -> {
		for (ModifyPlayerMovementDuringUsingitem callback : callbacks) {
			callback.modifyPlayerMovementDuringUsingitem(player);
		}
	});

	@FunctionalInterface
	public interface ModifyPlayerMovementDuringUsingitem {
		/**
		 * @param player the player is moving during using an item.
		 */
		void modifyPlayerMovementDuringUsingitem(ClientPlayerEntity player);
	}
}
