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

package net.fabricmc.fabric.api.networking.player.tracking.v1;

import java.util.Collection;
import java.util.stream.Stream;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.ChunkManager;

import net.fabricmc.fabric.impl.networking.player.tracking.EntityTrackerStorageAccessor;

/**
 * Helper streams for looking up players for receiving packets.
 *
 * <p>The word "watching" means that an entity/chunk on the server is known to a player's client (in view distance, etc).
 *
 * <p>These methods should be called on the server thread and would only work on logical servers.
 *
 * <p>For methods returning {@link Collection}s, the returned collections should not be modified in any way.
 */
public final class PlayerStream {
	private PlayerStream() {
	}

	/**
	 * Gets all the players on the minecraft server.
	 *
	 * <p>The returned collection should not be modified by the user.
	 *
	 * @param server the server
	 * @return all players on the server
	 */
	public static Collection<ServerPlayerEntity> all(MinecraftServer server) {
		return server.getPlayerManager().getPlayerList();
	}

	/**
	 * Gets all the players in a server world.
	 *
	 * <p>The returned collection should not be modified by the user.
	 *
	 * @param world the server world
	 * @return the players in the server world
	 * @throws IllegalArgumentException if the world is not a server world
	 */
	public static Collection<ServerPlayerEntity> world(ServerWorld world) {
		return world.getPlayers();
	}

	/**
	 * Gets all players watching a chunk in a server world.
	 *
	 * @param world the server world
	 * @param pos   the chunk in question
	 * @return the players watching the chunk
	 * @throws IllegalArgumentException if the world is not a server world
	 */
	public static Stream<ServerPlayerEntity> watching(ServerWorld world, ChunkPos pos) {
		return world.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(pos, false);
	}

	/**
	 * Gets all players watching an entity in a server world.
	 *
	 * <p>The returned collection should not be modified by the user.
	 *
	 * <p><b>Warning</b>: If the provided entity is a player, it is not
	 * guaranteed by the contract that said player is included in the
	 * resulting stream.
	 *
	 * @param entity the watched entity
	 * @return the players watching the entity
	 * @throws IllegalArgumentException if the entity is not in a server world
	 */
	public static Collection<ServerPlayerEntity> watching(Entity entity) {
		ChunkManager manager = entity.world.getChunkManager();

		if (manager instanceof ServerChunkManager) {
			ThreadedAnvilChunkStorage storage = ((ServerChunkManager) manager).threadedAnvilChunkStorage;

			return ((EntityTrackerStorageAccessor) storage).fabric_getTrackingPlayers(entity);
		}

		throw new IllegalArgumentException("Only supported on server worlds!");
	}

	/**
	 * Gets all players watching a block entity in a server world.
	 *
	 * @param entity the block entity
	 * @return the players watching the block position
	 * @throws IllegalArgumentException if the block entity is not in a server world
	 */
	public static Stream<ServerPlayerEntity> watching(BlockEntity entity) {
		if (!entity.hasWorld() || entity.getWorld().isClient) {
			throw new IllegalArgumentException("Only supported on server worlds!");
		}

		return watching((ServerWorld) entity.getWorld(), entity.getPos());
	}

	/**
	 * Gets all players watching a block position in a server world.
	 *
	 * @param world the server world
	 * @param pos   the block position
	 * @return the players watching the block position
	 * @throws IllegalArgumentException if the world is not a server world
	 */
	public static Stream<ServerPlayerEntity> watching(ServerWorld world, BlockPos pos) {
		return watching(world, new ChunkPos(pos));
	}

	/**
	 * Gets all players around a position in a world.
	 *
	 * <p>The distance check is done in the three-dimensional space instead of in the horizontal plane.
	 *
	 * @param world  the world
	 * @param pos    the position
	 * @param radius the maximum distance from the position
	 * @return the players around the position
	 * @throws IllegalArgumentException if the world is not a server world
	 */
	public static Stream<ServerPlayerEntity> around(ServerWorld world, Vec3d pos, double radius) {
		double radiusSq = radius * radius;
		return world(world).stream().filter((p) -> p.squaredDistanceTo(pos) <= radiusSq);
	}

	/**
	 * Gets all players around a position in a world.
	 *
	 * <p>The distance check is done in the three-dimensional space instead of in the horizontal plane.
	 *
	 * @param world  the world
	 * @param pos    the position (can be a block pos)
	 * @param radius the maximum distance from the position
	 * @return the players around the position
	 * @throws IllegalArgumentException if the world is not a server world
	 */
	public static Stream<ServerPlayerEntity> around(ServerWorld world, Vec3i pos, double radius) {
		double radiusSq = radius * radius;
		return world(world).stream().filter((p) -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= radiusSq);
	}
}
