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

package net.fabricmc.fabric.api.transfer.v1.fluid;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.fluid.CauldronWrapper;

public final class FluidStorage {
	public static final BlockApiLookup<Storage<Fluid>, Direction> SIDED =
			BlockApiLookup.get(new Identifier("fabric:sided_fluid_storage"), Storage.asClass(), Direction.class);

	private FluidStorage() {
	}

	static {
		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, context) -> CauldronWrapper.get(world, pos), Blocks.CAULDRON);
	}
}
