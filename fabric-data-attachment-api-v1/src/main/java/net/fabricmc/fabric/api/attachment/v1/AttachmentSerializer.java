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

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

/**
 * A serializer for an {@link Attachment}, that handles writing to and reading from NBT to make attached data
 * persist after server restarts. For types which already have a {@link Codec}, {@link #fromCodec(Codec)} can be used
 * instead of implementing a custom serializer.
 *
 * @param <A> the type of the attached data
 */
public interface AttachmentSerializer<A> {
	String NBT_ATTACHMENT_KEY = "fabric:attachments";

	/**
	 * Creates an {@link AttachmentSerializer} using a {@link Codec}.
	 *
	 * @param codec the codec.
	 * @param <A> the type of the attached data.
	 * @return a serializer based on the provided {@link Codec}.
	 */
	static <A> AttachmentSerializer<A> fromCodec(Codec<A> codec) {
		return new AttachmentSerializer<>() {
			@Override
			public @Nullable NbtElement toNbt(A value) {
				return codec.encodeStart(NbtOps.INSTANCE, value)
						.result()
						.orElse(null);
			}

			@Override
			public @Nullable A fromNbt(NbtElement nbt) {
				return codec.decode(NbtOps.INSTANCE, nbt)
						.result()
						.map(Pair::getFirst)
						.orElse(null);
			}
		};
	}

	/**
	 * Serializes attached data to NBT.
	 *
	 * @param value attached data
	 * @return an {@link NbtElement} encoding the data.
	 */
	@Nullable
	NbtElement toNbt(A value);

	/**
	 * Reads attached data from serialized NBT.
	 *
	 * @param nbt the serialized NBT data, obtained from {@link #toNbt(Object)}
	 * @return the deserialized value.
	 */
	@Nullable
	A fromNbt(NbtElement nbt);
}
