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

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends EntityMixin {
	@Shadow
	public abstract boolean isDead();

	@Shadow
	public abstract Optional<BlockPos> getSleepingPosition();

	@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onKilledOther(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onEntityKilledOther(DamageSource source, CallbackInfo ci, Entity attacker) {
		// FIXME: Cannot use shadowed fields from supermixins - needs a fix so people can use fabric api in a dev environment even though this is fine in this repo and prod.
		//  A temporary fix is to just cast the mixin to LivingEntity and access the world field with a few ugly casts.
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.invoker().afterKilledOtherEntity((ServerWorld) ((LivingEntity) (Object) this).world, attacker, (LivingEntity) (Object) this);
	}

	@Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isDead()Z", ordinal = 1))
	boolean beforePlayerKilled(LivingEntity livingEntity, DamageSource source, float amount) {
		if (livingEntity instanceof ServerPlayerEntity) {
			return isDead() && ServerPlayerEvents.ALLOW_DEATH.invoker().allowDeath((ServerPlayerEntity) livingEntity, source, amount);
		}

		return isDead();
	}

	@Inject(method = "sleep", at = @At("RETURN"))
	private void onSleep(BlockPos pos, CallbackInfo info) {
		EntitySleepEvents.START_SLEEPING.invoker().onSleep((LivingEntity) (Object) this, pos);
	}

	@Inject(method = "wakeUp", at = @At("HEAD"))
	private void onWakeUp(CallbackInfo info) {
		// If actually asleep - this method is often called "just to be sure"...
		if (getSleepingPosition().isPresent()) {
			EntitySleepEvents.WAKE_UP.invoker().onWakeUp((LivingEntity) (Object) this);
		}
	}

	@Inject(method = "isSleepingInBed", at = @At("RETURN"), cancellable = true)
	private void onIsSleepingInBed(CallbackInfoReturnable<Boolean> info) {
		if (getSleepingPosition().isPresent()) {
			BlockPos sleepingPos = getSleepingPosition().get();
			BlockState bedState = world.getBlockState(sleepingPos);
			ActionResult result = EntitySleepEvents.IS_VALID_BED.invoker().isValidBed((LivingEntity) (Object) this, sleepingPos, bedState);

			if (result != ActionResult.PASS) {
				info.setReturnValue(result.isAccepted());
			}
		}
	}

	@Inject(method = "getSleepingDirection", at = @At("RETURN"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void onGetSleepingDirection(CallbackInfoReturnable<Direction> info, @Nullable BlockPos sleepingPos) {
		if (sleepingPos != null) {
			Optional<Direction> newDirection = EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.invoker().modifySleepDirection((LivingEntity) (Object) this, sleepingPos, info.getReturnValue());

			if (newDirection.isPresent()) {
				info.setReturnValue(newDirection.get());
			}
		}
	}
}
