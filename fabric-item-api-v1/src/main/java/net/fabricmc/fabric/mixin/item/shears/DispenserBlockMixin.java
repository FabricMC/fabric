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

package net.fabricmc.fabric.mixin.item.shears;

import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.item.ShearsHelper;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
	@Shadow
	@Final
	private static Map<Item, DispenserBehavior> BEHAVIORS;

	@ModifyReturnValue(at = @At("TAIL"), method = "getBehaviorForItem")
	private DispenserBehavior registerShearsBehavior(DispenserBehavior original, @Local ItemStack stack) {
		// allows anything in fabric:shears to have the dispenser behavior of shears,
		// but only if there isn't a dispenser behavior already registered
		if (!BEHAVIORS.containsKey(stack.getItem())
				&& ShearsHelper.isShears(stack)) {
			return new ShearsDispenserBehavior();
		}

		return original;
	}
}
