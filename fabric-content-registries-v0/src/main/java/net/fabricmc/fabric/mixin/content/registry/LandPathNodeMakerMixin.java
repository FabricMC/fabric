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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;

// Applied a bit earlier than other mods to ensure changes and optimizations to default vanilla behavior
@Mixin(value = LandPathNodeMaker.class, priority = 999)
public class LandPathNodeMakerMixin {
	/**
	 * Overrides the node type for the specified position, if the position is a direct target in a path.
	 */
	@Inject(method = "getCommonNodeType", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void getCommonNodeType(BlockView world, BlockPos pos, CallbackInfoReturnable<PathNodeType> cir, BlockState state) {
		PathNodeType nodeType = LandPathNodeTypesRegistry.getPathNodeType(state, world, pos, false);

		if (nodeType != null) {
			cir.setReturnValue(nodeType);
		}
	}

	/**
	 * Overrides the node type for the specified position, if the position is found as neighbor block in a path.
	 */
	@Inject(method = "getNodeTypeFromNeighbors", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void getNodeTypeFromNeighbors(BlockView world, BlockPos.Mutable pos, PathNodeType nodeType, CallbackInfoReturnable<PathNodeType> cir, int i, int j, int k, int l, int m, int n, BlockState state) {
		PathNodeType neighborNodeType = LandPathNodeTypesRegistry.getPathNodeType(state, world, pos, true);

		if (neighborNodeType != null) {
			cir.setReturnValue(neighborNodeType);
		}
	}
}
