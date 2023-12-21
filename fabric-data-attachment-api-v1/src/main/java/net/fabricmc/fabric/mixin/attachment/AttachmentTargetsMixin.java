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
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentSerializingImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.AttachmentTypeImpl;

@Mixin(value = {BlockEntity.class, Entity.class, World.class, WorldChunk.class})
public class AttachmentTargetsMixin implements AttachmentTargetImpl {
	@Unique
	private static final Logger logger = LoggerFactory.getLogger("fabric-data-attachment-api-v1");
	@Unique
	@Nullable
	protected IdentityHashMap<AttachmentType<?>, Object> fabric_dataAttachments = null;

	@Override
	@Nullable
	public <T> T getAttached(AttachmentType<T> type) {
		T current = fabric_dataAttachments == null ? null : (T) fabric_dataAttachments.get(type);

		if (current != null) {
			return current;
		}

		Supplier<T> initializer;

		if ((initializer = ((AttachmentTypeImpl<T>) type).initializer()) != null) {
			logger.warn("Attachment type '" + type.identifier() + "' has defaults, and should be used with the other getAttached method.");

			if (fabric_dataAttachments == null) {
				fabric_dataAttachments = new IdentityHashMap<>();
			}

			T initialized = initializer.get();
			fabric_dataAttachments.put(type, initialized);
			return initialized;
		}

		return null;
	}

	@Override
	@Nullable
	public <T> T setAttached(AttachmentType<T> att, @Nullable T value) {
		if (value == null) {
			if (fabric_dataAttachments == null) {
				return null;
			}

			T removed = (T) fabric_dataAttachments.remove(att);

			if (fabric_dataAttachments.isEmpty()) {
				fabric_dataAttachments = null;
			}

			return removed;
		} else {
			if (fabric_dataAttachments == null) {
				fabric_dataAttachments = new IdentityHashMap<>();
			}

			return (T) fabric_dataAttachments.put(att, value);
		}
	}

	@Override
	public boolean hasAttached(AttachmentType<?> att) {
		return fabric_dataAttachments != null && fabric_dataAttachments.containsKey(att);
	}

	@Override
	public void writeAttachmentsToNbt(NbtCompound nbt) {
		AttachmentSerializingImpl.serializeAttachmentData(nbt, fabric_dataAttachments);
	}

	@Override
	public void readAttachmentsFromNbt(NbtCompound nbt) {
		fabric_dataAttachments = AttachmentSerializingImpl.deserializeAttachmentData(nbt);
	}

	@Override
	public boolean hasPersistentAttachments() {
		return AttachmentSerializingImpl.hasSerializableAttachments(fabric_dataAttachments);
	}
}
