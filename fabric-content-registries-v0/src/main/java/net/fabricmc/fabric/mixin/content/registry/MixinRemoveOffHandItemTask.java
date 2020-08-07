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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ai.brain.task.RemoveOffHandItemTask;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.registry.ShieldRegistry;

@Mixin(RemoveOffHandItemTask.class)
public abstract class MixinRemoveOffHandItemTask {
	/**
	 * Piglins should not drop modded shields either.
	 */
	@Redirect(method = "shouldRun", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item dontDropShields(ItemStack stack) {
		if (stack.getItem() == Items.SHIELD || ShieldRegistry.INSTANCE.isShield(stack.getItem())) {
			return Items.SHIELD;
		}

		return stack.getItem();
	}
}
