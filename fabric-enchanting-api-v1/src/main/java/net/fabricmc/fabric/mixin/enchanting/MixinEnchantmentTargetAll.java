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

package net.fabricmc.fabric.mixin.enchanting;

import net.fabricmc.fabric.api.enchanting.v1.FabricEnchantmentTarget;
import net.fabricmc.fabric.impl.enchanting.EnchantmentTargetRegistryImpl;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$1")
public class MixinEnchantmentTargetAll {
	@SuppressWarnings({"WeakerAccess", "UnnecessaryQualifiedMemberReference"})
	@Inject(method = "Lnet/minecraft/enchantment/EnchantmentTarget$1;isAcceptableItem(Lnet/minecraft/item/Item;)Z", at = @At("TAIL"), cancellable = true)
	public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
		for (Iterator<FabricEnchantmentTarget> it = EnchantmentTargetRegistryImpl.INSTANCE.getIterator(); it.hasNext(); ) {
			if (it.next().isAcceptableItem(item)) {
				callbackInfoReturnable.setReturnValue(true);
				return;
			}
		}
	}
}
