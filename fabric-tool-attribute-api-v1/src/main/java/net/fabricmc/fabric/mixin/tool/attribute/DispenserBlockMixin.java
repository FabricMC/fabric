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

package net.fabricmc.fabric.mixin.tool.attribute;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {
	@Shadow
	@Final
	private static Map<Item, DispenserBehavior> BEHAVIORS;

	@Inject(method = "getBehaviorForItem", at = @At("HEAD"), cancellable = true)
	private void getBehaviorForItem(ItemStack stack, CallbackInfoReturnable<DispenserBehavior> cir) {
		if (stack.getItem().isIn(FabricToolTags.SHEARS)) {
			cir.setReturnValue(BEHAVIORS.get(Items.SHEARS));
		}
	}
}
