/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.api.event.block.ClimbingCallback;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

	@Shadow
	public abstract boolean isClimbing();

	private ClimbingCallback.Result climbingCallBackResult = null;

	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "isClimbing", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void isClimbing(CallbackInfoReturnable<Boolean> cir, final BlockState state) {

        final LivingEntity self = (LivingEntity) (Object) this;

		climbingCallBackResult = ClimbingCallback.EVENT.invoker().canClimb(self, state, getBlockPos());
        if (climbingCallBackResult != null) {
			if (climbingCallBackResult.climbSpeed <= 0.0D) {
				cir.setReturnValue(false);
			}
			else {
				cir.setReturnValue(true);
			}
			cir.cancel();
		}
    }

    @ModifyVariable(method = "travel", name = "vec3d_2", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE))
	private Vec3d modifyClimbVelocity(Vec3d vec3d_2) {
		if (isClimbing() && horizontalCollision) {
			ClimbingCallback.Result result = climbingCallBackResult;
			climbingCallBackResult = null;
			if (result != null) {
				return new Vec3d(vec3d_2.x, result.climbSpeed, vec3d_2.z);
			}
		}
		return vec3d_2;
	}
}
