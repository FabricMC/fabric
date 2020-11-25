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

package net.fabricmc.fabric.mixin.enchantment;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.AnvilScreenHandler;

/**
 * Ensures that stackable items that are enchanted using an anvil can
 * stack with item enchanted using an enchantment table
 *
 * <p>This mixin is disabled in fabric api because of uncertainty about it's
 * necessity and implications for vanilla compatibility. However it is included
 * to illustrate how to fix the issue if desired.</p>
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * <p>Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * to newer versions of the game.</p>
 */
@Mixin(AnvilScreenHandler.class)
public class DisabledAnvilScreenHandlerMixin {
	@Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void updateResult(CallbackInfo callback, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map map, int w) {
		// If the item is damagable and the max count isn't one (ie it's a normal stackable item)
		// And if k != 1 (k is only equal to 1 when there's a name change in the anvil)
		if (!itemStack2.getItem().isDamageable() && itemStack2.getItem().getMaxCount() != 1 && k != 1) {
			CompoundTag tag = itemStack2.getTag();

			if (tag != null) {
				int oldRepairCost = (tag.getInt("RepairCost"));

				if (oldRepairCost != 0) {
					oldRepairCost = oldRepairCost - 1;

					if (oldRepairCost != 0) {
						oldRepairCost = oldRepairCost / 2;
					}
				}

				if (oldRepairCost == 0) {
					tag.remove("RepairCost");
				} else {
					tag.putInt("RepairCost", oldRepairCost);
				}

				// Then set the tag of the resulting item stack to what it would be without the "RepairCost" field
				itemStack2.setTag(tag);
			}
		}
	}
}
