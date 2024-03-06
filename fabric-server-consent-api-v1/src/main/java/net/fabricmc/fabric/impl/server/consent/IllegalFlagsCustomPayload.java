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

package net.fabricmc.fabric.impl.server.consent;

import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record IllegalFlagsCustomPayload(List<Identifier> illegalFlags) implements CustomPayload {
	public static final String SOME_UNIVERSAL_NAMESPACE = "noconsent";
	public static final Identifier ID = new Identifier(SOME_UNIVERSAL_NAMESPACE, "xpple");

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(illegalFlags, PacketByteBuf::writeIdentifier);
	}

	public IllegalFlagsCustomPayload(List<Identifier> illegalFlags) {
		this.illegalFlags = illegalFlags;
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
