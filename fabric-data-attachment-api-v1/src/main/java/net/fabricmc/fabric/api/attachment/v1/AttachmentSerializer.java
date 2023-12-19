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

public interface AttachmentSerializer<A> {
	String NBT_ATTACHMENT_KEY = "fabric:attachments";

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

	@Nullable
	NbtElement toNbt(A value);

	@Nullable
	A fromNbt(NbtElement nbt);
}
