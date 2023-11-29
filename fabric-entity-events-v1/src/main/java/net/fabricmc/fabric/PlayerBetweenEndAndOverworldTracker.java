package net.fabricmc.fabric;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerBetweenEndAndOverworldTracker {

	private static final Map<UUID, Entry> playersBetweenEndAndOverworld = new HashMap<>();

	public static void setPlayerIsBetweenEndAndOverworld(UUID playerUUID, ServerWorld origin, ServerWorld destination) {
		playersBetweenEndAndOverworld.put(playerUUID, new Entry(origin, destination));
	}

	public static boolean isPlayerBetweenEndAndOverworld(ServerPlayerEntity player) {
		return playersBetweenEndAndOverworld.containsKey(player.getUuid());
	}

	public static Entry setPlayerIsNotBetweenEndAndOverworld(UUID playerUUID) {
		return playersBetweenEndAndOverworld.remove(playerUUID);
	}

	public record Entry(ServerWorld origin, ServerWorld destination) {}

}
