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

package net.fabricmc.fabric.api.object.builder.v1.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MinecartComparatorLogic<T extends AbstractMinecartEntity> {
	/**
	 * Compute the comparator output of a detector rail when a minecart is resting
	 * on top of it. Called from {@link net.minecraft.block.DetectorRailBlock#getComparatorOutput}.
	 * @param minecart The minecart on the rail
	 * @param state BlockState of the rail
	 * @param pos Position of the rail
	 * @return A redstone power value >= 0 to use, else a value < 0 to run vanilla's logic
	 */
	int getComparatorValue(T minecart, BlockState state, World world, BlockPos pos);
}
