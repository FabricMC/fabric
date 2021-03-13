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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.item.v1.crossbow.FabricCrossbowExtensions;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
	private static final Hand[] HANDS = {Hand.MAIN_HAND, Hand.OFF_HAND}; // Cache the hands to not create the hands array each time the loop is run

	// Because the uses of this method are hardcoded, checking each hand for the Fabric interfaces of the items is needed.
	// Note: this does not cancel for the vanilla items unless they are holding a custom implementation of the items
	@Inject(method = "getHandPossiblyHolding", at = @At(value = "HEAD"), cancellable = true)
	private static void getHandPossiblyHolding(LivingEntity entity, Item item, CallbackInfoReturnable<Hand> cir) {
		for (Hand hand : HANDS) {
			if (item == Items.CROSSBOW) { // Make sure we only check for crossbows when searching for crossbows and allow for other items like bows in future
				if (entity.getStackInHand(hand).getItem() instanceof FabricCrossbowExtensions) {
					cir.setReturnValue(hand);
					return;
				}
			}
		}
	}
}
