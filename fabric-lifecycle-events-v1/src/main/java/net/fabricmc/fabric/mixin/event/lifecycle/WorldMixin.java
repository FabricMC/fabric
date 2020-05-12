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

package net.fabricmc.fabric.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Mixin(World.class)
public abstract class WorldMixin {
	@Shadow
	public abstract boolean isClient();

	@Inject(method = "addBlockEntity", at = @At("TAIL"))
	protected void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		if (!this.isClient()) { // Only fire this event if we are a server world
			ServerLifecycleEvents.BLOCK_ENTITY_LOAD.invoker().onLoadBlockEntity(blockEntity, (ServerWorld) (Object) this);
		}
	}

	@Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	protected void onUnloadBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity) {
		if (!this.isClient()) { // Only fire this event if we are a server world
			ServerLifecycleEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnloadBlockEntity(blockEntity, (ServerWorld) (Object) this);
		}
	}

	@Inject(at = @At("RETURN"), method = "tickBlockEntities")
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		if (!this.isClient()) {
			ServerLifecycleEvents.WORLD_TICK.invoker().onTick((ServerWorld) (Object) this);
		}
	}
}
