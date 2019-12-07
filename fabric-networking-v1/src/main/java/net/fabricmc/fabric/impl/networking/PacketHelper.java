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

package net.fabricmc.fabric.impl.networking;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;

public final class PacketHelper {
	public static final Identifier QUERY_CHANNELS = new Identifier("fabric-networking-v1", "query_channels");
	public static final Identifier REGISTER = new Identifier("minecraft", "register");
	public static final Identifier UNREGISTER = new Identifier("minecraft", "unregister");

	public static PacketByteBuf createRegisterChannelBuf(Collection<Identifier> ids) {
		PacketByteBuf buf = PacketByteBufs.create();
		boolean first = true;

		for (Identifier a : ids) {
			if (!first) {
				buf.writeByte(0);
			} else {
				first = false;
			}

			buf.writeBytes(a.toString().getBytes(StandardCharsets.US_ASCII));
		}

		return buf;
	}

	public static void releaseBuffer(ByteBuf buffer) {
		if (buffer != null && buffer.refCnt() > 0 && !PacketDebugOptions.DISABLE_BUFFER_RELEASES) buffer.release();
	}

	private PacketHelper() {
	}
}
