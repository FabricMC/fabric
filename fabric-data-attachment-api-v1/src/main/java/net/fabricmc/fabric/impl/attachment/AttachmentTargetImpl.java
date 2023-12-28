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

package net.fabricmc.fabric.impl.attachment;

import java.util.Map;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public interface AttachmentTargetImpl extends AttachmentTarget {
	/**
	 * Copies entity attachments when it is respawned and a new instance is created.
	 * Is triggered on player respawn, return from the End, or entity conversion.
	 * In the first case, only the attachments with {@link AttachmentType#copyOnPlayerRespawn()} will be transferred.
	 */
	@SuppressWarnings("unchecked")
	static void copyOnRespawn(AttachmentTargetImpl original, AttachmentTargetImpl target, boolean alive) {
		Map<AttachmentType<?>, ?> attachments = original.fabric_getAttachments();

		for (Map.Entry<AttachmentType<?>, ?> entry : attachments.entrySet()) {
			AttachmentType<Object> type = (AttachmentType<Object>) entry.getKey();

			if (alive || type.copyOnPlayerRespawn()) {
				target.setAttached(type, entry.getValue());
			}
		}
	}

	default Map<AttachmentType<?>, ?> fabric_getAttachments() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default void fabric_writeAttachmentsToNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default void fabric_readAttachmentsFromNbt(NbtCompound nbt) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default boolean fabric_hasPersistentAttachments() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
