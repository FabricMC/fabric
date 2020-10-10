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

package net.fabricmc.fabric.mixin.event.lifecycle.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.mixin.event.lifecycle.WorldMixin;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends WorldMixin {
	// Call our load event after vanilla has loaded the entity
	@Inject(method = "addEntityPrivate", at = @At("TAIL"))
	private void onEntityLoad(int id, Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_LOAD.invoker().onLoad(entity, (ClientWorld) (Object) this);
	}

	// Call our unload event before vanilla does.
	@Inject(method = "finishRemovingEntity", at = @At("HEAD"))
	private void onEntityUnload(Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_UNLOAD.invoker().onUnload(entity, (ClientWorld) (Object) this);
	}

	// We override our injection on the clientworld so only the client's block entity invocations will run
	@Override
	protected void onLoadBlockEntity(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.invoker().onLoad(blockEntity, (ClientWorld) (Object) this);
	}

	// We override our injection on the clientworld so only the client's block entity invocations will run
	@Override
	protected void onUnloadBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity) {
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, (ClientWorld) (Object) this);
	}

	@Override
	protected void onRemoveBlockEntity(CallbackInfo ci, Profiler profiler, Iterator iterator, BlockEntity blockEntity) {
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(blockEntity, (ClientWorld) (Object) this);
	}

	@Override
	protected boolean onPurgeRemovedBlockEntities(List<BlockEntity> blockEntityList, Collection<BlockEntity> removals) {
		for (BlockEntity removal : removals) {
			ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.invoker().onUnload(removal, (ClientWorld) (Object) this);
		}

		return super.onPurgeRemovedBlockEntities(blockEntityList, removals); // Call super
	}

	// We override our injection on the clientworld so only the client world's tick invocations will run
	@Override
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		ClientTickEvents.END_WORLD_TICK.invoker().onEndTick((ClientWorld) (Object) this);
	}

	@Inject(method = "tickEntities", at = @At("HEAD"))
	private void startWorldTick(CallbackInfo ci) {
		ClientTickEvents.START_WORLD_TICK.invoker().onStartTick((ClientWorld) (Object) this);
	}
}
