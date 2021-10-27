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

import net.fabricmc.fabric.api.fluid.v1.ExtendedFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements FabricFluidEntity {
    @Shadow public World world;
    @Shadow private BlockPos blockPos;
    @Shadow public float fallDistance;
	@Shadow protected boolean firstUpdate;
	@Shadow protected boolean submergedInWater;
	@Shadow @Nullable protected Tag<Fluid> submergedFluidTag;
	@Shadow public boolean horizontalCollision;

	@Unique protected boolean touchingFabricFluid = false;
	@Unique protected boolean submergedInFabricFluid = false;
	@Unique protected final double CENTER_EYE_OFFSET = 0.1111111119389534d;

	@Shadow @Nullable public abstract Entity getVehicle();
    @Shadow public abstract boolean updateMovementInFluid(Tag<Fluid> tag, double d);
    @Shadow public abstract void extinguish();
    @Shadow public abstract double getX();
    @Shadow public abstract double getZ();
	@Shadow public abstract double getEyeY();
	@Shadow public abstract boolean isSubmergedIn(Tag<Fluid> fluidTag);
	@Shadow public abstract boolean isSpectator();
	@Shadow public abstract Vec3d getPos();
	@Shadow public abstract double getY();
	@Shadow public abstract BlockPos getBlockPos();
	@Shadow public abstract boolean isSprinting();
	@Shadow	public abstract void updateVelocity(float speed, Vec3d movementInput);
	@Shadow public abstract void move(MovementType movementType, Vec3d movement);
	@Shadow public abstract Vec3d getVelocity();
	@Shadow public abstract void setVelocity(Vec3d velocity);
	@Shadow public abstract boolean doesNotCollide(double offsetX, double offsetY, double offsetZ);
	@Shadow public abstract boolean isLogicalSideForUpdatingMovement();
	@Shadow protected abstract void playStepSound(BlockPos pos, BlockState state);
	@Shadow protected abstract void playExtinguishSound();
	@Shadow public abstract void emitGameEvent(GameEvent event);
	@Shadow public abstract boolean isOnFire();

	@Inject(method = "tick()V", at = @At("TAIL"))
	private void tick(CallbackInfo ci) {
		fabricFluidTick();
	}

	@Inject(method = "shouldSpawnSprintingParticles()Z", at = @At("HEAD"), cancellable = true)
	private void shouldSpawnSprintingParticles(CallbackInfoReturnable<Boolean> cir) {
		//Don't spawn sprinting particles if the entity is touching a fabric_fluid
		if (this.isTouchingFabricFluid()) cir.setReturnValue(false);
	}

	@Redirect(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;playStepSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"))
	private void playStepSoundHandler(Entity entity, BlockPos pos, BlockState state) {
		//Don't play step sounds if the entity is touching a fabric_fluid
		if (!this.isTouchingFabricFluid()) this.playStepSound(pos, state);
	}

	@Redirect(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;emitGameEvent(Lnet/minecraft/world/event/GameEvent;)V"))
	private void emitGameEventHandler(Entity entity, GameEvent event) {
		//Emit the swim event if the entity is touching a fabric_fluid
		if (!this.isTouchingFabricFluid()) this.emitGameEvent(event);
		else this.emitGameEvent(GameEvent.SWIM);
	}

	@Inject(method = "updateSubmergedInWaterState()V", at = @At("HEAD"), cancellable = true)
	private void updateSubmergedInFluidState(CallbackInfo ci) {
		this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
		this.submergedInFabricFluid = this.isSubmergedIn(FabricFluidTags.FABRIC_FLUID);
		this.submergedFluidTag = null;

		//Get the y of the center of the eye
		double eyeY = this.getEyeY() - CENTER_EYE_OFFSET;

		//If the entity is on a boat, not set the tag regardless
		if (this.getVehicle() instanceof BoatEntity boat) {
			if (!boat.isSubmergedInWater() && boat.getBoundingBox().maxY >= eyeY && boat.getBoundingBox().minY <= eyeY) {
				return;
			}
		}

		//Get the tag of the fluid in the eye block
		BlockPos pos = new BlockPos(this.getX(), eyeY, this.getZ());
		FluidState fluidState = this.world.getFluidState(pos);
		Tag<Fluid> tag = FabricFluidTags.getFluidTags().stream().filter(fluidState::isIn).findFirst().orElse(null);

		if (tag != null) {
			double eyeFluidY = (float)pos.getY() + fluidState.getHeight(this.world, pos);
			if (eyeFluidY > eyeY) {
				//If the entity is submerged by the fluid above the eye, set the current "submerged fluid" tag
				this.submergedFluidTag = tag;
			}
		}

		ci.cancel();
	}

	@Inject(method = "baseTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateWaterState()Z", shift = At.Shift.AFTER))
	private void updateFabricFluidState(CallbackInfo ci) {
		this.checkFabricFluidState();
	}

	@Unique
	protected void checkFabricFluidState() {
		if (this.getVehicle() instanceof BoatEntity) {
			//If the entity is on a boat, not touches the fluid
			this.touchingFabricFluid = false;
		} else if (this.world.getFluidState(this.blockPos).getFluid() instanceof ExtendedFlowableFluid fluid) {
			//Get the fluid viscosity, that is equal to the pushing strength of the fluid
			double viscosity = fluid.getViscosity(this.world, this.getPos(), this.getThis());

			//updateMovementInFluid returns true if the entity is currently touching the fluid
			if (this.updateMovementInFluid(FabricFluidTags.FABRIC_FLUID, viscosity)) {
				//If the entity, in the previous tick, not touches the fluid, and this is not the first update,
				//execute the "fluid touched" event
				if (!this.touchingFabricFluid && !this.firstUpdate) this.onFabricFluidTouched(fluid);

				//Prevent fall damage and extinguish fire
				if (fluid.canPreventFallDamage()) this.fallDistance = 0.0F;
				if (fluid.canExtinguishFire() && isOnFire()) {
					this.extinguish();
					this.playExtinguishSound();
				}

				this.touchingFabricFluid = true;
			} else {
				this.touchingFabricFluid = false;
			}
		} else {
			this.touchingFabricFluid = false;
		}
	}

	/**
	 * @return true if the entity is touching a fabric_fluid.
	 */
	@Override
	public boolean isTouchingFabricFluid() {
		return touchingFabricFluid;
	}

	/**
	 * @return true if the entity is submerged in a fabric_fluid.
	 */
	@Override
	public boolean isSubmergedInFabricFluid() {
		return submergedInFabricFluid;
	}

	@Unique
	private void onFabricFluidTouched(ExtendedFlowableFluid fluid) {
		//This is not executed on spectator mode, and for exp orbs
		if (!this.isSpectator() && !(this.getThis() instanceof ExperienceOrbEntity)) {
			//Eecute the splash event
			fluid.onSplash(this.world, this.getPos(), this.getThis());
		}
	}

	@Unique
	private void fabricFluidTick() {
		if (this.world.getFluidState(this.blockPos).getFluid() instanceof ExtendedFlowableFluid fluid
				&& this.isSubmergedIn(FabricFluidTags.FABRIC_FLUID)) {
			//Executes an event if the entity is submerged in a fabric_fluid
			fluid.onSubmerged(world, this.getThis());
		}
	}

	@Unique
	private Entity getThis() {
		return (Entity)(Object)this;
	}
}
