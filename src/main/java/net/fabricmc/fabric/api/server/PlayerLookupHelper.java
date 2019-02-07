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

package net.fabricmc.fabric.api.server;

import net.fabricmc.fabric.impl.server.PlayerLookupHelperImpl;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;

import java.util.stream.Stream;

public interface PlayerLookupHelper {
	static PlayerLookupHelper get() {
		return get(FabricLoader.INSTANCE.getEnvironmentHandler().getServerInstance());
	}

	static PlayerLookupHelper get(MinecraftServer server) {
		return PlayerLookupHelperImpl.get(server);
	}

	Stream<PlayerEntity> players();
	Stream<PlayerEntity> playersWatching(World world, ChunkPos pos);
	Stream<PlayerEntity> playersWatching(World world, Entity entity);
	Stream<PlayerEntity> playersAround(World world, Vec3d vector, double radius);
	Stream<PlayerEntity> playersInWorld(World world);

	default Stream<PlayerEntity> playersExcept(PlayerEntity player) {
		return players().filter((p) -> p != player);
	}

	default Stream<PlayerEntity> playersWatching(World world, BlockPos pos) {
		return playersWatching(world, new ChunkPos(pos));
	}

	default Stream<PlayerEntity> playersAround(World world, BlockPos pos, double radius) {
		return playersAround(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), radius);
	}
}
