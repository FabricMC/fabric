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

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import net.fabricmc.fabric.api.registry.LandPathNodeTypesRegistry;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMakerMixin {
	/**
	 * Gets the node type for the specified position.
	 */
	@Inject(method = "getCommonNodeType", at = @At("HEAD"), cancellable = true)
	private static void getCommonNodeType(@NotNull BlockView world, BlockPos pos, @NotNull CallbackInfoReturnable<PathNodeType> cir) {
		PathNodeType nodeType = LandPathNodeTypesRegistry.getPathNodeType(world, pos);

		if (nodeType != null) {
			cir.setReturnValue(nodeType);
		}
	}
}
