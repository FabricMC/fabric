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

import java.util.Optional;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.api.util.SoundParameters;
import net.fabricmc.fabric.impl.fluid.FabricFluidClientPlayerEntity;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;

@Mixin(Entity.class)
public abstract class EntityMixin implements FabricFluidEntity {
	//region INTERNAL METHODS AND VARIABLES PLACEHOLDERS

	@Shadow
	public World world;
	@Shadow
	public float fallDistance;
	@Shadow
	protected boolean firstUpdate;
	@Shadow
	@Final
	protected Random random;
	@Unique
	private boolean touchingFabricFluid = false;
	@Unique
	private boolean submergedInFabricFluid = false;
	@Unique
	private FluidState submergedFluid = null;
	@Unique
	private FluidState firstTouchedFabricFluid = null;
	@Unique
	private double fabricFluidHeight = 0;

	@Shadow
	@Nullable
	public abstract Entity getVehicle();

	@Shadow
	public abstract void extinguish();

	@Shadow
	public abstract boolean isSpectator();

	@Shadow
	public abstract Vec3d getVelocity();

	@Shadow
	public abstract void setVelocity(Vec3d velocity);

	@Shadow
	protected abstract void playExtinguishSound();

	@Shadow
	public abstract boolean isOnFire();

	@Shadow
	public abstract boolean isTouchingWater();

	@Shadow
	public abstract boolean isSubmergedInWater();

	@Shadow
	public abstract void playSound(SoundEvent sound, float volume, float pitch);

	@Shadow
	public abstract double getFluidHeight(Tag<Fluid> fluid);

	@Shadow
	public abstract boolean isRegionUnloaded();

	@Shadow
	public abstract Box getBoundingBox();

	@Shadow
	public abstract boolean isPushedByFluids();

	//endregion

	//region SWIM

	@Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect1(Entity entity) {
		//Adds the swimmable fabric fluids to the valid fluids for swimming
		return this.isTouchingSwimmableFluid();
	}

	@Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedInWater()Z"))
	private boolean isSubmergedInWaterRedirect(Entity entity) {
		//Adds the swimmable fabric fluids to the valid fluids for swimming
		return this.isSubmergedInSwimmableFluid();
	}

	@Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect(FluidState state, Tag<Fluid> tag) {
		//Adds the swimmable fabric fluids to the valid fluids for swimming
		return FluidUtils.isSwimmable(state) && !state.isIn(FluidTags.LAVA);
	}

	@Redirect(method = "shouldLeaveSwimmingPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect2(Entity entity) {
		//If the entity is not touching a swimmable fluid, should leave swimming pose
		return this.isTouchingSwimmableFluid();
	}

	//endregion

	//region SOUNDS AND SPRINTING PARTICLES

	@Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect3(Entity entity) {
		//Adds the fabric fluids to the valid fluids to play swim sounds
		return entity.isTouchingWater() || this.isTouchingFabricFluid();
	}

	@Inject(method = "playSwimSound", at = @At(value = "HEAD"), cancellable = true)
	private void playSwimSound(float volume, CallbackInfo ci) {
		//If the entity is touching a fabric fluid gets the swim sound from the fluid and plays it
		if (this.isTouchingFabricFluid()) {
			//Plays the swim sound
			Optional<SoundEvent> swimSound = ((FabricFlowableFluid) firstTouchedFabricFluid.getFluid()).getSwimSound();
			swimSound.ifPresent(sound -> this.playSound(sound, volume, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F));

			ci.cancel();
		}
	}

	@Inject(method = "getSplashSound", at = @At(value = "HEAD"), cancellable = true)
	private void getSplashSound(CallbackInfoReturnable<SoundEvent> cir) {
		//If the entity is touching a fabric fluid gets the splash sound and returns it
		if (this.isTouchingFabricFluid()) {
			//Gets the generic splash sound (if not present, will be used the default pre-defined sound)
			Optional<SoundEvent> splashSound = ((FabricFlowableFluid) firstTouchedFabricFluid.getFluid()).getGenericSplashSound();
			splashSound.ifPresent(cir::setReturnValue);
		}
	}

	@Inject(method = "shouldSpawnSprintingParticles", at = @At("HEAD"), cancellable = true)
	private void shouldSpawnSprintingParticles(CallbackInfoReturnable<Boolean> cir) {
		//Don't spawn sprinting particles if the entity is touching a fabric fluid
		if (this.isTouchingFabricFluid()) {
			cir.setReturnValue(false);
		}
	}

	//endregion

	//region HOT DAMAGE

	@Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z", ordinal = 0))
	private boolean isInLavaRedirect(Entity entity) {
		if (this.isTouchingFabricFluid()) {
			return true;
		} else {
			return entity.isInLava();
		}
	}

