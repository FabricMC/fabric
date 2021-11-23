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

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
	@Inject(method = "trySleep", at = @At("HEAD"), cancellable = true)
	private void onTrySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> info) {
		PlayerEntity.SleepFailureReason failureReason = EntitySleepEvents.ALLOW_SLEEPING.invoker().allowSleep((PlayerEntity) (Object) this, pos);

		if (failureReason != null) {
			info.setReturnValue(Either.left(failureReason));
		}
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isDay()Z"))
	private boolean redirectDaySleepCheck(World world) {
		boolean day = world.isDay();

		if (((LivingEntity) (Object) this).getSleepingPosition().isPresent()) {
			BlockPos pos = ((LivingEntity) (Object) this).getSleepingPosition().get();
			ActionResult result = EntitySleepEvents.ALLOW_SLEEP_TIME.invoker().allowSleepTime((PlayerEntity) (Object) this, pos, !day);

			if (result != ActionResult.PASS) {
				return !result.isAccepted(); // true from the event = night-like conditions, so we have to invert
			}
		}

		return day;
	}

	@Inject(method = "isSleepingLongEnough", at = @At("RETURN"), cancellable = true)
	private void onIsSleepingLongEnough(CallbackInfoReturnable<Boolean> info) {
		if (info.getReturnValueZ()) {
			info.setReturnValue(EntitySleepEvents.ALLOW_RESETTING_TIME.invoker().allowResettingTime((PlayerEntity) (Object) this));
		}
	}
}
