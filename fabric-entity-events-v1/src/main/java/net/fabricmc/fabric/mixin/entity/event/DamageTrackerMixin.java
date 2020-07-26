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

package net.fabricmc.fabric.mixin.entity.event;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;

import net.fabricmc.fabric.api.entity.event.v1.LivingEntityEvents;

@Mixin(DamageTracker.class)
public abstract class DamageTrackerMixin {
	@Shadow
	@Final
	private LivingEntity entity;

	@Inject(method = "onDamage", at = @At("HEAD"))
	private void afterDamage(DamageSource damageSource, float originalHealth, float damageAmount, CallbackInfo ci) {
		LivingEntityEvents.AFTER_DAMAGED.invoker().afterDamaged(this.entity, damageSource, damageAmount, originalHealth);
	}
}
