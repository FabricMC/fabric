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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;

import net.fabricmc.fabric.api.enchantment.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

/**
 * A simple mixin to add a callback to the anvil enchantment application logic.
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * <p>Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * to newer versions of the game.</p>
 */
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
	@Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
	public boolean updateResult(Enchantment enchantment, ItemStack itemStack) {
		TriState eventCallback = EnchantmentEvents.ACCEPT_APPLICATION.invoker().shouldAccept(enchantment, itemStack);

		if (eventCallback != TriState.DEFAULT) {
			return eventCallback.get();
		}

		return enchantment.isAcceptableItem(itemStack);
	}
}
