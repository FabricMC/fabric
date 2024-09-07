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
import java.util.function.BiPredicate;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.SyncType;
import net.fabricmc.fabric.impl.attachment.sync.s2c.AttachmentSyncPayload;

@Mixin({BlockEntity.class, Entity.class, World.class, Chunk.class})
abstract class AttachmentTargetsMixin implements AttachmentTargetImpl {
	@Nullable
	private IdentityHashMap<AttachmentType<?>, Object> fabric_dataAttachments = null;
	/*
	 * All the attachment changes that should always be sent to newcomers, players that begin to track this target
	 */
	@Nullable
	private IdentityHashMap<AttachmentType<?>, AttachmentChange> fabric_alwaysSentToNewcomers = null;
	/*
	 * Same as above, except that the changes might not be sent to any newcomer, needs to be checked before sending
	 */
	@Nullable
	private IdentityHashMap<AttachmentType<?>, AttachmentChange> fabric_maybeSentToNewcomers = null;

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
			var payload = new AttachmentSyncPayload(List.of(AttachmentChange.create(
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

	@SuppressWarnings("unchecked")
	@Override
	public void fabric_readAttachmentsFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapperLookup) {
		Map<AttachmentType<?>, Object> deserialized = AttachmentSerializingImpl.deserializeAttachmentData(nbt, wrapperLookup);

		if (deserialized != null) {
			// calling setAttached so that sync logic runs properly
			// takes care of the case of targetOnly() attachments that would have no opportunity to sync
			for (Map.Entry<AttachmentType<?>, Object> entry : deserialized.entrySet()) {
				setAttached((AttachmentType<Object>) entry.getKey(), entry.getValue());
			}
		}
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
		SyncType syncType = ((AttachmentTypeImpl<?>) type).syncPredicate().type();

		if (syncType == SyncType.TARGET_ONLY) {
			// the target can never be a newcomer (i.e. start tracking itself), so this never needs to be synced
			return;
		}

		if (syncType == SyncType.CUSTOM) {
			if (value == null) {
				if (fabric_maybeSentToNewcomers == null) {
					return;
				}

				fabric_maybeSentToNewcomers.remove(type);

				if (fabric_maybeSentToNewcomers.isEmpty()) {
					fabric_maybeSentToNewcomers = null;
				}
			} else {
				AttachmentChange change = AttachmentChange.create(fabric_getSyncTargetInfo(), type, value);

				if (fabric_maybeSentToNewcomers == null) {
					fabric_maybeSentToNewcomers = new IdentityHashMap<>();
				}

				fabric_maybeSentToNewcomers.put(type, change);
			}
		} else {
			/*
			 * covers both ALL and ALL_BUT_TARGET: the target is never a newcomer,
			 * so it *always* needs to be synced in the second case
			 */
			if (value == null) {
				if (fabric_alwaysSentToNewcomers == null) {
					return;
				}

				fabric_alwaysSentToNewcomers.remove(type);

				if (fabric_alwaysSentToNewcomers.isEmpty()) {
					fabric_alwaysSentToNewcomers = null;
				}
			} else {
				AttachmentChange change = AttachmentChange.create(fabric_getSyncTargetInfo(), type, value);

				if (fabric_alwaysSentToNewcomers == null) {
					fabric_alwaysSentToNewcomers = new IdentityHashMap<>();
				}

				fabric_alwaysSentToNewcomers.put(type, change);
			}
		}
	}

	@Override
	@Nullable
	public List<AttachmentChange> fabric_getInitialSyncChanges(ServerPlayerEntity player) {
		if (fabric_dataAttachments == null || fabric_alwaysSentToNewcomers == null && fabric_maybeSentToNewcomers == null) {
			return null;
		}

		List<AttachmentChange> list = new ArrayList<>();

		if (fabric_alwaysSentToNewcomers != null) {
			list.addAll(fabric_alwaysSentToNewcomers.values());
		}

		if (fabric_maybeSentToNewcomers != null) {
			for (Map.Entry<AttachmentType<?>, AttachmentChange> entry : fabric_maybeSentToNewcomers.entrySet()) {
				BiPredicate<AttachmentTarget, ServerPlayerEntity> pred =
						((AttachmentTypeImpl<?>) entry.getKey()).syncPredicate().customTest();
				// trySync type should always be CUSTOM here
				assert pred != null;

				if (pred.test(this, player)) {
					list.add(entry.getValue());
				}
			}
		}

		return list;
	}
}
