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

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	//region INTERNAL METHODS AND VARIABLES PLACEHOLDERS

	@Shadow
	protected abstract float getBaseMovementSpeedMultiplier();

	@Shadow
	public abstract boolean isClimbing();

	@Shadow
	public abstract Vec3d method_26317(double d, boolean bl, Vec3d vec3d);

	@Shadow
	protected abstract boolean shouldSwimInFluids();

	@Shadow
	public abstract boolean canMoveVoluntarily();

	@Shadow
	public abstract void updateLimbs(LivingEntity entity, boolean flutter);

	@Shadow
	public abstract boolean canWalkOnFluid(Fluid fluid);

	//endregion

	//region FALL DAMAGE

	@SuppressWarnings("deprecation")
	@Inject(method = "fall", at = @At("HEAD"))
	private void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo ci) {
		//Check every tick, when falling, if there is a fabric fluid that can prevent fall damage
		if (!((FabricFluidEntity) getThis()).isTouchingFabricFluid()) {
			//NOTE: This requires checkFabricFluidState to be public, but it should be private, so is deprecated
			((FabricFluidEntity) getThis()).checkFabricFluidState();
		}
	}

	//endregion

	//region DROWNING

	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getMaxAir()I"))
	private int getMaxAirRedirect(@NotNull LivingEntity entity) {
		//If the entity is submerged in a fabric fluid, returns -20, so basetick does not reset the air
		return FluidUtils.isFabricFluid(((FabricFluidEntity) getThis()).getSubmergedFluid()) ? -20 : entity.getMaxAir();
	}

	//endregion

	//region SWIMMING

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tag/Tag;)D", ordinal = 1))
	private double getFluidHeightRedirect(LivingEntity entity, Tag<Fluid> tag) {
		//Adds the fabric fluids to the valid fluids for swimming
		return ((FabricFluidEntity) getThis()).isTouchingFabricFluid() ? ((FabricFluidEntity) getThis()).getFabricFluidHeight() : getThis().getFluidHeight(tag);
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(LivingEntity entity) {
		//Adds the swimmable fabric fluids to the valid fluids for swimming
		return ((FabricFluidEntity) getThis()).isTouchingSwimmableFluid();
	}

	//endregion

	//region MOVEMENT SPEED INTO THE FLUID

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	private void travel(Vec3d movementInput, CallbackInfo ci) {
		if ((this.canMoveVoluntarily() || getThis().isLogicalSideForUpdatingMovement())
				&& ((FabricFluidEntity) getThis()).isTouchingFabricFluid() && this.shouldSwimInFluids()
				&& !this.canWalkOnFluid(getThis().world.getFluidState(getThis().getBlockPos()).getFluid())) {
			//Updates the travel movement velocity if the entity is on a fabric fluid (uses the water behaviour)
			boolean falling = getThis().getVelocity().y <= 0.0D;
			double currentY = getThis().getY();
			float movement = getThis().isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();

			getThis().updateVelocity(0.02F, movementInput);
			getThis().move(MovementType.SELF, getThis().getVelocity());

			Vec3d velocity = getThis().getVelocity();

			if (getThis().horizontalCollision && this.isClimbing()) {
				velocity = new Vec3d(velocity.x, 0.2D, velocity.z);
			}

			getThis().setVelocity(velocity.multiply(movement, 0.800000011920929D, movement));

			Vec3d newVelocity = this.method_26317(0.08D, falling, getThis().getVelocity());
			getThis().setVelocity(newVelocity);
			if (getThis().horizontalCollision && getThis().doesNotCollide(newVelocity.x,
					newVelocity.y + 0.6000000238418579D - getThis().getY() + currentY, newVelocity.z)) {
				getThis().setVelocity(new Vec3d(newVelocity.x, 0.30000001192092896D, newVelocity.z));
			}

			this.updateLimbs(this.getThis(), this.getThis() instanceof Flutterer);

			ci.cancel();
		}
	}

	//endregion

	@Unique
	private LivingEntity getThis() {
		return (LivingEntity) (Object) this;
	}
}
