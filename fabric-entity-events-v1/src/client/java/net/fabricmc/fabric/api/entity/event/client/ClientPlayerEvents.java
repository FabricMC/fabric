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
	/*
	 * Flag for using default slowdown during using an item.
	 */
	public static final float USING_DEFAULT_SLOWDOWN_SPEED = -1.0F;

	/*
	 * Default slowdown speed when using an item in Minecraft.
	 */
	public static final float DEFAULT_SLOWDOWN_SPEED = 0.2F;

	/**
	 * An event that is called when a player is moving during using an item.
	 */
	public static final Event<AdjustUsingItemSpeed> ADJUST_USING_ITEM_SPEED = EventFactory.createArrayBacked(AdjustUsingItemSpeed.class, callbacks -> player -> {
		float maxSpeed = -0.1F;

		for (AdjustUsingItemSpeed callback : callbacks) {
			float currentSpeed = callback.adjustUsingItemSpeed(player);

			if (currentSpeed >= 0.0F && currentSpeed <= 1.0F) {
				maxSpeed = currentSpeed > maxSpeed ? currentSpeed : maxSpeed;
			}
		}

		return maxSpeed == -0.1F ? DEFAULT_SLOWDOWN_SPEED : maxSpeed;
	});

	@FunctionalInterface
	public interface AdjustUsingItemSpeed {
		/**
		 * Called when a player is moving during using an item.
		 *
		 * @param player the player is moving during using an item.
		 * @return the percentage of the player's speed from 0.0F to 1.0F.
		 * {@link #DEFAULT_SLOWDOWN_SPEED} indicates that no adjustment should be applied.
		 */
		float adjustUsingItemSpeed(ClientPlayerEntity player);
	}
}
