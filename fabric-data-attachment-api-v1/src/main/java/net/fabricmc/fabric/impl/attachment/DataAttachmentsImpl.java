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
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

@ApiStatus.Internal
public final class DataAttachmentsImpl {
	public static final String NBT_KEY = "fabric:attachments";
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-data-attachment-api-v1");

	public static void writeNbt(@Nullable IdentityHashMap<AttachmentType<?, ?>, Object> map, NbtCompound nbt) {
		if (map == null) {
			return;
		}

		NbtCompound attachmentsTag = new NbtCompound();

		for (Map.Entry<AttachmentType<?, ?>, Object> entry : map.entrySet()) {
			AttachmentType<?, ?> attachmentType = entry.getKey();
			AttachmentSerializer serializer = attachmentType.getSerializer();

			if (serializer != null) {
				try {
					@Nullable
					NbtCompound attachmentNbt = serializer.toNbt(entry.getValue());

					if (attachmentNbt != null) {
						attachmentsTag.put(attachmentType.getIdentifier().toString(), attachmentNbt);
					}
				} catch (Exception exception) {
					LOGGER.error("Exception caught while serializing attachment data for attachment " + attachmentType.getIdentifier(), exception);
				}
			}
		}

		if (!attachmentsTag.isEmpty()) {
			nbt.put(NBT_KEY, attachmentsTag);
		}
	}

	@Nullable
	public static <T> IdentityHashMap<AttachmentType<?, ?>, Object> readNbt(Class<T> targetClass, T target, NbtCompound nbt) {
		if (!nbt.contains(NBT_KEY, NbtElement.COMPOUND_TYPE)) {
			return null;
		}

		NbtCompound attachmentsTag = nbt.getCompound(NBT_KEY);
		IdentityHashMap<AttachmentType<?, ?>, Object> map = new IdentityHashMap<>();

		for (String key : attachmentsTag.getKeys()) {
			try {
				Identifier attachmentId = new Identifier(key);
				AttachmentType<?, T> attachmentType = AttachmentTypeImpl.get(attachmentId, targetClass);

				if (attachmentType != null) {
					AttachmentSerializer<?, ? super T> serializer = attachmentType.getSerializer();

					if (serializer != null) {
						@Nullable
						Object value = serializer.fromNbt(target, attachmentsTag.getCompound(key));

						if (value != null) {
							map.put(attachmentType, value);
						}
					}
				}
			} catch (Exception exception) {
				LOGGER.error("Exception caught while deserializing attachment data: " + attachmentsTag, exception);
			}
		}

		return map.isEmpty() ? null : map;
	}
}
