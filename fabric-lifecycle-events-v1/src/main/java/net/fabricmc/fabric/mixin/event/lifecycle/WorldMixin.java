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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Mixin(World.class)
public abstract class WorldMixin {
	@Shadow
	public abstract boolean isClient();

	@Shadow
	public abstract Profiler getProfiler();

	@Inject(method = "addBlockEntity", at = @At("TAIL"))
	protected void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		if (!this.isClient()) { // Only fire this event if we are a server world
			ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, (ServerWorld) (Object) this);
		}
	}

	// Mojang what hell, why do you need three ways to unload block entities
	@Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	protected void onUnloadBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity) {
		if (!this.isClient()) { // Only fire this event if we are a server world
			ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, (ServerWorld) (Object) this);
		}
	}

	@Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V")), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	protected void onRemoveBlockEntity(CallbackInfo ci, Profiler profiler, Iterator iterator, BlockEntity blockEntity) {
		if (!this.isClient()) {
			ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, (ServerWorld) (Object) this);
		}
	}

	@Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z", ordinal = 1))
	protected boolean onPurgeRemovedBlockEntities(List<BlockEntity> blockEntityList, Collection<BlockEntity> removals) {
		if (!this.isClient()) {
			for (BlockEntity removal : removals) {
				ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(removal, (ServerWorld) (Object) this);
			}
		}

		// Mimic vanilla logic
		return blockEntityList.removeAll(removals);
	}

	@Inject(at = @At("RETURN"), method = "tickBlockEntities")
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		if (!this.isClient()) {
			ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld) (Object) this);
		}
	}
}
