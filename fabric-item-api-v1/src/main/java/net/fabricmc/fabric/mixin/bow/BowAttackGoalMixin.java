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

package net.fabricmc.fabric.mixin.bow;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.BowItem;

import net.fabricmc.fabric.api.item.v1.bow.FabricBowExtensions;

@Mixin(BowAttackGoal.class)
public abstract class BowAttackGoalMixin<T extends HostileEntity & RangedAttackMob> extends Goal {
	@Shadow
	@Final
	private T actor;

	// Confirms that an entity is using a bow by returning true
	@Inject(method = "isHoldingBow()Z", at = @At("HEAD"), cancellable = true)
	private void isHoldingCustomBow(CallbackInfoReturnable<Boolean> callbackInfo) {
		boolean holdingCustomBow = actor.isHolding(FabricBowExtensions.class::isInstance);

		if (holdingCustomBow) {
			callbackInfo.setReturnValue(true);
		}
	}

	// Modifies the pull progress if a custom bow is used
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
	private float redirectPullProgress(int useTicks) {
		if (actor.getActiveItem().getItem() instanceof FabricBowExtensions) {
			return ((FabricBowExtensions) actor.getActiveItem().getItem()).getCustomPullProgress(useTicks, actor.getActiveItem());
		}

		return BowItem.getPullProgress(useTicks);
	}
}
