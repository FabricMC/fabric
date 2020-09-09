/*
 * Copyright (c) 2016, 2017, 2018, 2019, 2020 FabricMC
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

package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(Item.class)
abstract class ItemMixin implements ItemExtensions {
	@Unique
	private EquipmentSlotProvider equipmentSlotProvider;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		FabricItemInternals.onBuild(settings, (Item) (Object) this);
	}

	@Override
	public EquipmentSlotProvider fabric_getEquipmentSlotProvider() {
		return equipmentSlotProvider;
	}

	@Override
	public void fabric_setEquipmentSlotProvider(EquipmentSlotProvider equipmentSlotProvider) {
		this.equipmentSlotProvider = equipmentSlotProvider;
	}

	@Inject(method = "isUsedOnRelease", at = @At("HEAD"), cancellable = true)
	public void isUsedOnRelease(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (itemStack.getItem() instanceof CrossbowItem) {
			cir.setReturnValue(true);
		}
	}
}
