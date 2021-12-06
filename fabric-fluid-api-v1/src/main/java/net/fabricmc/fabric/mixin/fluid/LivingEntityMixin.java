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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.fabricmc.fabric.impl.fluid.FabricFluidLivingEntity;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements FabricFluidLivingEntity {
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

	//region BREATHING IN FLUIDS

	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isSubmergedRedirect(LivingEntity entity, Tag<Fluid> tag) {
		//Checks if the entity is submerged by a non-breathable fluid
		//If the entity cannot breathe on that fluid will lose air
		return !isSubmergedInBreathableFluid();
	}

	@Override
	public boolean isTouchingBreathableByAquaticFluid(boolean breatheOnRain) {
		return FluidUtils.isBreathableByAquatic(((FabricFluidEntity) getThis()).getFirstTouchedFabricFluid())
				|| getThis().isInsideWaterOrBubbleColumn()
				|| (breatheOnRain && getThis().isTouchingWaterOrRain());
	}

	@Override
	public boolean isSubmergedInBreathableFluid() {
		return FluidUtils.isBreathable(((FabricFluidEntity) getThis()).getSubmergedFluid());
	}

	//endregion

	//region SWIMMING

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/tag/Tag;)D", ordinal = 1))
	private double getFluidHeightRedirect(LivingEntity entity, Tag<Fluid> tag) {
		/* If an entity is on a fabric fluid, instead of water returns the fabric fluid height,
			that will be used like water to handle swim upward */
		return ((FabricFluidEntity) getThis()).isTouchingFabricFluid() ? ((FabricFluidEntity) getThis()).getFabricFluidHeight() : getThis().getFluidHeight(tag);
	}

	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(LivingEntity entity) {
		//If the entity is touching a swimmable fabric fluid, use the same behaviour of water for swim upward
		return ((FabricFluidEntity) getThis()).isTouchingSwimmableFluid();
	}

	//endregion

	//region MOVEMENT INTO THE FLUID

	@Inject(method = "travel", at = @At("HEAD"), cancellable = true)
	private void travel(Vec3d movementInput, CallbackInfo ci) {
		FluidState walkedFluid = getThis().world.getFluidState(getThis().getBlockPos());

		if ((this.canMoveVoluntarily() || getThis().isLogicalSideForUpdatingMovement())
				&& ((FabricFluidEntity) getThis()).isTouchingFabricFluid() && this.shouldSwimInFluids()
				&& !this.canWalkOnFluid(walkedFluid.getFluid())) {
			//Applies the slow falling effect

			double fallSpeed = 0.08D;
			boolean falling = getThis().getVelocity().y <= 0.0D;

			if (falling && getThis().hasStatusEffect(StatusEffects.SLOW_FALLING)) {
				fallSpeed = 0.01D;
				getThis().fallDistance = 0.0F;
			}

			//Updates the travel movement velocity if the entity is on a fabric fluid (uses the water behaviour)

			double currentY = getThis().getY();
			float movement = getThis().isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
			float speed = 0.02F;

			//Applies the depth strider effect

			if (!walkedFluid.isIn(FabricFluidTags.IGNORE_DEPTH_STRIDER)) {
				float depthStriderLevel = (float) EnchantmentHelper.getDepthStrider(getThis());

				if (depthStriderLevel > 3.0F) {
					depthStriderLevel = 3.0F;
				}

				if (!getThis().isOnGround()) {
					depthStriderLevel *= 0.5F;
				}

				if (depthStriderLevel > 0.0F) {
					movement += (0.54600006F - movement) * depthStriderLevel / 3.0F;
					speed += (getThis().getMovementSpeed() - speed) * depthStriderLevel / 3.0F;
				}
			}

			//Updates the travel movement velocity

			getThis().updateVelocity(speed, movementInput);
			getThis().move(MovementType.SELF, getThis().getVelocity());

			Vec3d velocity = getThis().getVelocity();

			if (getThis().horizontalCollision && this.isClimbing()) {
				velocity = new Vec3d(velocity.x, 0.2D, velocity.z);
			}

			getThis().setVelocity(velocity.multiply(movement, 0.800000011920929D, movement));

			Vec3d newVelocity = this.method_26317(fallSpeed, falling, getThis().getVelocity());
			getThis().setVelocity(newVelocity);

			if (getThis().horizontalCollision && getThis().doesNotCollide(newVelocity.x,
					newVelocity.y + 0.6000000238418579D - getThis().getY() + currentY, newVelocity.z)) {
				getThis().setVelocity(newVelocity.x, 0.30000001192092896D, newVelocity.z);
			}

			//Updates the limbs movement

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
