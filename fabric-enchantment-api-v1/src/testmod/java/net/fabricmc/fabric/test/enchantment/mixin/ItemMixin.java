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

package net.fabricmc.fabric.test.enchantment.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(Item.class)
public class ItemMixin {
	@Inject(method = "isEnchantable", at = @At("RETURN"), cancellable = true)
	public void isEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		if (((Item) (Object) this) == Items.DIRT) {
			callback.setReturnValue(stack.getCount() == 1);
		}
	}

	@Inject(method = "getEnchantability", at = @At("RETURN"), cancellable = true)
	public void getEnchantability(CallbackInfoReturnable<Integer> callback) {
		if (((Item) (Object) this) == Items.DIRT) {
			callback.setReturnValue(15);
		}
	}
}
