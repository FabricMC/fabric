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

package net.fabricmc.fabric.mixin.attachment;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;

@Mixin(WorldChunk.class)
abstract class WorldChunkMixin extends AttachmentTargetsMixin implements AttachmentTargetImpl {
	@Shadow
	@Final
	World world;

	@Shadow
	public abstract Map<BlockPos, BlockEntity> getBlockEntities();

	@Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V", at = @At("TAIL"))
	private void transferProtoChunkAttachement(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
		AttachmentTargetImpl.transfer(protoChunk, this, false);
	}

	@Override
	public void fabric_getInitialSyncChanges(ServerPlayerEntity player, Consumer<AttachmentChange> changeOutput) {
		super.fabric_getInitialSyncChanges(player, changeOutput);

		for (BlockEntity be : this.getBlockEntities().values()) {
			((AttachmentTargetImpl) be).fabric_getInitialSyncChanges(player, changeOutput);
		}
	}

	@Override
	public Iterable<ServerPlayerEntity> fabric_getTracking() {
		if (this.world instanceof ServerWorld serverWorld) {
			return PlayerLookup.tracking(serverWorld, ((Chunk) (Object) this).getPos());
		} else {
			return List.of();
		}
	}
}
