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

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;

import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;

/**
 * Adds support for {@link FabricMineableTags#SHEARS_MINEABLE}.
 */
@Mixin(ShearsItem.class)
abstract class ShearsItemMixin {
	@Inject(method = "isSuitableFor", at = @At("HEAD"), cancellable = true)
	private void fabric$onIsSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (state.isIn(FabricMineableTags.SHEARS_MINEABLE)) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
	private void fabric$onGetMiningSpeedMultiplier(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> info) {
		if (info.getReturnValueF() == 1.0f) { // if not caught by vanilla checks
			if (state.isIn(FabricMineableTags.SHEARS_MINEABLE)) { // mimics MiningToolItem.getMiningSpeedMultiplier
				// In vanilla 1.17, shears have three special mining speed multiplier values:
				//   - cobweb and leaves return 15.0
				//   - wool returns 5.0
				//   - vines and glow lichen return 2.0
				// As the most "neutral" option out of these three,
				// we'll use 5.0 as it's not extremely fast nor extremely slow.
				info.setReturnValue(5.0f);
			}
		}
	}
}
