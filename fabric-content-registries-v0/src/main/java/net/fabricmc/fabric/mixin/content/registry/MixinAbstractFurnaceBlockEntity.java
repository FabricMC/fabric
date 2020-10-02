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

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.impl.content.registry.FuelRegistryImpl;

@Mixin(AbstractFurnaceBlockEntity.class)
public class MixinAbstractFurnaceBlockEntity {
	@Inject(at = @At("RETURN"), method = "createFuelTimeMap")
	private static void fuelTimeMapHook(CallbackInfoReturnable<Map<Item, Integer>> info) {
		FuelRegistryImpl.INSTANCE.apply(info.getReturnValue());
	}

	@Inject(at = @At("HEAD"), method = "canUseAsFuel", cancellable = true)
	private static void canUseAsFuelHook(ItemStack stack, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(FuelRegistry.INSTANCE.get(stack.getItem()) != null);
	}

	@Inject(at = @At("HEAD"), method = "getFuelTime", cancellable = true)
	protected void getFuelTimeHook(ItemStack fuel, CallbackInfoReturnable<Integer> info) {
		if (fuel.isEmpty()) {
			info.setReturnValue(0);
		} else {
			Integer burnTime = FuelRegistry.INSTANCE.get(fuel.getItem());
			info.setReturnValue(burnTime == null ? 0 : burnTime);
		}
	}
}
