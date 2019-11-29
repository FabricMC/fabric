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

package net.fabricmc.fabric.api.block.entity;

import com.google.common.base.Preconditions;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/**
 * Implement this interface on a BlockEntity which you would like to be
 * synchronized with the client side using the built-in engine methods.
 */
public interface BlockEntityClientSerializable {
	void fromClientTag(CompoundTag tag);

	CompoundTag toClientTag(CompoundTag tag);

	/**
	 * When called on the server, schedules a BlockEntity sync to client.
	 * This will cause {@link #toClientTag(CompoundTag)} to be called on the
	 * server to generate the packet data, and then
	 * {@link #fromClientTag(CompoundTag)} on the client to decode that data.
	 *
	 * <p>This is preferable to
	 * {@link World#updateListeners(net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, net.minecraft.block.BlockState, int)}
	 * because it does not cause entities to update their pathing as a side effect.
	 */
	default void sync() {
		World world = ((BlockEntity) this).getWorld();
		Preconditions.checkNotNull(world); //Maintain distinct failure case from below
		if (!(world instanceof ServerWorld)) throw new IllegalStateException("Cannot call sync() on the logical client! Did you check world.isClient first?");

		((ServerWorld) world).getChunkManager().markForUpdate(((BlockEntity) this).getPos());
	}
}
