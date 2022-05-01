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

package net.fabricmc.fabric.api.server;

import java.util.stream.Stream;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

/**
 * Helper streams for looking up players on a server.
 *
 * <p>In general, most of these methods will only function with a {@link ServerWorld} instance.
 *
 * @deprecated Please use {@link PlayerLookup} instead.
 */
@Deprecated
public final class PlayerStream {
	private PlayerStream() { }

	public static Stream<ServerPlayerEntity> all(MinecraftServer server) {
		if (server.getPlayerManager() != null) {
			return server.getPlayerManager().getPlayerList().stream();
		} else {
			return Stream.empty();
		}
	}

	public static Stream<PlayerEntity> world(World world) {
		if (world instanceof ServerWorld) {
			// noinspection unchecked,rawtypes
			return ((Stream) ((ServerWorld) world).getPlayers().stream());
		} else {
			throw new RuntimeException("Only supported on ServerWorld!");
		}
	}

	public static Stream<PlayerEntity> watching(World world, ChunkPos pos) {
		if (world instanceof ServerWorld) {
			//noinspection unchecked,rawtypes
			return (Stream) PlayerLookup.tracking((ServerWorld) world, pos).stream();
		}

		throw new RuntimeException("Only supported on ServerWorld!");
	}

	/**
	 * Warning: If the provided entity is a PlayerEntity themselves, it is not
	 * guaranteed by the contract that said PlayerEntity is included in the
	 * resulting stream.
	 */
	@SuppressWarnings("JavaDoc")
	public static Stream<PlayerEntity> watching(Entity entity) {
		//noinspection unchecked,rawtypes
		return (Stream) PlayerLookup.tracking(entity).stream();
	}

	public static Stream<PlayerEntity> watching(BlockEntity entity) {
		return watching(entity.getWorld(), entity.getPos());
	}

	public static Stream<PlayerEntity> watching(World world, BlockPos pos) {
		return watching(world, new ChunkPos(pos));
	}

	public static Stream<PlayerEntity> around(World world, Vec3d vector, double radius) {
		double radiusSq = radius * radius;
		return world(world).filter((p) -> p.squaredDistanceTo(vector) <= radiusSq);
	}

	public static Stream<PlayerEntity> around(World world, BlockPos pos, double radius) {
		double radiusSq = radius * radius;
		return world(world).filter((p) -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= radiusSq);
	}
}
