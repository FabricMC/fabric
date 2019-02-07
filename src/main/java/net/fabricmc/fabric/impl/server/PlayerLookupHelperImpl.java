/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.server;

import net.fabricmc.fabric.api.server.PlayerLookupHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;

import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public class PlayerLookupHelperImpl implements PlayerLookupHelper {
	private static final WeakHashMap<MinecraftServer, PlayerLookupHelper> HELPER_MAP = new WeakHashMap<>();
	private final MinecraftServer server;

	private PlayerLookupHelperImpl(MinecraftServer server) {
		this.server = server;
	}

	// TODO: cache
	public static PlayerLookupHelper get(MinecraftServer server) {
		return HELPER_MAP.computeIfAbsent(server, PlayerLookupHelperImpl::new);
	}

	@Override
	public Stream<PlayerEntity> players() {
		//noinspection unchecked
		return ((List<PlayerEntity>) (List) server.getPlayerManager().getPlayerList()).stream();
	}

	@Override
	public Stream<PlayerEntity> playersWatching(World world, ChunkPos pos) {
		//noinspection unchecked
		return ((Stream<PlayerEntity>) (Stream) ((ServerWorld) world).getChunkManager().getPlayersWatchingChunk(pos, false, false));
	}

	@Override
	public Stream<PlayerEntity> playersWatching(World world, Entity entity) {
		EntityTracker tracker = ((ServerWorld) world).getEntityTracker();
		if (tracker instanceof EntityTrackerStreamAccessor) {
			//noinspection unchecked
			return ((Stream<PlayerEntity>) (Stream) ((EntityTrackerStreamAccessor) tracker).fabric_getTrackingPlayers(entity));
		} else {
			// fallback
			return playersWatching(world, new ChunkPos((int) (entity.x / 16.0D), (int) (entity.z / 16.0D)));
		}
	}

	@Override
	public Stream<PlayerEntity> playersAround(World world, Vec3d vector, double radius) {
		double radiusSq = radius * radius;
		return playersInWorld(world).filter((p) -> p.squaredDistanceTo(vector) <= radiusSq);
	}

	@Override
	public Stream<PlayerEntity> playersInWorld(World world) {
		return ((ServerWorld) world).players.stream();
	}
}
