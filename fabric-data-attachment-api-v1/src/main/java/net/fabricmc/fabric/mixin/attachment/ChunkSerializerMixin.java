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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.poi.PointOfInterestStorage;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

/**
 * @author Technici4an
 */
@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
	@ModifyExpressionValue(
			at = @At(
					value = "NEW",
					target = "net/minecraft/world/chunk/WorldChunk"
			),
			method = "deserialize"
	)
	private static WorldChunk injectReadNbt(WorldChunk chunk, ServerWorld world, PointOfInterestStorage poiStorage, ChunkPos chunkPos, NbtCompound nbt) {
		((AttachmentTargetImpl) chunk).fabric_readAttachmentsFromNbt(nbt);
		return chunk;
	}

	@Inject(
			at = @At("RETURN"),
			method = "serialize"
	)
	private static void injectWriteNbt(ServerWorld world, Chunk chunk, CallbackInfoReturnable<NbtCompound> cir) {
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
			((AttachmentTargetImpl) chunk).fabric_writeAttachmentsToNbt(cir.getReturnValue());
		}
	}
}
