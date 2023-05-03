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

package net.fabricmc.fabric.api.event.client.player;

import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * <p>
 * This event fires when the client player presses the attack key (left mouse button by default),
 * excluding cases when in attack cooldown or the player's hand is occupied with riding.
 * The event fires before vanilla handling (block breaking, entity attacking).
 * If the callback returns true, the vanilla handling will be cancelled.
 * </p>
 * <p>
 * This event is client-only, which means handling it may require sending custom packets.
 * </p>
 * <p>
 * In case the player presses the attack key multiple times within a single tick,
 * this event might be triggered multiple times during that tick.
 * {@link net.minecraft.entity.player.ItemCooldownManager} may be useful.
 * </p>
 */
public interface ClientPreAttackCallback {
	Event<ClientPreAttackCallback> EVENT = EventFactory.createArrayBacked(
			ClientPreAttackCallback.class,
			(listeners) -> (player) -> {
				for (ClientPreAttackCallback event : listeners) {
					boolean intercepts = event.onClientPlayerPreAttack(player);

					if (intercepts) {
						return true;
					}
				}

				return false;
			}
	);

	/**
	 * @param player the client player
	 * @return true to intercept vanilla attack handling, false to continue
	 */
	boolean onClientPlayerPreAttack(ClientPlayerEntity player);
}
