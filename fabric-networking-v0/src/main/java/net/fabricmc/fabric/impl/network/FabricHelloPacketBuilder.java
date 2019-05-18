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
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

class FabricHelloPacketBuilder {
	static final Identifier ID = new Identifier("fabric:hello");

	static PacketByteBuf buildHelloPacket() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("versionMajor", 1);
		tag.putInt("versionMinor", 0);

		CompoundTag modsTag = new CompoundTag();
		for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
			CompoundTag modTag = new CompoundTag();
			modTag.putString("name", container.getMetadata().getName());
			modTag.putString("version", container.getMetadata().getVersion().getFriendlyString());

			modsTag.put(container.getMetadata().getId(), modTag);
		}

		tag.put("mods", modsTag);

		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeCompoundTag(tag);
		return buf;
	}
}
