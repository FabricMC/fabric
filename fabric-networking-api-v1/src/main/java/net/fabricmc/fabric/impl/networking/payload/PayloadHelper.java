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

import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class PayloadHelper {
	public static void write(PacketByteBuf byteBuf, PacketByteBuf data) {
		byteBuf.writeBytes(data.copy());
	}

	public static PacketByteBuf read(PacketByteBuf byteBuf) {
		PacketByteBuf newBuf = PacketByteBufs.create();
		newBuf.writeBytes(byteBuf.copy());
		byteBuf.skipBytes(byteBuf.readableBytes());
		return newBuf;
	}
}
