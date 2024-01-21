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

package net.fabricmc.fabric.impl.recipe.ingredient;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CustomIngredientPayloadS2C(int protocolVersion) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, CustomIngredientPayloadS2C> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, CustomIngredientPayloadS2C::protocolVersion,
			CustomIngredientPayloadS2C::new
	);
	public static final CustomPayload.Id<CustomIngredientPayloadS2C> ID = new Id<>(CustomIngredientSync.PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
