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

package net.fabricmc.fabric.impl.networking.payload;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;

public record RetainedPayload(Identifier id, PacketByteBuf buf) implements ResolvablePayload {
	@Override
	public ResolvedPayload resolve(@Nullable PacketType<?> type) {
		try {
			if (type == null) {
				PacketByteBuf copy = PacketByteBufs.create();
				copy.writeBytes(buf);
				return new UntypedPayload(this.id, copy);
			} else {
				TypedPayload typed = new TypedPayload(type.read(buf));
				int dangling = buf.readableBytes();

				if (dangling > 0) {
					throw new IllegalStateException("Found " + dangling + " extra bytes when reading packet " + id);
				}

				return typed;
			}
		} finally {
			buf.release();
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		throw new UnsupportedOperationException("RetainedPayload shouldn't be used to send");
	}
}
