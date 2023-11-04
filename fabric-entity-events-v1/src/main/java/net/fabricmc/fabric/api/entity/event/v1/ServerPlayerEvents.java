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

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerPlayerEvents {
	/**
	 * An event that is called before a player is spawned when opening a world or joining a server.
	 *
	 * <p>Mods may use this event to set the player's world and position before it is completely spawned.
	 *
	 * <p>Note: this event is not called when a player is respawned.
	 * For that purpose, {@link ServerPlayerEvents#COPY_FROM} should be used.
	 */
	public static final Event<BeforeSpawn> BEFORE_SPAWN = EventFactory.createArrayBacked(BeforeSpawn.class, callbacks -> (player) -> {
		for (BeforeSpawn callback : callbacks) {
			callback.beforeSpawn(player);
		}
	});

	/**
	 * An event that is called when the data from an old player is copied to a new player.
	 *
	 * <p>This event is typically called before a player is completely respawned.
	 * Mods may use this event to copy old player data to a new player.
	 *
	 * <p>Notes:
	 * <ul>
	 * <li>Unlike event {@link ServerPlayerEvents#BEFORE_SPAWN}, changing the player's world might cause undefined behaviour and crash the game.</li>
	 * <li>Changing the player's position may have undesired effects because respawn side effects (such as the respawn anchor losing a charge) will happen anyway.</li>
	 * </ul>
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
	 * @deprecated Use the more general {@link ServerLivingEntityEvents#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
	 */
	@Deprecated
	public static final Event<AllowDeath> ALLOW_DEATH = EventFactory.createArrayBacked(AllowDeath.class, callbacks -> (player, damageSource, damageAmount) -> {
		for (AllowDeath callback : callbacks) {
			if (!callback.allowDeath(player, damageSource, damageAmount)) {
				return false;
			}
		}

		return true;
	});

	@FunctionalInterface
	public interface BeforeSpawn {
		/**
		 * Called before a player is spawned when opening a world or joining a server.
		 *
		 * <p>Note: this event is not called when a player is respawned.
		 * For that purpose, {@link ServerPlayerEvents#COPY_FROM} should be used.
		 *
		 * @param player the player
		 */
		void beforeSpawn(ServerPlayerEntity player);
	}

	@FunctionalInterface
	public interface CopyFrom {
		/**
		 * Called when player data is copied to a new player.
		 *
		 * <p>Notes:
		 * <ul>
		 * <li>Unlike event {@link ServerPlayerEvents#BEFORE_SPAWN}, changing the player's world might cause undefined behaviour and crash the game.</li>
		 * <li>Changing the player's position may have undesired effects because respawn side effects (such as the respawn anchor losing a charge) will happen anyway.</li>
		 * </ul>
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

	/**
	 * @deprecated Use the more general {@link ServerLivingEntityEvents#ALLOW_DEATH} event instead and check for {@code instanceof ServerPlayerEntity}.
	 */
	@Deprecated
	@FunctionalInterface
	public interface AllowDeath {
		/**
		 * Called when a player takes fatal damage (before totems of undying can take effect).
		 *
		 * @param player the player
		 * @param damageSource the fatal damage damageSource
		 * @param damageAmount the damageAmount of damage that has killed the player
		 * @return true if the death should go ahead, false otherwise.
		 */
		boolean allowDeath(ServerPlayerEntity player, DamageSource damageSource, float damageAmount);
	}

	private ServerPlayerEvents() {
	}

	static {
		// Forward general living entity event to (older) player-specific event.
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (entity instanceof ServerPlayerEntity player) {
				return ServerPlayerEvents.ALLOW_DEATH.invoker().allowDeath(player, damageSource, damageAmount);
			}

			return true;
		});
	}
}
