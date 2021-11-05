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

package net.fabricmc.fabric.mixin.mininglevel;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.mininglevel.v1.FabricTool;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;

/**
 * Adds support for {@link FabricMineableTags#SHEARS_MINEABLE}.
 */
@Mixin(ShearsItem.class)
abstract class ShearsItemMixin implements FabricTool {
	@Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
	private void fabric$onIsSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (state.isIn(getEffectiveBlocks())) {
			int miningLevel = getMiningLevel();

			if (miningLevel >= MiningLevelManager.getRequiredMiningLevel(state)) {
				info.setReturnValue(true);
			}
		}
	}

	@Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
	private void fabric$onGetMiningSpeedMultiplier(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> info) {
		if (info.getReturnValueF() == 1.0f) { // if not caught by vanilla checks
			if (state.isIn(getEffectiveBlocks())) { // mimics MiningToolItem.getMiningSpeedMultiplier
				// In vanilla 1.17, shears have three special mining speed multiplier values:
				if (state.isIn(FabricMineableTags.SHEARS_MINEABLE_FAST)) {
					info.setReturnValue(15.0f); // - cobweb and leaves return 15.0
				} else if (state.isIn(FabricMineableTags.SHEARS_MINEABLE_SLOW)) {
					info.setReturnValue(2.0f); // - vines and glow lichen return 2.0
				} else {
					info.setReturnValue(5.0f); // - wool returns 5.0. As the most "neutral" option out of these three, we'll use it by default.
				}
			}
		}
	}

	@Override
	public ToolMaterial getToolMaterial() {
		return ToolMaterials.IRON; // Vanilla shears are made of iron
	}

	@Override
	public Tag<Block> getEffectiveBlocks() {
		return FabricMineableTags.SHEARS_MINEABLE;
	}
}
