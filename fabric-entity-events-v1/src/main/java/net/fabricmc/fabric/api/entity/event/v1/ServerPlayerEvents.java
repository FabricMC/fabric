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

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerPlayerEvents {
	/**
	 * An event that is called when the data from an old player is copied to a new player.
	 *
	 * <p>This event is typically called before a player is completely respawned.
	 * Mods may use this event to copy old player data to a new player.
	 */
	public static final Event<ServerPlayerEvents.CopyFrom> COPY_FROM = EventFactory.createArrayBacked(ServerPlayerEvents.CopyFrom.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
		for (CopyFrom callback : callbacks) {
			callback.copyFromPlayer(oldPlayer, newPlayer, alive);
		}
	});

	/**
	 * An event that is called after a player has been respawned.
	 *
	 * <p>Mods may use this event for reference clean up on the old player.
	 */
	public static final Event<ServerPlayerEvents.AfterRespawn> AFTER_RESPAWN = EventFactory.createArrayBacked(ServerPlayerEvents.AfterRespawn.class, callbacks -> (oldPlayer, newPlayer, alive) -> {
		for (AfterRespawn callback : callbacks) {
			callback.afterRespawn(oldPlayer, newPlayer, alive);
		}
	});

	/**
	 * An event that is called when a player takes fatal damage.
	 *
	 * <p>Mods can cancel this to keep the player alive.
	 */
	public static final Event<BeforeDeath> BEFORE_DEATH = EventFactory.createArrayBacked(BeforeDeath.class, callbacks -> (oldPlayer, damageSource, amount) -> {
		boolean result = true;
		for (BeforeDeath callback : callbacks) {
			result &= callback.beforeDeath(oldPlayer, damageSource, amount);
		}
		return result;
	});


	@FunctionalInterface
	public interface CopyFrom {
		/**
		 * Called when player data is copied to a new player.
		 *
		 * @param oldPlayer the old player
		 * @param newPlayer the new player
		 * @param alive whether the old player is still alive
		 */
		void copyFromPlayer(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	@FunctionalInterface
	public interface AfterRespawn {
		/**
		 * Called after player a has been respawned.
		 *
		 * @param oldPlayer the old player
		 * @param newPlayer the new player
		 * @param alive whether the old player is still alive
		 */
		void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive);
	}

	@FunctionalInterface
	public interface BeforeDeath {
		/**
		 * Called when a player takes fatal damage.
		 *
		 * @param player the player
		 * @param source the fatal damage source
		 * @param amount the amount of damage that has killed the player
		 * @return whether or not the player should die
		 */
		boolean beforeDeath(ServerPlayerEntity player, DamageSource source, float amount);
	}

	private ServerPlayerEvents() {
	}
}
