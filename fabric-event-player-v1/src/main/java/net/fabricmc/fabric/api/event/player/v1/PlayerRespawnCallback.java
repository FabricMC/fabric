package net.fabricmc.fabric.api.event.player.v1;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Represents a callback for when a {@link ServerPlayerEntity} is respawned.
 *
 * <p>This occurs when a player dies, or changes dimensions, such as returning to the overworld from the end.
 *
 * @apiNote This callback may be used to reposition the respawning player, or copy modded data between the old and the new.
 */
public interface PlayerRespawnCallback {
	Event<PlayerRespawnCallback> EVENT = EventFactory.createArrayBacked(PlayerRespawnCallback.class, (callbacks) -> (newPlayer, oldPlayer, newDimension, alive) -> {
		for (PlayerRespawnCallback callback : callbacks) {
			callback.onRespawn(newPlayer, oldPlayer, newDimension, alive);
		}
	});

	/**
	 * Called when a player respawns.
	 *
	 * <p>Note the player can be repositioned.
	 *
	 * <p>When this method is called, {@code newPlayer} will be at its spawn position.
	 *
	 * <p>The player may be repositioned by callbacks to change where it respawns.
	 * The {@code oldPlayer}'s coordinates and world will be the same as before the respawn,
	 * By default player's {@linkplain Entity#dimension dimension} will have been set to {@link DimensionType#OVERWORLD}.
	 *
	 * <p>Player's data has been copied from the {@code oldPlayer} to {@code newPlayer}.
	 * The actual copied data depends on various factors such as the value of {@code isAlive} or of the {@link GameRules#KEEP_INVENTORY keepInventory} GameRule.
	 *
	 * @param newPlayer The new {@link ServerPlayerEntity} that will be spawned.
	 * @param oldPlayer The old {@link ServerPlayerEntity} that is being removed.
	 * @param dimension The dimension this player is being respawned in.
	 * @param isAlive Whether the old player is still alive.
	 */
	void onRespawn(ServerPlayerEntity newPlayer, ServerPlayerEntity oldPlayer, DimensionType dimension, boolean isAlive);
}
