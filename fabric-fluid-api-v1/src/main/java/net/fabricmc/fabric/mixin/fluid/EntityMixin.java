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

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.MovementType;
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

import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements FabricFluidEntity {
	//region INTERNAL METHODS AND VARIABLES PLACEHOLDERS

    @Shadow public World world;
    @Shadow public float fallDistance;
	@Shadow protected boolean firstUpdate;
	@Shadow protected boolean submergedInWater;
	@Shadow @Nullable protected Tag<Fluid> submergedFluidTag;
	@Shadow public boolean horizontalCollision;
	@Shadow	@Final protected Random random;

	@Shadow @Nullable public abstract Entity getVehicle();
    @Shadow public abstract void extinguish();
    @Shadow public abstract double getX();
    @Shadow public abstract double getZ();
	@Shadow public abstract double getEyeY();
	@Shadow public abstract boolean isSubmergedIn(Tag<Fluid> fluidTag);
	@Shadow public abstract boolean isSpectator();
	@Shadow public abstract double getY();
	@Shadow public abstract BlockPos getBlockPos();
	@Shadow public abstract boolean isSprinting();
	@Shadow	public abstract void updateVelocity(float speed, Vec3d movementInput);
	@Shadow public abstract void move(MovementType movementType, Vec3d movement);
	@Shadow public abstract Vec3d getVelocity();
	@Shadow public abstract void setVelocity(Vec3d velocity);
	@Shadow public abstract boolean doesNotCollide(double offsetX, double offsetY, double offsetZ);
	@Shadow public abstract boolean isLogicalSideForUpdatingMovement();
	@Shadow protected abstract void playExtinguishSound();
	@Shadow public abstract boolean isOnFire();
	@Shadow public abstract boolean isSwimming();
	@Shadow public abstract void setSwimming(boolean swimming);
	@Shadow public abstract boolean hasVehicle();
	@Shadow public abstract boolean isTouchingWater();
	@Shadow public abstract boolean isSubmergedInWater();
	@Shadow	public abstract boolean isInSwimmingPose();
	@Shadow	public abstract void playSound(SoundEvent sound, float volume, float pitch);
	@Shadow public abstract double getFluidHeight(Tag<Fluid> fluid);
	@Shadow public abstract boolean isRegionUnloaded();
	@Shadow public abstract Box getBoundingBox();
	@Shadow public abstract boolean isPushedByFluids();

	//endregion

	@Unique protected boolean touchingFabricFluid = false;
	@Unique protected boolean submergedInFabricFluid = false;
	@Unique protected FluidState submergedFluid = null;
	@Unique protected FluidState firstTouchedFabricFluid = null;
	@Unique protected double fabricFluidHeight = 0;

	//region SWIM

	@Inject(method = "updateSwimming()V", at = @At(value = "HEAD"), cancellable = true)
	private void updateSwimming(CallbackInfo ci) {
		if (this.isSwimming()) {
			this.setSwimming(this.isSprinting()
					&& (this.isTouchingWater() || this.isTouchingFabricFluid())
					&& !this.hasVehicle());
		} else {
			this.setSwimming(this.isSprinting()
					&& (this.isSubmergedInWater() || this.isSubmergedInFabricFluid())
					&& !this.hasVehicle()
					&& (this.isInWater() || this.isInFabricFluid()));
		}
		ci.cancel();
	}

	@Inject(method = "shouldLeaveSwimmingPose()Z", at = @At(value = "HEAD"), cancellable = true)
	private void shouldLeaveSwimmingPose(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.isInSwimmingPose() && (!this.isTouchingWater() || !this.isTouchingFabricFluid()));
	}

	//swimming sounds

	@Redirect(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
	private boolean isTouchingWater(Entity entity) {
		return this.isTouchingWater() || this.isTouchingFabricFluid();
	}

	@Inject(method = "playSwimSound(F)V", at = @At(value = "HEAD"), cancellable = true)
	private void playSwimSound(float volume, CallbackInfo ci) {
		if (isTouchingFabricFluid() && firstTouchedFabricFluid.getFluid() instanceof FabricFlowableFluid fluid) {
			fluid.getSwimSound().ifPresent(sound -> this.playSound(sound, volume, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F));
			ci.cancel();
		}
	}

	//sprinting particles

	@Inject(method = "shouldSpawnSprintingParticles()Z", at = @At("HEAD"), cancellable = true)
	private void shouldSpawnSprintingParticles(CallbackInfoReturnable<Boolean> cir) {
		//Don't spawn sprinting particles if the entity is touching a fabric_fluid
		if (this.isTouchingFabricFluid()) cir.setReturnValue(false);
	}

	//endregion

	//region HOT DAMAGE

	@Redirect(method = "baseTick()V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;isInLava()Z", ordinal = 0))
	private boolean isInLavaRedirect(Entity entity) {
		if (isTouchingFabricFluid()) return true;
		else return entity.isInLava();
	}

	//endregion

	//region WET DAMAGE

	@Inject(method = "isWet()Z", at = @At("HEAD"), cancellable = true)
	private void isWet(CallbackInfoReturnable<Boolean> cir) {
		if (isTouchingFabricFluid() && firstTouchedFabricFluid.isIn(FabricFluidTags.WET)) {
			cir.setReturnValue(true);
		}
	}

	//endregion

	//region TOUCHING AND SUBMERGED STATE UPDATERS

	@Inject(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z", shift = At.Shift.AFTER))
	private void updateFabricFluidState(CallbackInfo ci) {
		this.checkFabricFluidState();
	}

	@Unique
	protected void checkFabricFluidState() {
		//The result is true if the entity is currently touching a fabric fluid
		if (this.updateMovementInFabricFluid()) {
			FabricFlowableFluid fluid = (FabricFlowableFluid)firstTouchedFabricFluid.getFluid();

			//If the entity, in the previous tick, not touches the fluid, executes the "fluid touched" event
			if (!this.touchingFabricFluid && !this.firstUpdate) this.onFabricFluidTouched(fluid);

			//Prevent fall damage and extinguish fire
			if (fluid.isIn(FabricFluidTags.PREVENT_FALL_DAMAGE)) this.fallDistance = 0.0F;
			if (fluid.isIn(FabricFluidTags.FIRE_EXTINGUISHER) && this.isOnFire()) {
				this.extinguish();
				this.playExtinguishSound();
			}

			this.touchingFabricFluid = true;
		} else {
			this.touchingFabricFluid = false;
		}
	}

	@Unique
	protected boolean updateMovementInFabricFluid() {
		fabricFluidHeight = 0;
		firstTouchedFabricFluid = null;

		if (this.isRegionUnloaded()) return false;
		if (this.getVehicle() instanceof BoatEntity) return false;

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

		for(int x = minX; x < maxX; ++x) {
			for(int y = minY; y < maxY; ++y) {
				for(int z = minZ; z < maxZ; ++z) {
					mutable.set(x, y, z);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (FluidUtils.isFabricFluid(fluidState)) {
						double height = y + fluidState.getHeight(this.world, mutable);
						if (height >= box.minY) {
							//Set the first touched fabric fluid
							if (firstTouchedFabricFluid == null) firstTouchedFabricFluid = fluidState;

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
			if (!(getThis() instanceof PlayerEntity)) pushingStrength = pushingStrength.normalize();

			//Apply the fluid viscosity to calc the pushing strength
			double viscosity = ((FabricFlowableFluid)firstTouchedFabricFluid.getFluid()).getViscosity(this.world, this.getThis());
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

	@Inject(method = "updateSubmergedInWaterState()V", at = @At("HEAD"), cancellable = true)
	private void updateSubmergedInFluidState(CallbackInfo ci) {
		//todo maybe this method rewriting is unnecessary!
		this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
		this.submergedInFabricFluid = FluidUtils.isFabricFluid(submergedFluid);
		this.submergedFluidTag = null;
		this.submergedFluid = null;

		//Get the y of the center of the eye
		double eyeY = this.getEyeY() - 0.1111111119389534d;

		//If the entity is on a boat, not set the tag regardless
		if (this.getVehicle() instanceof BoatEntity boat) {
			if (!boat.isSubmergedInWater() && boat.getBoundingBox().maxY >= eyeY && boat.getBoundingBox().minY <= eyeY) {
				return;
			}
		}

		//Get the tag of the fluid in the eye block
		BlockPos pos = new BlockPos(this.getX(), eyeY, this.getZ());
		FluidState fluidState = this.world.getFluidState(pos);
		Tag<Fluid> tag = FluidTags.getTags().stream().filter(fluidState::isIn).findFirst().orElse(null);

		if (tag != null) {
			double eyeFluidY = (float)pos.getY() + fluidState.getHeight(this.world, pos);
			if (eyeFluidY > eyeY) {
				//If the entity is submerged by the fluid above the eye, set the current "submerged fluid"
				this.submergedFluidTag = tag;
				this.submergedFluid = fluidState;
			}
		} else if (FluidUtils.isFabricFluid(fluidState)) {
			double eyeFluidY = (float)pos.getY() + fluidState.getHeight(this.world, pos);
			if (eyeFluidY > eyeY) {
				//If the entity is submerged by the fluid above the eye, set the current "submerged fluid"
				this.submergedFluid = fluidState;
			}
		}

		ci.cancel();
	}

	//endregion

	//region TOUCHING AND SUBMERGED EVENTS

	@Unique
	private void onFabricFluidTouched(FabricFlowableFluid fluid) {
		//This is not executed on spectator mode, and for exp orbs
		if (!this.isSpectator() && !(this.getThis() instanceof ExperienceOrbEntity)) {
			//Eecute the splash event
			fluid.onSplash(this.world, this.getThis());
		}
	}

	@Inject(method = "tick()V", at = @At("TAIL"))
	private void tick(CallbackInfo ci) {
		if (isSubmergedInFabricFluid() && submergedFluid != null
				&& submergedFluid.getFluid() instanceof FabricFlowableFluid fluid) {
			//Executes an event if the entity is submerged in a fabric_fluid
			fluid.onSubmerged(world, this.getThis());
		}
		if (isTouchingFabricFluid() && firstTouchedFabricFluid.getFluid() instanceof FabricFlowableFluid fluid) {
			//Executes an event if the entity is touching a fabric_fluid
			fluid.onTouching(world, this.getThis());
		}
	}

	//endregion

	//region STATE CHECKERS

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
		return this.isSubmergedInWater() || (this.isSubmergedInFabricFluid()
				&& submergedFluid != null && FluidUtils.isSwimmable(submergedFluid));
	}

	@Override
	public boolean isTouchingSwimmableFluid() {
		return this.isTouchingWater() || (this.isTouchingFabricFluid() && FluidUtils.isSwimmable(firstTouchedFabricFluid));
	}

	@Override
	public double getFabricFluidHeight() {
		return this.fabricFluidHeight;
	}

	//endregion


	@Unique
	private Entity getThis() {
		return (Entity)(Object)this;
	}
}
