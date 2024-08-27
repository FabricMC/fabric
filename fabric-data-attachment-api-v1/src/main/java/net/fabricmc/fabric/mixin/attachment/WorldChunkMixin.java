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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.impl.attachment.BlockEntityAttachmentReceiver;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPayload;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;
import net.fabricmc.fabric.impl.attachment.sync.SyncType;

@Mixin(WorldChunk.class)
abstract class WorldChunkMixin extends AttachmentTargetsMixin implements AttachmentTargetImpl, BlockEntityAttachmentReceiver {
	@Shadow
	@Final
	private World world;
	@Unique
	private Map<BlockPos, Map<AttachmentType<?>, AttachmentChange>> alwaysSentToNewcomersBE = new HashMap<>();
	@Unique
	private Map<BlockPos, Map<AttachmentType<?>, AttachmentChange>> maybeSentToNewcomersBE = new HashMap<>();

	@Inject(method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/ProtoChunk;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;)V", at = @At("TAIL"))
	private void transferProtoChunkAttachement(ServerWorld world, ProtoChunk protoChunk, WorldChunk.EntityLoader entityLoader, CallbackInfo ci) {
		AttachmentTargetImpl.transfer(protoChunk, this, false);
	}

	@Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;markRemoved()V"))
	private void removeBlockEntityAttachments(BlockPos pos, CallbackInfo ci) {
		alwaysSentToNewcomersBE.remove(pos);
		maybeSentToNewcomersBE.remove(pos);
	}

	@Override
	public void fabric_acknowledgeBlockEntityAttachment(BlockPos pos, AttachmentType<?> type, @Nullable Object value) {
		SyncType syncType = ((AttachmentTypeImpl<?>) type).syncType();

		if (syncType == SyncType.TARGET_ONLY) {
			return;
		}

		Map<BlockPos, Map<AttachmentType<?>, AttachmentChange>> globalMap = syncType == SyncType.CUSTOM
				? maybeSentToNewcomersBE
				: alwaysSentToNewcomersBE;
		var targetInfo = new AttachmentTargetInfo.BlockEntityTarget(pos);

		if (value == null && globalMap.containsKey(pos)) {
			Map<AttachmentType<?>, AttachmentChange> map = globalMap.get(pos);
			map.remove(type);

			if (map.isEmpty()) {
				globalMap.remove(pos);
			}
		} else if (value != null) {
			globalMap.computeIfAbsent(pos, k -> new IdentityHashMap<>())
					.put(type, new AttachmentChange(targetInfo, type, value));
		}
	}

	@Override
	@Nullable
	public AttachmentSyncPayload fabric_getInitialSyncPayload(ServerPlayerEntity player) {
		AttachmentSyncPayload chunkPayload = super.fabric_getInitialSyncPayload(player);
		List<AttachmentChange> changes = chunkPayload == null ? new ArrayList<>() : chunkPayload.attachments();

		this.alwaysSentToNewcomersBE.values().forEach(m -> changes.addAll(m.values()));
		this.maybeSentToNewcomersBE.forEach((blockPos, map) -> {
			for (Map.Entry<AttachmentType<?>, AttachmentChange> entry : map.entrySet()) {
				BiPredicate<AttachmentTarget, ServerPlayerEntity> pred = ((AttachmentTypeImpl<?>) entry.getKey()).customSyncTargetTest();
				// trySync type should always be CUSTOM here
				assert pred != null;

				if (pred.test(this, player)) {
					changes.add(entry.getValue());
				}
			}
		});

		return new AttachmentSyncPayload(changes);
	}

	@Override
	public void fabric_syncChange(AttachmentType<?> type, AttachmentSyncPayload payload) {
		if (this.world instanceof ServerWorld serverWorld) {
			switch (((AttachmentTypeImpl<?>) type).syncType()) {
			case ALL, ALL_BUT_TARGET -> PlayerLookup
					// Can't shadow the method or field as we are already extending a supermixin
					.tracking(serverWorld, ((Chunk) (Object) this).getPos())
					.forEach(player -> AttachmentSync.trySync(payload, player));
			case CUSTOM -> PlayerLookup
					.tracking(serverWorld, ((Chunk) (Object) this).getPos())
					.forEach(player -> {
						if (((AttachmentTypeImpl<?>) type).customSyncTargetTest().test(this, player)) {
							AttachmentSync.trySync(payload, player);
						}
					});
			case TARGET_ONLY -> {
			}
			case NONE -> throw new IllegalStateException();
			}
		}
	}
}
