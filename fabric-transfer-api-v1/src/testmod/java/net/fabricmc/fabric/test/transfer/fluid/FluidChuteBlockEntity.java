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

package net.fabricmc.fabric.test.transfer.fluid;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidKey;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidTransfer;
import net.fabricmc.fabric.api.transfer.v1.storage.Movement;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class FluidChuteBlockEntity extends BlockEntity implements Tickable {
	private int tickCounter = 0;

	public FluidChuteBlockEntity() {
		super(FluidTransferTest.FLUID_CHUTE_TYPE);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void tick() {
		if (!world.isClient() && tickCounter++ % 20 == 0) {
			Storage<FluidKey> top = FluidTransfer.SIDED.find(world, pos.offset(Direction.UP), Direction.DOWN);
			Storage<FluidKey> bottom = FluidTransfer.SIDED.find(world, pos.offset(Direction.DOWN), Direction.UP);

			if (top != null && bottom != null) {
				Movement.move(top, bottom, fluid -> true, FluidConstants.BUCKET, null);
			}
		}
	}
}
