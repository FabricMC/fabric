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

package net.fabricmc.fabric.mixin.tool.attribute;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.fabricmc.fabric.impl.tool.attribute.DynamicToolContext;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {
	@Shadow
	@Final
	public PlayerEntity player;

	@Inject(at = @At("HEAD"), method = "getBlockBreakingSpeed")
	public void getBlockBreakingSpeedSetContext(BlockState block, CallbackInfoReturnable<Float> cir) {
		DynamicToolContext.set(player);
	}

	@Inject(at = @At("RETURN"), method = "getBlockBreakingSpeed")
	public void getBlockBreakingSpeedClearContext(BlockState block, CallbackInfoReturnable<Float> cir) {
		DynamicToolContext.clear();
	}
}
