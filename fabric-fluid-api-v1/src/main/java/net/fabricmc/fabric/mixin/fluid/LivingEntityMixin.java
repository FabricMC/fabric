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

package net.fabricmc.fabric.mixin.fluid;

import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
	@Shadow protected abstract float getBaseMovementSpeedMultiplier();
	@Shadow public abstract boolean isClimbing();
	@Shadow public abstract Vec3d method_26317(double d, boolean bl, Vec3d vec3d);
	@Shadow protected abstract boolean shouldSwimInFluids();
	@Shadow public abstract boolean canMoveVoluntarily();
	@Shadow public abstract void updateLimbs(LivingEntity entity, boolean flutter);
	@Shadow public abstract boolean canWalkOnFluid(Fluid fluid);

	@Inject(method = "fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", at = @At("HEAD"))
	private void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		//Check the fluid state every tick when falling and not touching a fabric_fluid, similar to water
		if (!this.isTouchingFabricFluid()) {
			this.checkFabricFluidState();
		}
	}

	@Redirect(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getMaxAir()I"))
	private int getMaxAirRedirect(@NotNull LivingEntity entity) {
		//If the entity is subberged in fabric_fluid returns -20, so basetick does not reset the air
		return entity.isSubmergedIn(FabricFluidTags.FABRIC_FLUID) ? -20 : entity.getMaxAir();
	}

	@Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(LivingEntity entity) {
		return this.isTouchingSwimmableFluid();
	}

	@Redirect(method = "tickMovement()V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tag/Tag;)D", ordinal = 1))
	private double getFluidHeightRedirect(LivingEntity entity, Tag<Fluid> tag) {
		if (this.isTouchingFabricFluid()) {
			return this.getFluidHeight(FabricFluidTags.FABRIC_FLUID);
		}
		else return this.getFluidHeight(tag);
	}

	@Inject(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
	private void travel(Vec3d movementInput, CallbackInfo ci) {
		if ((this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement())
				&& this.isTouchingFabricFluid() && this.shouldSwimInFluids()
				&& !this.canWalkOnFluid(this.world.getFluidState(this.getBlockPos()).getFluid())) {

			//Calculates the travel movement if the entity is on a fabric_fluid
			//This applies the same behaviour of an entity in water, but with fabric_fluid

			double d = 0.08D;
			boolean bl = this.getVelocity().y <= 0.0D;
			double e = this.getY();
			float j = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
			float g = 0.02F;

			this.updateVelocity(g, movementInput);
			this.move(MovementType.SELF, this.getVelocity());
			Vec3d vec3d = this.getVelocity();
			if (this.horizontalCollision && this.isClimbing()) {
				vec3d = new Vec3d(vec3d.x, 0.2D, vec3d.z);
			}

			this.setVelocity(vec3d.multiply(j, 0.800000011920929D, j));
			Vec3d vec3d2 = this.method_26317(d, bl, this.getVelocity());
			this.setVelocity(vec3d2);
			if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.getY() + e, vec3d2.z)) {
				this.setVelocity(new Vec3d(vec3d2.x, 0.30000001192092896D, vec3d2.z));
			}

			this.updateLimbs(this.getThis(), this.getThis() instanceof Flutterer);

			ci.cancel();
		}
	}

	@Unique
	private LivingEntity getThis() {
		return (LivingEntity)(Object)this;
	}
}
