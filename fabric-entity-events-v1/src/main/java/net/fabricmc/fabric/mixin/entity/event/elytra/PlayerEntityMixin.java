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

package net.fabricmc.fabric.mixin.entity.event.elytra;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.entity.event.v1.AllowElytraFlight;

@Mixin(value = PlayerEntity.class, priority = 900)
abstract class PlayerEntityMixin extends LivingEntity {
	PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		throw new AssertionError();
	}

	@Shadow
	public abstract void startFallFlying();

	/**
	 * Allow the server-side and client-side elytra checks to succeed for elytra flight through {@link AllowElytraFlight}.
	 */
	@Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EquipmentSlot;CHEST:Lnet/minecraft/entity/EquipmentSlot;"), method = "checkFallFlying()Z", require = 1, allow = 1, cancellable = true)
	void injectElytraCheck(CallbackInfoReturnable<Boolean> cir) {
		if (AllowElytraFlight.EVENT.invoker().allowElytraFlight(PlayerEntity.class.cast(this), false)) {
			startFallFlying();
			cir.setReturnValue(true);
		}
	}
}
