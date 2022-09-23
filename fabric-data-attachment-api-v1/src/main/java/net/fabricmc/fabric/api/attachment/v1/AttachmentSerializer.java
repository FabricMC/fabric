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

package net.fabricmc.fabric.api.attachment.v1;

import java.util.Objects;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

/**
 * Serializer for an {@link AttachmentType}, that defines how the attachment is serialized to NBT and back.
 *
 * <p>Note that serializers are only used on the logical server side.
 *
 * @param <A> Type of the attached object.
 * @param <T> Type of the host of this attachment.
 */
public interface AttachmentSerializer<A, T> {
	/**
	 * Serialize the value to a new NBT compound.
	 * If {@code null} is returned, the value will not be saved at all.
	 *
	 * @param value The current value of the attachment. Never {@code null}.
	 * @return The serialized attachment, or {@code null} if nothing should be saved.
	 */
	@Nullable
	NbtCompound toNbt(A value);

	/**
	 * Create a new instance from an NBT compound previously created by {@link #toNbt}.
	 * If {@code null} is returned, the instance will not be placed in the attachment target.
	 *
	 * @param target The target of this attachment. This can be used to capture a reference (for example to a host entity).
	 * @param nbt The nbt data of this attachment. Never {@code null}.
	 * @return The deserialized attachment, or {@code null} if no attachment should be loaded.
	 */
	@Nullable
	A fromNbt(T target, NbtCompound nbt);

	/**
	 * Create an attachment serializer from a codec.
	 */
	static <A, T> AttachmentSerializer<A, T> fromCodec(Codec<A> codec) {
		Objects.requireNonNull(codec, "Codec may not be null.");

		return new AttachmentSerializer<>() {
			@Override
			@Nullable
			public NbtCompound toNbt(A value) {
				@Nullable
				NbtElement element = codec.encodeStart(NbtOps.INSTANCE, value).result().orElse(null);

				if (element instanceof NbtCompound compound) {
					return compound;
				} else if (element != null) {
					NbtCompound compound = new NbtCompound();
					compound.put("fabric:value", element); // TODO: is this a waste of space?
					return compound;
				}

				return null;
			}

			@Override
			@Nullable
			public A fromNbt(T target, NbtCompound nbt) {
				NbtElement toDecode = nbt;

				if (nbt.getSize() == 1 && nbt.contains("fabric:value")) {
					toDecode = nbt.get("fabric:value");
				}

				return codec.decode(NbtOps.INSTANCE, toDecode).result().map(Pair::getFirst).orElse(null);
			}
		};
	}
}
