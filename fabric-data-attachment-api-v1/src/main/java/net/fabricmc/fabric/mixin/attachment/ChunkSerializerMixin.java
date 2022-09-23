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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.poi.PointOfInterestStorage;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/world/chunk/WorldChunk.<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/tick/ChunkTickScheduler;Lnet/minecraft/world/tick/ChunkTickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Lnet/minecraft/world/chunk/WorldChunk$EntityLoader;Lnet/minecraft/world/gen/chunk/BlendingData;)V",
					shift = At.Shift.BY,
					by = 2
			),
			method = "deserialize",
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void injectReadNbt(ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt, CallbackInfoReturnable<ProtoChunk> cir, UpgradeData upgradeData, boolean bl, NbtList nbtList, int i, ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider, Registry<?> registry, long m, ChunkStatus.ChunkType chunkType, BlendingData blendingData, Chunk chunk) {
		if (chunk instanceof WorldChunk) {
			((AttachmentTargetImpl) chunk).readAttachmentsFromNbt(WorldChunk.class, nbt);
		} else {
			throw new AssertionError("chunk is not a WorldChunk! This is not supposed to happen!");
		}
	}

	@Inject(
			at = @At("RETURN"),
			method = "serialize"
	)
	private static void injectWriteNbt(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
			((AttachmentTargetImpl) chunk).writeAttachmentsToNbt(cir.getReturnValue());
		}
	}
}
