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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.MiningToolItem;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.mininglevel.v1.FabricTool;

@Mixin(MiningToolItem.class)
abstract class MiningToolItemMixin implements FabricTool {
	@Shadow
	@Final
	private Tag<Block> effectiveBlocks;

	@Inject(method = "isSuitableFor", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ToolMaterial;getMiningLevel()I"), cancellable = true)
	private void fabric$onIsSuitableFor(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if (!isSuitableFor(getMiningLevel(), state)) {
			info.setReturnValue(false);
		}
	}

	@Override
	public Tag<Block> getEffectiveBlocks() {
		return effectiveBlocks;
	}
}
