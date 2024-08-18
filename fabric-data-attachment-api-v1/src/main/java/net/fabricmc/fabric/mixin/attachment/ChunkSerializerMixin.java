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

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.world.chunk.Chunk;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(ChunkSerializer.class)
abstract class ChunkSerializerMixin {
	// Adding a mutable record field like this is likely a bad idea, but I cannot see a better way.
	@Unique
	@Nullable
	private NbtCompound attachmentNbtData;

	@Inject(method = "method_61794", at = @At("RETURN"))
	private static void storeAttachmentNbtData(HeightLimitView heightLimitView, DynamicRegistryManager dynamicRegistryManager, NbtCompound nbt, CallbackInfoReturnable<ChunkSerializer> cir, @Share("attachmentDataNbt") LocalRef<NbtCompound> attachmentDataNbt) {
		final ChunkSerializer serializer = cir.getReturnValue();

		if (serializer == null) {
			return;
		}

		if (nbt.contains(AttachmentTarget.NBT_ATTACHMENT_KEY, NbtElement.COMPOUND_TYPE)) {
			((ChunkSerializerMixin)(Object)serializer).attachmentNbtData = nbt.getCompound(AttachmentTarget.NBT_ATTACHMENT_KEY);
		}
	}

	@Inject(method = "deserialize", at = @At("RETURN"))
	private void setAttachmentDataInChunk(ServerWorld serverWorld, PointOfInterestStorage pointOfInterestStorage, StorageKey storageKey, ChunkPos chunkPos, CallbackInfoReturnable<ProtoChunk> cir) {
		ProtoChunk chunk = cir.getReturnValue();
		if (chunk != null && attachmentNbtData != null) {
			var nbt = new NbtCompound();
			nbt.put(AttachmentTarget.NBT_ATTACHMENT_KEY, attachmentNbtData);
			((AttachmentTargetImpl) chunk).fabric_readAttachmentsFromNbt(nbt, serverWorld.getRegistryManager());
		}
	}


	@Inject(method = "method_61793", at = @At("RETURN"))
	private static void storeAttachmentNbtData(ServerWorld world, Chunk chunk, CallbackInfoReturnable<ChunkSerializer> cir) {
		var nbt = new NbtCompound();
		((AttachmentTargetImpl) chunk).fabric_writeAttachmentsToNbt(nbt, world.getRegistryManager());
		((ChunkSerializerMixin)(Object)cir.getReturnValue()).attachmentNbtData = nbt.getCompound(AttachmentTarget.NBT_ATTACHMENT_KEY);
	}

	@Inject(method = "serialize", at = @At("RETURN"))
	private void writeChunkAttachments(CallbackInfoReturnable<NbtCompound> cir) {
		if (attachmentNbtData != null) {
			cir.getReturnValue().put(AttachmentTarget.NBT_ATTACHMENT_KEY, attachmentNbtData);
		}
	}
}
