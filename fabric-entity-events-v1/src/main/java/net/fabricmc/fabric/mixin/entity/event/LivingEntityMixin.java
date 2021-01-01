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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends EntityMixin {
	@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onKilledOther(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onEntityKilledOther(DamageSource source, CallbackInfo ci, Entity attacker) {
		// FIXME: Cannot use shadowed fields from supermixins - needs a fix so people can use fabric api in a dev environment even though this is fine in this repo and prod.
		//  A temporary fix is to just cast the mixin to LivingEntity and access the world field with a few ugly casts.
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.invoker().afterKilledOtherEntity((ServerWorld) ((LivingEntity) (Object) this).world, attacker, (LivingEntity) (Object) this);
	}
}
