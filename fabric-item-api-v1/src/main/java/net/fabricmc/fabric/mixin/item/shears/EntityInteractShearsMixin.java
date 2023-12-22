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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.item.ShearsHelper;

@Mixin({SheepEntity.class, SnowGolemEntity.class, MooshroomEntity.class})
public abstract class EntityInteractShearsMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "interactMob")
	private boolean isShears(boolean original, @Local ItemStack stack) {
		// allows anything in fabric:shears to shear sheep and snow golems
		return original || ShearsHelper.isShears(stack);
	}
}
