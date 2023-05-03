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
 * This event fires every tick when the the attack key (left mouse button by default) is in pressed state.
 * If the callback returns true,
 * the vanilla handling (block breaking, entity attacking, hand swing) will be cancelled,
 * and the later callbacks of this event are also cancelled.
 * </p>
 * <p>
 * This event is client-only, which means handling it may require sending custom packets.
 * </p>
 * <p>
 * To check whether the attack key is just pressed, use
 * <code>
 * MinecraftClient.getInstance().options.attackKey.wasPressed()
 * </code>
 * inside the callback.
 * </p>
 * <p>
 * The vanilla attack cooldown does not affect this event.
 * {@link net.minecraft.entity.player.ItemCooldownManager} can be used for custom item cooldown handling.
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
	 * @return whether to intercept attack handling
	 */
	boolean onClientPlayerPreAttack(ClientPlayerEntity player);
}
