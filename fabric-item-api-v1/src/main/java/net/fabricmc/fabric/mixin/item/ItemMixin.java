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

package net.fabricmc.fabric.mixin.item;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.UpdateAnimationHandler;
import net.fabricmc.fabric.impl.item.FabricItemInternals;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(Item.class)
abstract class ItemMixin implements ItemExtensions {
	@Unique
	private EquipmentSlotProvider fabric_equipmentSlotProvider;

	@Unique
	private CustomDamageHandler fabric_customDamageHandler;

	@Unique
	private UpdateAnimationHandler fabric_updateAnimationHandler;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo info) {
		FabricItemInternals.onBuild(settings, (Item) (Object) this);
	}

	@Override
	public EquipmentSlotProvider fabric_getEquipmentSlotProvider() {
		return fabric_equipmentSlotProvider;
	}

	@Override
	public void fabric_setEquipmentSlotProvider(EquipmentSlotProvider equipmentSlotProvider) {
		this.fabric_equipmentSlotProvider = equipmentSlotProvider;
	}

	@Override
	public CustomDamageHandler fabric_getCustomDamageHandler() {
		return fabric_customDamageHandler;
	}

	@Override
	public void fabric_setCustomDamageHandler(CustomDamageHandler handler) {
		this.fabric_customDamageHandler = handler;
	}

	@Override
	public @Nullable UpdateAnimationHandler fabric_getUpdateAnimationHandler() {
		return fabric_updateAnimationHandler;
	}

	@Override
	public void fabric_setUpdateAnimationHandler(UpdateAnimationHandler handler) {
		this.fabric_updateAnimationHandler = handler;
	}
}
