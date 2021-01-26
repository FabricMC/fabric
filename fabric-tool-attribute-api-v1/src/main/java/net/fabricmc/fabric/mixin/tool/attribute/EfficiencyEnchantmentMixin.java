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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

@Mixin(EfficiencyEnchantment.class)
public class EfficiencyEnchantmentMixin {
	@Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
	private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem().isIn(FabricToolTags.SHEARS)) {
			cir.setReturnValue(true);
		}
	}
}
