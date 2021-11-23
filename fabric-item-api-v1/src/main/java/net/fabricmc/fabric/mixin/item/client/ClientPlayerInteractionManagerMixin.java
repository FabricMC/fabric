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

package net.fabricmc.fabric.mixin.item.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.item.v1.FabricItem;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
	@Shadow
	@Final
	private MinecraftClient client;
	@Shadow
	private BlockPos currentBreakingPos;
	@Shadow
	private ItemStack selectedStack;

	/**
	 * Allows a FabricItem to continue block breaking progress even if the count or nbt changed.
	 * For this, we inject after vanilla decided that the stack was "not unchanged", and we set if back to "unchanged"
	 * if the item wishes to continue mining.
	 */
	@ModifyVariable(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/util/math/BlockPos.equals(Ljava/lang/Object;)Z"
			),
			method = "isCurrentlyBreaking",
			index = 3
	)
	private boolean fabricItemContinueBlockBreakingInject(boolean stackUnchanged) {
		if (!stackUnchanged) {
			// The stack changed and vanilla is about to cancel block breaking progress. Check if the item wants to continue block breaking instead.
			ItemStack oldStack = this.selectedStack;
			ItemStack newStack = this.client.player.getMainHandStack();

			if (oldStack.isOf(newStack.getItem()) && ((FabricItem) oldStack.getItem()).allowContinuingBlockBreaking(this.client.player, oldStack, newStack)) {
				stackUnchanged = true;
			}
		}

		return stackUnchanged;
	}
}
