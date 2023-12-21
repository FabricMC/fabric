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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class AttachmentSerializingImpl {
	@SuppressWarnings("unchecked")
	public static void serializeAttachmentData(NbtCompound nbt, @Nullable IdentityHashMap<AttachmentType<?>, ?> attachments) {
		if (attachments == null) {
			return;
		}

		var compound = new NbtCompound();

		for (Map.Entry<AttachmentType<?>, ?> entry : attachments.entrySet()) {
			AttachmentType<?> type = entry.getKey();

			if (type.persistent()) {
				Codec<Object> codec = (Codec<Object>) type.codec();
				// non-nullity enforced by builder API
				codec.encodeStart(NbtOps.INSTANCE, entry.getValue())
						.result()
						.ifPresent(serialized ->
								compound.put(type.identifier().toString(), serialized)
						);
			}
		}

		nbt.put(AttachmentTarget.NBT_ATTACHMENT_KEY, compound);
	}

	public static IdentityHashMap<AttachmentType<?>, Object> deserializeAttachmentData(NbtCompound nbt) {
		var attachments = new IdentityHashMap<AttachmentType<?>, Object>();

		if (nbt.contains(AttachmentTarget.NBT_ATTACHMENT_KEY, NbtElement.COMPOUND_TYPE)) {
			NbtCompound compound = nbt.getCompound(AttachmentTarget.NBT_ATTACHMENT_KEY);

			for (String key : compound.getKeys()) {
				AttachmentType<?> type = AttachmentRegistryImpl.get(new Identifier(key));

				if (type != null && type.persistent()) {
					// non-nullity enforced by builder API
					type.codec()
							.decode(NbtOps.INSTANCE, compound.get(key))
							.result()
							.map(Pair::getFirst)
							.ifPresent(deserialized -> attachments.put(type, deserialized));
				}
			}
		}

		return attachments;
	}

	public static boolean hasSerializableAttachments(@Nullable IdentityHashMap<AttachmentType<?>, ?> map) {
		if (map == null) {
			return false;
		}

		for (AttachmentType<?> type : map.keySet()) {
			if (type.persistent()) {
				return true;
			}
		}

		return false;
	}
}