	//endregion

	//region WET DAMAGE

	@Inject(method = "isWet", at = @At("HEAD"), cancellable = true)
	private void isWet(CallbackInfoReturnable<Boolean> cir) {
		//Adds the fabric fluids to the valid fluids that could to wet entities
		if (this.isTouchingFabricFluid() && firstTouchedFabricFluid.isIn(FabricFluidTags.WET)) {
			cir.setReturnValue(true);
		}
	}

	//endregion

	//region TOUCHING AND SUBMERGED STATE UPDATERS

	@Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateSubmergedInWaterState()V", shift = At.Shift.AFTER))
	private void baseTick(CallbackInfo ci) {
		//Updates the current touching and submerged in fabric fluid state, and the current submerged fluid
		this.checkFabricFluidState();
		this.updateSubmergedInFabricFluidState();
	}

	//NOTE: LivingEntityMixin requires checkFabricFluidState to be public, but it should be private, so is deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	@Override
	public void checkFabricFluidState() {
		//The result is true if the entity is currently touching a fabric fluid
		if (this.updateMovementInFabricFluid()) {
			FabricFlowableFluid fluid = (FabricFlowableFluid) firstTouchedFabricFluid.getFluid();

			//If the entity, in the previous tick, not touches the fluid, executes the "fluid touched" event
			if (!this.touchingFabricFluid && !this.firstUpdate) {
				this.onFabricFluidTouched(fluid);
			}

			//Prevent fall damage and extinguish fire
			if (fluid.isIn(FabricFluidTags.PREVENT_FALL_DAMAGE)) {
				this.fallDistance = 0.0F;
			}

			if (fluid.isIn(FabricFluidTags.CAN_EXTINGUISH_FIRE) && this.isOnFire()) {
				this.extinguish();
				this.playExtinguishSound();
			}

			this.touchingFabricFluid = true;
		} else {
			this.touchingFabricFluid = false;
		}
	}

