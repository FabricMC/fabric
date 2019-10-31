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

package net.fabricmc.fabric.mixin.tools;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

import net.fabricmc.fabric.api.tools.v1.ActableAttributeHolder;
import net.fabricmc.fabric.api.tools.v1.ToolActor;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tools.ToolManager;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory {
	private final ToolActor<PlayerEntity> actor = ToolActor.of(this.player);

	@Shadow
	@Final
	public DefaultedList<ItemStack> main;

	@Shadow
	public int selectedSlot;

	@Shadow
	@Final
	public PlayerEntity player;

	@Shadow
	public abstract ItemStack getInvStack(int int_1);

	@Inject(method = "isUsingEffectiveTool", at = @At("HEAD"))
	public void actMiningLevel(BlockState state, CallbackInfoReturnable<Boolean> info) {
		ItemStack stack = this.getInvStack(this.selectedSlot);

		if (stack.getItem() instanceof ActableAttributeHolder) {
			TriState ret = ToolManager.handleIsEffectiveOn(stack, state, actor);

			if (ret != TriState.DEFAULT) {
				info.setReturnValue(ret.get());
			}
		}
	}

	@Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"))
	public void actMiningSleed(BlockState state, CallbackInfoReturnable<Float> info) {
		ItemStack stack = this.main.get(this.selectedSlot);

		if (stack.getItem() instanceof ActableAttributeHolder) {
			info.setReturnValue(((ActableAttributeHolder) stack.getItem()).getMiningSpeed(stack, actor));
		}
	}
}
