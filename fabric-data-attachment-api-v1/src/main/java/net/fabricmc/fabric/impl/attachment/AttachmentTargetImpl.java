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

import java.util.IdentityHashMap;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

/**
 * Internal interface implemented by the attachment targets via mixin. Mostly a duck with a bit of extra logic.
 */
@ApiStatus.Internal
public interface AttachmentTargetImpl {
	<T> T get(AttachmentType<T, ?> type);

	@Nullable
	<T> T set(AttachmentType<T, ?> type, T value);

	@Nullable
	IdentityHashMap<AttachmentType<?, ?>, Object> getAttachmentsHolder();

	void setAttachmentsHolder(@Nullable IdentityHashMap<AttachmentType<?, ?>, Object> map);

	default <T> void readAttachmentsFromNbt(Class<T> targetClass, NbtCompound nbt) {
		setAttachmentsHolder(DataAttachmentsImpl.readNbt(targetClass, (T) this, nbt));
	}

	default void writeAttachmentsToNbt(NbtCompound nbt) {
		DataAttachmentsImpl.writeNbt(getAttachmentsHolder(), nbt);
	}

	default boolean hasSerializableAttachments() {
		IdentityHashMap<AttachmentType<?, ?>, Object> map = getAttachmentsHolder();
		if (map == null) return false;

		for (AttachmentType<?, ?> type : map.keySet()) {
			if (((AttachmentTypeImpl<?, ?>) type).getSerializer() != null) {
				return true;
			}
		}

		return false;
	}
}
