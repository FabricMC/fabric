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

package net.fabricmc.fabric.impl.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class FabricHelloPacketBuilder {
	static final Identifier ID = new Identifier("fabric", "hello");
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 0;

	static PacketByteBuf buildHelloPacket() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("majorVersion", MAJOR_VERSION);
		tag.putInt("minorVersion", MINOR_VERSION);

		CompoundTag modsTag = new CompoundTag();
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			modsTag.putString(container.getMetadata().getId(), container.getMetadata().getVersion().getFriendlyString());
		}

		tag.put("mods", modsTag);

		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(tag);
		return buf;
	}
}
