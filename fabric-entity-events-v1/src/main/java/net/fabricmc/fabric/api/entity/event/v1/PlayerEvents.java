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

package net.fabricmc.fabric.api.entity.event.v1;

import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class PlayerEvents {
	/**
	 * An event that is called right before the player is ticked.
	 *
	 * @see PlayerEntity#tick()
	 */
	public static final Event<BeforePlayerTick> BEFORE_PLAYER_TICK = EventFactory.createArrayBacked(BeforePlayerTick.class, callbacks -> player -> {
		for (BeforePlayerTick callback : callbacks) {
			callback.prePlayerTick(player);
		}
	});

	/**
	 * An event that is called at the end of the player tick.
	 *
	 * @see PlayerEntity#tick()
	 */
	public static final Event<AfterPlayerTick> AFTER_PLAYER_TICK = EventFactory.createArrayBacked(AfterPlayerTick.class, callbacks -> player -> {
		for (AfterPlayerTick callback : callbacks) {
			callback.postPlayerTick(player);
		}
	});

	@FunctionalInterface
	public interface BeforePlayerTick {
		/**
		 * Called before the player tick.
		 *
		 * @param player
		 */
		void prePlayerTick(PlayerEntity player);
	}

	@FunctionalInterface
	public interface AfterPlayerTick {
		/**
		 * Called after the player tick.
		 *
		 * @param player
		 */
		void postPlayerTick(PlayerEntity player);
	}
}
