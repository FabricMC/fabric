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

package net.fabricmc.fabric.mixin.content.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

import net.fabricmc.fabric.api.registry.SpreadableBlockRegistry;
import net.fabricmc.fabric.impl.content.registry.SpreadableBlockRegistryImpl;

@Mixin(SpreadableBlock.class)
public class SpreadableBlockMixin extends SnowyBlock {
	private SpreadableBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow
	private static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
		return false;
	}

	@Inject(
			method = "randomTick",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;",
					shift = At.Shift.BY, by = 2
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void doFabricBlockSpread(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci, BlockState thisState, int i, BlockPos targetPos) {
		SpreadableBlockRegistry spreadableRegistry = SpreadableBlockRegistryImpl.getInstanceBySpreadable(state);

		if (spreadableRegistry != null) {
			BlockState newState = spreadableRegistry.get(world.getBlockState(targetPos));

			if (newState != null && canSpread(newState, world, targetPos)) {
				if (newState.contains(SNOWY)) {
					newState = newState.with(SNOWY, world.getBlockState(targetPos.up()).isOf(Blocks.SNOW));
				}

				world.setBlockState(targetPos, newState);
			}
		}
	}
}
