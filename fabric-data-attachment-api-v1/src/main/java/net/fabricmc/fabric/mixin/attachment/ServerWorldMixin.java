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

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.attachment.AttachmentPersistentState;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPayload;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;

@Mixin(ServerWorld.class)
abstract class ServerWorldMixin extends World implements AttachmentTargetImpl {
	@Shadow
	@Final
	private MinecraftServer server;

	protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
		super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void createAttachmentsPersistentState(CallbackInfo ci) {
		// Force persistent state creation
		ServerWorld world = (ServerWorld) (Object) this;
		var type = new PersistentState.Type<>(
				() -> new AttachmentPersistentState(world),
				(nbt, wrapperLookup) -> AttachmentPersistentState.read(world, nbt, server.getRegistryManager()),
				null // Object builder API 12.1.0 and later makes this a no-op
		);
		world.getPersistentStateManager().getOrCreate(type, AttachmentPersistentState.ID);
	}

	@Override
	public AttachmentTargetInfo<?> fabric_getSyncTargetInfo() {
		return AttachmentTargetInfo.WorldTarget.INSTANCE;
	}

	@Override
	public void fabric_syncChange(AttachmentType<?> type, AttachmentSyncPayload payload) {
		switch (((AttachmentTypeImpl<?>) type).syncType()) {
		case ALL, ALL_BUT_TARGET -> PlayerLookup
				// Can't shadow the method or field as we are already extending a supermixin
				.world((ServerWorld) (Object) this)
				.forEach(player -> AttachmentSyncImpl.trySync(payload, player));
		case CUSTOM -> PlayerLookup
				.world((ServerWorld) (Object) this)
				.forEach(player -> {
					if (((AttachmentTypeImpl<?>) type).customSyncTargetTest().test(this, player)) {
						AttachmentSyncImpl.trySync(payload, player);
					}
				});
		case TARGET_ONLY -> {
		}
		case NONE -> throw new IllegalStateException();
		}
	}
}
