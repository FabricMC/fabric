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

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.chunk.BlendingData;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.ChunkInitialAttachmentDataImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.ChunkAttachmentChangePayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.ChunkAttachmentInitialSyncPayloadS2C;

@Mixin(WorldChunk.class)
abstract class WorldChunkMixin extends Chunk implements AttachmentTargetImpl, ChunkInitialAttachmentDataImpl {
	@Shadow
	@Final
	private World world;

	WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
		super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
	}

	@Inject(
			method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V",
			at = @At("TAIL")
	)
	public void transferProtoChunkAttachement(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
		AttachmentTargetImpl.transfer(protoChunk, this, false);
	}

	@Override
	public ChunkAttachmentInitialSyncPayloadS2C fabric_createPayload(ServerPlayerEntity player) {
		// naive code, TODO optimize
		return new ChunkAttachmentInitialSyncPayloadS2C(
				this.pos,
				this.fabric_getInitialAttachmentsFor(player),
				Maps.transformValues(
						this.blockEntities,
						be -> ((AttachmentTargetImpl) be).fabric_getInitialAttachmentsFor(player)
				)
		);
	}

	@Override
	public void fabric_syncChange(AttachmentType<?> type, Object change) {
		if (this.world instanceof ServerWorld serverWorld) {
			var payload = new ChunkAttachmentChangePayloadS2C(this.pos, new AttachmentChange(type, change));

			PlayerLookup.tracking(serverWorld, this.pos)
					.forEach(player -> AttachmentSync.syncIfPossible(payload, type, this, player));
		}
	}
}
