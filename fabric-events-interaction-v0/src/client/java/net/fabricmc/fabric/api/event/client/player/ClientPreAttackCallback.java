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
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * <p>
 * Callback for when the client player left-clicks. If the callback returns true, vanilla handling
 * (including breaking block or attacking entity) will be cancelled.
 * </p>
 * <p>
 * This event is client-only, so handling this event probably involves sending custom packets.
 * </p>
 * <p>
 * This event will not fire when in attack cooldown or when the player hand is busy riding.
 * </p>
 * <p>
 * If the player clicks multiple times in one tick, this event may be fired multiple times in one tick.
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
	 * @return true to intercept the attack, false to continue
	 */
	boolean onClientPlayerPreAttack(ClientPlayerEntity player);
}
