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

import java.util.function.BooleanSupplier;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	@Shadow
	@Final
	private MinecraftServer server;

	// Make sure "insideBlockTick" is true before we call the start tick, so inject after it is set
	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;inBlockTick:Z", opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
	private void startWorldTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		ServerTickEvents.START_WORLD_TICK.invoker().onStartTick((ServerWorld) (Object) this);
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void endWorldTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld) (Object) this);
	}

	@Inject(method = "tickChunk", at = @At("RETURN"))
	private void tick(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		ServerChunkEvents.CHUNK_TICK.invoker().onChunkTick((ServerWorld) (Object) this, chunk);
	}

	@Inject(method = "tickEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
	private void tick(Entity entity, CallbackInfo ci) {
		ServerEntityEvents.ENTITY_TICK.invoker().onTick(entity, (ServerWorld) (Object) this);
	}

	@Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickRiding()V", shift = At.Shift.AFTER))
	private void tickRiding(Entity vehicle, Entity passenger, CallbackInfo ci) {
		ServerEntityEvents.ENTITY_TICK.invoker().onTick(passenger, (ServerWorld) (Object) this);
	}
}
