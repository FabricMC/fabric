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

import java.util.IdentityHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentEntrypoint;
import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin({BlockEntity.class, Entity.class, World.class, Chunk.class})
abstract class AttachmentTargetsMixin implements AttachmentTargetImpl {
	@Nullable
	private IdentityHashMap<AttachmentType<?>, Object> fabric_dataAttachments = null;

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
		// Extremely inelegant, but the only alternative is separating out these two mixins and duplicating code
		Object thisObject = this;

		if (thisObject instanceof BlockEntity) {
			((BlockEntity) thisObject).markDirty();
		} else if (thisObject instanceof Chunk) {
			((Chunk) thisObject).setNeedsSaving(true);

			if (type.isPersistent() && ((Chunk) thisObject).getStatus().equals(ChunkStatus.EMPTY)) {
				AttachmentEntrypoint.LOGGER.warn("Attaching persistent attachment {} to chunk with chunk status EMPTY. Attachment might be discarded.", type.identifier());
			}
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
	public void fabric_writeAttachmentsToNbt(NbtCompound nbt) {
		AttachmentSerializingImpl.serializeAttachmentData(nbt, fabric_dataAttachments);
	}

	@Override
	public void fabric_readAttachmentsFromNbt(NbtCompound nbt) {
		fabric_dataAttachments = AttachmentSerializingImpl.deserializeAttachmentData(nbt);
	}

	@Override
	public boolean fabric_hasPersistentAttachments() {
		return AttachmentSerializingImpl.hasPersistentAttachments(fabric_dataAttachments);
	}

	@Override
	public Map<AttachmentType<?>, ?> fabric_getAttachments() {
		return fabric_dataAttachments;
	}
}