	@Unique
	private boolean updateMovementInFabricFluid() {
		fabricFluidHeight = 0;
		firstTouchedFabricFluid = null;

		if (this.isRegionUnloaded()) {
			return false;
		}

		if (this.getVehicle() instanceof BoatEntity) {
			return false;
		}

		//Calculates and applies the pushing strength of the fluid to the entity

		Box box = this.getBoundingBox().contract(0.001D);
		int minX = MathHelper.floor(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.floor(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.floor(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		boolean pushable = this.isPushedByFluids();
		double maxTouchedFluidHeight = 0.0D;
		Vec3d pushingStrength = Vec3d.ZERO;
		int nfluid = 0;

		for (int x = minX; x < maxX; ++x) {
			for (int y = minY; y < maxY; ++y) {
				for (int z = minZ; z < maxZ; ++z) {
					mutable.set(x, y, z);
					FluidState fluidState = this.world.getFluidState(mutable);

					if (FluidUtils.isFabricFluid(fluidState)) {
						double height = y + fluidState.getHeight(this.world, mutable);

						if (height >= box.minY) {
							//Set the first touched fabric fluid
							if (firstTouchedFabricFluid == null) {
								firstTouchedFabricFluid = fluidState;
							}

							//Calc the max touched fluid height
							maxTouchedFluidHeight = Math.max(height - box.minY, maxTouchedFluidHeight);

							if (pushable) {
								//Get the fluid velocity
								Vec3d fluidVelocity = fluidState.getVelocity(this.world, mutable);

								if (maxTouchedFluidHeight < 0.4D) {
									fluidVelocity = fluidVelocity.multiply(maxTouchedFluidHeight);
								}

								//Sum the velocity of all fabric fluid touched
								pushingStrength = pushingStrength.add(fluidVelocity);

								//Number of touched fluids
								++nfluid;
							}
						}
					}
				}
			}
		}

		if (pushingStrength.length() > 0.0D) {
			//Calc the average velocity of all fabric fluids touched
			if (nfluid > 0) {
				pushingStrength = pushingStrength.multiply(1.0D / (double) nfluid);
			}

			//If the entity is not a player normalize the velocity
			if (!(getThis() instanceof PlayerEntity)) {
				pushingStrength = pushingStrength.normalize();
			}

			//Apply the fluid viscosity to calc the pushing strength
			double viscosity = ((FabricFlowableFluid) firstTouchedFabricFluid.getFluid()).getViscosity(this.world, this.getThis());
			pushingStrength = pushingStrength.multiply(viscosity);

			//Normalize the pushing strength basing on the current entity velocity
			Vec3d entityVelocity = this.getVelocity();

			if (Math.abs(entityVelocity.x) < 0.003D && Math.abs(entityVelocity.z) < 0.003D && pushingStrength.length() < 0.0045000000000000005D) {
				pushingStrength = pushingStrength.normalize().multiply(0.0045000000000000005D);
			}

			//Apply the pushing strength of the fluid
			this.setVelocity(this.getVelocity().add(pushingStrength));
		}

		fabricFluidHeight = maxTouchedFluidHeight;
		return firstTouchedFabricFluid != null;
	}

	@Unique
	private void updateSubmergedInFabricFluidState() {
		this.submergedInFabricFluid = FluidUtils.isFabricFluid(this.submergedFluid);

		//Get the actual fluid that submerges the entity, (if is not submerged, the result is null)
		FluidState newSubmergedFluid = FluidUtils.getSubmergedFluid(getThis());

		/* Checks "enter in", "exit from", and "changed" fluid
			and executes the respective protected event */
		this.onSubmergedFluidUpdated(this.submergedFluid, newSubmergedFluid);

		this.submergedFluid = newSubmergedFluid;
	}

	//endregion

	//region TOUCHING AND SUBMERGED EVENTS

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo ci) {
		//Events are not executed on spectator mode, and for exp orbs
		if (this.isSpectator() || this.getThis() instanceof ExperienceOrbEntity) {
			return;
		}

		//Executes an event if the entity is submerged in a fabric fluid
		if (isActuallySubmergedInFabricFluid()) {
			((FabricFlowableFluid) submergedFluid.getFluid()).onSubmerged(this.world, this.getThis());
		}

		//Executes an event if the entity is touching a fabric fluid
		if (isTouchingFabricFluid()) {
			((FabricFlowableFluid) firstTouchedFabricFluid.getFluid()).onTouching(this.world, this.getThis());
		}
	}

	@Unique
	private void onFabricFluidTouched(FabricFlowableFluid fluid) {
		//Events are not executed on spectator mode, and for exp orbs
		if (this.isSpectator() || this.getThis() instanceof ExperienceOrbEntity) {
			return;
		}

		//Plays the splash sound
		SoundParameters splashSound = fluid.getSplashSound(this.world, getThis());
		splashSound.ifHasSound(sound -> this.playSound(sound.getSoundEvent(), sound.getVolume(), sound.getPitch()));

		//Executes an event if the entity is splashing in a fabric fluid
		fluid.onSplash(this.world, this.getThis());
	}

	@Unique
	private void onSubmergedFluidUpdated(FluidState oldFluidState, FluidState newFluidState) {
		if (getThis() instanceof FabricFluidClientPlayerEntity clientPlayer) {
			if (!FluidUtils.areEqual(oldFluidState, newFluidState)) {
				if (oldFluidState == null && newFluidState != null) {
					clientPlayer.enterInFluid(newFluidState);
				} else if (oldFluidState != null && newFluidState == null) {
					clientPlayer.exitFromFluid(oldFluidState);
				} else if (oldFluidState != null) {
					clientPlayer.changedFluid(oldFluidState, newFluidState);
				}
			}
		}
	}

	//endregion

	//region STATE CHECKERS

	@Override
	public FluidState getFirstTouchedFabricFluid() {
		return firstTouchedFabricFluid;
	}

	@Override
	public FluidState getSubmergedFluid() {
		return submergedFluid;
	}

	@Override
	public boolean isInFabricFluid() {
		return !this.firstUpdate && this.getFabricFluidHeight() > 0.0D;
	}

	@Override
	public boolean isInWater() {
		return !this.firstUpdate && this.getFluidHeight(FluidTags.WATER) > 0.0D;
	}

	@Override
	public boolean isSubmergedInFabricFluid() {
		return submergedInFabricFluid;
	}

	@Override
	public boolean isTouchingFabricFluid() {
		return touchingFabricFluid;
	}

	@Override
	public boolean isSubmergedInSwimmableFluid() {
		return this.isSubmergedInWater() || (this.isSubmergedInFabricFluid() && FluidUtils.isSwimmable(submergedFluid));
	}

	@Override
	public boolean isTouchingSwimmableFluid() {
		return this.isTouchingWater() || (this.isTouchingFabricFluid() && FluidUtils.isSwimmable(firstTouchedFabricFluid));
	}

	@Override
	public double getFabricFluidHeight() {
		return this.fabricFluidHeight;
	}

	@Unique
	private boolean isActuallySubmergedInFabricFluid() {
		return isSubmergedInFabricFluid() && submergedFluid != null && submergedFluid.getFluid() instanceof FabricFlowableFluid;
	}

	//endregion

	@Unique
	private Entity getThis() {
		return (Entity) (Object) this;
	}
}
