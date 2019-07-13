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
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {

	private ServerPlayerEntityMixin(EntityType<?> type, World world) {
		super(type, world);
		throw new UnsupportedOperationException();
	}

	@Inject(at = @At("HEAD"), method = "changeDimension", cancellable = true)
	private void changeDimension(final DimensionType targetType, final CallbackInfoReturnable<Entity> info) {
		ActionResult result = DimensionTeleportCallback.EVENT.invoker().placeEntity((ServerPlayerEntity) (Object) this, ((ServerPlayerEntity) (Object) this).dimension, targetType);
		if(result == ActionResult.FAIL) {
			info.setReturnValue(this);
		}

		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		DimensionType previousType = player.dimension;

		// going to a FabricDimensionType
		if(targetType instanceof FabricDimensionType) {
			FabricEntityTeleporter.changeDimension((ServerPlayerEntity) (Object) this, targetType, ((FabricDimensionType) targetType).getEntryPlacement());
			info.setReturnValue(this);
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
