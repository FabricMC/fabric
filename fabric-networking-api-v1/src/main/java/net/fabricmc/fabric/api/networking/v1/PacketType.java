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

package net.fabricmc.fabric.api.networking.v1;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public final class PacketType<T extends FabricPacket> {
	private final Identifier id;
	private final Function<PacketByteBuf, T> constructor;

	private PacketType(Identifier id, Function<PacketByteBuf, T> constructor) {
		this.id = id;
		this.constructor = constructor;
	}

	public static <P extends FabricPacket> PacketType<P> create(Identifier id, Function<PacketByteBuf, P> constructor) {
		return new PacketType<>(id, constructor);
	}

	public Identifier getId() {
		return id;
	}

	@ApiStatus.Internal
	public T read(PacketByteBuf buf) {
		try {
			return this.constructor.apply(buf);
		} catch (RuntimeException e) {
			throw new RuntimeException("Error while handling packet \"%s\": %s".formatted(this.id, e.getMessage()), e);
		}
	}
}
