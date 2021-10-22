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
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;
    @Shadow private BlockPos blockPos;
    @Shadow protected boolean firstUpdate;
    @Shadow public float fallDistance;
    @Shadow protected boolean touchingWater;
    protected boolean touchingDenseFluid;

    @Shadow @Nullable public abstract Entity getVehicle();
    @Shadow public abstract boolean updateMovementInFluid(Tag<Fluid> tag, double d);
    @Shadow public abstract void extinguish();
    //@Shadow protected abstract void onSwimmingStart();
    @Shadow public abstract void setSwimming(boolean swimming);
    @Shadow public abstract boolean isRegionUnloaded();
    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();

    @Inject(method = "checkWaterState()V",
            at = @At("HEAD"),
            cancellable = true)
    void checkWaterState(CallbackInfo ci) {
        if (this.getVehicle() instanceof BoatEntity) {
            this.touchingWater = false;
            this.touchingDenseFluid = false;
            ci.cancel();
        } else {
            double fluidDensity = getFluidDensity();
            if (fluidDensity != -1 && this.updateMovementInFluid(FabricFluidTags.FABRIC_FLUID, fluidDensity)) {
                if (!this.touchingDenseFluid && !this.firstUpdate) {
                    this.onDenseFluidTouched();
                    //this.onSwimmingStart();
                }

                this.fallDistance = 0.0F;
                this.touchingWater = true;
                this.touchingDenseFluid = true;
                this.extinguish();
                ci.cancel();
            }
            else {
                this.touchingDenseFluid = false;
            }
        }
    }

    @Inject(method = "updateSwimming()V",
            at = @At("HEAD"),
            cancellable = true)
    public void updateSwimming(CallbackInfo ci) {
        if (this.touchingDenseFluid) {
            setSwimming(false);
            ci.cancel();
        }
    }

    private double getFluidDensity() {
        if (this.isRegionUnloaded()) {
            return -1;
        } else {
            FluidState fluidState = this.world.getFluidState(this.blockPos);
            if (fluidState.getFluid() instanceof ExtendedFlowableFluid fluid) {
                return fluid.getStrength();
            }
            else return -1;
        }
    }

    private void onDenseFluidTouched() {
        FluidState fluidState = this.world.getFluidState(this.blockPos);
        if (fluidState.getFluid() instanceof ExtendedFlowableFluid fluid) {
            fluid.getSplashSound().ifPresent(soundEvent -> this.playSound(soundEvent, 1f, 1f));
            fluid.onSplash(this.world, new Vec3d(this.getX(), this.getY(), this.getZ()), (Entity)(Object)this);
        }
    }
}
