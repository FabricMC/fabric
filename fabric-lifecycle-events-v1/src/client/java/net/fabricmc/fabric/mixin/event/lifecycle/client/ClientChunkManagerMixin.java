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

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;

@Mixin(ClientChunkManager.class)
public abstract class ClientChunkManagerMixin {
	@Final
	@Shadow
	private ClientWorld world;

	@Inject(method = "loadChunkFromPacket", at = @At("TAIL"))
	private void onChunkLoad(int x, int z, PacketByteBuf packetByteBuf, NbtCompound nbtCompound, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> info) {
		ClientChunkEvents.CHUNK_LOAD.invoker().onChunkLoad(this.world, info.getReturnValue());
	}

	@Inject(method = "loadChunkFromPacket", at = @At(value = "NEW", target = "net/minecraft/world/chunk/WorldChunk", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onChunkUnload(int x, int z, PacketByteBuf buf, NbtCompound tag, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> info, int index, WorldChunk worldChunk, ChunkPos chunkPos) {
		if (worldChunk != null) {
			ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(this.world, worldChunk);
		}
	}

	@Inject(method = "unload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientChunkManager$ClientChunkMap;compareAndSet(ILnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/WorldChunk;)Lnet/minecraft/world/chunk/WorldChunk;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onChunkUnload(int chunkX, int chunkZ, CallbackInfo ci, int i, WorldChunk chunk) {
		ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(this.world, chunk);
	}

	@Inject(
			method = "updateLoadDistance",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/world/ClientChunkManager$ClientChunkMap.isInRadius(II)Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onUpdateLoadDistance(int loadDistance, CallbackInfo ci, int oldRadius, int newRadius, ClientChunkManager.ClientChunkMap clientChunkMap, int k, WorldChunk oldChunk, ChunkPos chunkPos) {
		if (!clientChunkMap.isInRadius(chunkPos.x, chunkPos.z)) {
			ClientChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(this.world, oldChunk);
		}
	}
}
