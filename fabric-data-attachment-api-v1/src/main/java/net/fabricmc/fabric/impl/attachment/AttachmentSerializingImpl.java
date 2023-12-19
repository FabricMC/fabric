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

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.Attachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;

public class AttachmentSerializingImpl {
	@SuppressWarnings("unchecked")
	public static void serializeAttachmentData(NbtCompound nbt, @Nullable IdentityHashMap<Attachment<?>, ?> attachments) {
		if (attachments == null) {
			return;
		}

		var compound = new NbtCompound();

		for (Map.Entry<Attachment<?>, ?> entry : attachments.entrySet()) {
			Attachment<?> attachment = entry.getKey();
			AttachmentSerializer<Object> serializer = (AttachmentSerializer<Object>) attachment.serializer();

			if (serializer != null) {
				compound.put(attachment.identifier().toString(), serializer.toNbt(entry.getValue()));
			}
		}

		nbt.put(AttachmentSerializer.NBT_ATTACHMENT_KEY, compound);
	}

	public static IdentityHashMap<Attachment<?>, Object> deserializeAttachmentData(NbtCompound nbt) {
		var attachments = new IdentityHashMap<Attachment<?>, Object>();

		if (nbt.contains(AttachmentSerializer.NBT_ATTACHMENT_KEY, NbtElement.COMPOUND_TYPE)) {
			NbtCompound compound = nbt.getCompound(AttachmentSerializer.NBT_ATTACHMENT_KEY);

			for (String key : compound.getKeys()) {
				Attachment<?> attachment = AttachmentRegistry.get(new Identifier(key));
				if (attachment == null) continue;
				AttachmentSerializer<?> serializer = attachment.serializer();
				if (serializer == null) continue;
				Object deserialized = serializer.fromNbt(compound.get(key));
				if (deserialized == null) continue;
				attachments.put(attachment, deserialized);
			}
		}

		return attachments;
	}

	public static boolean hasSerializableAttachments(@Nullable IdentityHashMap<Attachment<?>, ?> map) {
		if (map == null) {
			return false;
		}

		for (Attachment<?> attachment : map.keySet()) {
			if (attachment.serializer() != null) {
				return true;
			}
		}

		return false;
	}
}
