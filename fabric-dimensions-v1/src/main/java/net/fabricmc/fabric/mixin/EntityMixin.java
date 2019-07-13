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

package net.fabricmc.fabric.mixin;

import net.fabricmc.fabric.api.dimension.FabricDimensionType;
import net.fabricmc.fabric.api.dimension.FabricEntityTeleporter;
import net.fabricmc.fabric.event.entity.DimensionTeleportCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow protected BlockPos lastPortalPosition;
    @Shadow public abstract Vec3d getRotationVector();
    @Shadow public abstract Direction getHorizontalFacing();
    @Shadow public abstract BlockPos getBlockPos();
	@Shadow protected Vec3d field_6020;
	@Shadow protected Direction field_6028;

	@Inject(at = @At("HEAD"), method = "changeDimension", cancellable = true)
    private void changeDimension(final DimensionType targetType, final CallbackInfoReturnable<Entity> info) {
        ActionResult result = DimensionTeleportCallback.EVENT.invoker().placeEntity((Entity) (Object) this, ((Entity) (Object) this).dimension, targetType);
        if(result == ActionResult.FAIL) {
            info.setReturnValue((Entity) (Object) this);
        }

        Entity entity = (Entity) (Object) this;
        DimensionType previousType = entity.dimension;

        // going to a FabricDimensionType
        if(targetType instanceof FabricDimensionType) {
            FabricEntityTeleporter.changeDimension(entity, targetType, ((FabricDimensionType) targetType).getEntryPlacement());
            info.setReturnValue((Entity) (Object) this);
        }

        // coming from a FabricDimensionType
        else if (previousType instanceof FabricDimensionType) {
            setDefaultPortalValues();
        }
    }

    /**
     * Sets values used by `PortalForcer#changeDimension` to prevent a NPE crash.
     */
	@Unique
	private void setDefaultPortalValues() {
		this.field_6020 = this.getRotationVector(); // lastPortalDirectionVector
		this.field_6028 = this.getHorizontalFacing(); // lastPortalDirection
		this.lastPortalPosition = this.getBlockPos();
	}
}
