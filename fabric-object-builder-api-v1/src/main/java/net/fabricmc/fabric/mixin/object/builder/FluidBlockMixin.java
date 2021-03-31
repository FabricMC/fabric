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

package net.fabricmc.fabric.mixin.object.builder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.fluid.FluidFlowEvents;

@Mixin(FluidBlock.class)
public class FluidBlockMixin extends Block {
	public FluidBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
	private void receiveNeighborFluids(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
		for (Direction direction : Direction.values()) {
			FluidFlowEvents.FluidFlowInteractionEvent event = FluidFlowEvents.getEvent(this, world.getBlockState(pos.offset(direction)).getBlock(), direction);

			if (event != null) {
				boolean returnValue = event.onFlow(state, world.getBlockState(pos.offset(direction)), pos, world);

				if (!returnValue) {
					cir.setReturnValue(false);
					return;
				}
			}
		}
	}
}
