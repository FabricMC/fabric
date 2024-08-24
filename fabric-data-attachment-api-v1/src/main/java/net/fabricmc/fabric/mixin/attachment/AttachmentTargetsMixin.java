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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPayload;
import net.fabricmc.fabric.impl.attachment.sync.SyncType;

@Mixin({BlockEntity.class, Entity.class, World.class, Chunk.class})
abstract class AttachmentTargetsMixin implements AttachmentTargetImpl {
	@Nullable
	private IdentityHashMap<AttachmentType<?>, Object> fabric_dataAttachments = null;
	@Nullable
	private IdentityHashMap<AttachmentType<?>, AttachmentChange> fabric_globallySynced = null;
	@Nullable
	private IdentityHashMap<AttachmentType<?>, AttachmentChange> fabric_otherSynced = null;

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getAttached(AttachmentType<T> type) {
		return fabric_dataAttachments == null ? null : (T) fabric_dataAttachments.get(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T setAttached(AttachmentType<T> type, @Nullable T value) {
		this.fabric_markChanged(type);

		if (type.isSynced()) {
			this.fabric_acknowledgeSyncedEntry(type, value);
			var payload = new AttachmentSyncPayload(List.of(new AttachmentChange(
					fabric_getSyncTargetInfo(),
					type,
					value
			)));
			this.fabric_syncChange(type, payload);
		}

		if (value == null) {
			if (fabric_dataAttachments == null) {
				return null;
			}

			T removed = (T) fabric_dataAttachments.remove(type);

			if (fabric_dataAttachments.isEmpty()) {
				fabric_dataAttachments = null;
			}

			return removed;
		} else {
			if (fabric_dataAttachments == null) {
				fabric_dataAttachments = new IdentityHashMap<>();
			}

			return (T) fabric_dataAttachments.put(type, value);
		}
	}

	@Override
	public boolean hasAttached(AttachmentType<?> type) {
		return fabric_dataAttachments != null && fabric_dataAttachments.containsKey(type);
	}

	@Override
	public void fabric_writeAttachmentsToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
		AttachmentSerializingImpl.serializeAttachmentData(nbt, wrapperLookup, fabric_dataAttachments);
	}

	@Override
	public void fabric_readAttachmentsFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
		fabric_dataAttachments = AttachmentSerializingImpl.deserializeAttachmentData(nbt, wrapperLookup);
	}

	@Override
	public boolean fabric_hasPersistentAttachments() {
		return AttachmentSerializingImpl.hasPersistentAttachments(fabric_dataAttachments);
	}

	@Override
	public Map<AttachmentType<?>, ?> fabric_getAttachments() {
		return fabric_dataAttachments;
	}

	@Override
	public void fabric_acknowledgeSyncedEntry(AttachmentType<?> type, @Nullable Object value) {
		if (value == null) {
			if (((AttachmentTypeImpl<?>) type).syncType() == SyncType.ALL) {
				if (fabric_globallySynced == null) {
					return;
				}

				fabric_globallySynced.remove(type);

				if (fabric_globallySynced.isEmpty()) {
					fabric_globallySynced = null;
				}
			} else {
				if (fabric_otherSynced == null) {
					return;
				}

				fabric_otherSynced.remove(type);

				if (fabric_otherSynced.isEmpty()) {
					fabric_otherSynced = null;
				}
			}
		} else {
			AttachmentChange change = new AttachmentChange(fabric_getSyncTargetInfo(), type, value);

			if (((AttachmentTypeImpl<?>) type).syncType() == SyncType.ALL) {
				if (fabric_globallySynced == null) {
					fabric_globallySynced = new IdentityHashMap<>();
				}

				fabric_globallySynced.put(type, change);
			} else {
				if (fabric_otherSynced == null) {
					fabric_otherSynced = new IdentityHashMap<>();
				}

				fabric_otherSynced.put(type, change);
			}
		}
	}

	@Override
	@Nullable
	public AttachmentSyncPayload fabric_getInitialSyncPayload(ServerPlayerEntity player) {
		if (fabric_dataAttachments == null || fabric_globallySynced == null && fabric_otherSynced == null) {
			return null;
		}

		List<AttachmentChange> list = new ArrayList<>();

		if (fabric_globallySynced != null) {
			list.addAll(fabric_globallySynced.values());
		}

		if (fabric_otherSynced != null) {
			for (Map.Entry<AttachmentType<?>, AttachmentChange> entry : fabric_otherSynced.entrySet()) {
				AttachmentType<?> type = entry.getKey();
				boolean add;

				switch (((AttachmentTypeImpl<?>) type).syncType()) {
				case TARGET_ONLY -> add = (Object) this == player;
				case ALL_BUT_TARGET -> add = (Object) this != player;
				case CUSTOM -> add = ((AttachmentTypeImpl<?>) type).customSyncTargetTest().test(this, player);
				default -> throw new IllegalStateException();
				}

				if (add) {
					list.add(entry.getValue());
				}
			}
		}

		return new AttachmentSyncPayload(list);
	}
}
