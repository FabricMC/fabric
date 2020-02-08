package net.fabricmc.api.event.v1.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;

/**
 * Represents a callback for when a {@link ServerPlayerEntity} is respawned.
 *
 * <p>This occurs when a player dies, or returns to the overworld from the end.</p>
 */
public interface PlayerRespawnCallback {
	Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class, (callbacks) -> (newPlayer, oldPlayer, newDimension, died) -> {
		for (PlayerRespawnCallback callback : callbacks) {
			callback.onRespawn(newPlayer, oldPlayer, newDimension, died);
		}
	});

	/**
	 * Called when a player respawns.
	 *
	 * @param newPlayer The new {@link ServerPlayerEntity} that will be spawned.
	 * @param oldPlayer The old {@link ServerPlayerEntity} that is being removed.
	 * @param dimension The dimension this player is being respawned in.
	 * @param alive Whether this player being respawned is still alive.
	 */
	void onRespawn(ServerPlayerEntity newPlayer, ServerPlayerEntity oldPlayer, DimensionType dimension, boolean alive);
}
