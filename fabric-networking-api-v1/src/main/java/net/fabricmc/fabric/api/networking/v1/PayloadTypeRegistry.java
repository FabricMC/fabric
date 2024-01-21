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

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;

public interface PayloadTypeRegistry<B extends PacketByteBuf> {
	<T extends CustomPayload> CustomPayload.Type<? super B, T> register(CustomPayload.Id<T> id, PacketCodec<? super B, T> codec);

	@Nullable
	CustomPayload.Type<B, ? extends CustomPayload> get(Identifier id);

	@Nullable
	<T extends CustomPayload> CustomPayload.Type<B, T> get(CustomPayload.Id<T> id);

	static PayloadTypeRegistry<PacketByteBuf> configuration(NetworkSide side) {
		return switch (side) {
		case SERVERBOUND -> PayloadTypeRegistryImpl.CONFIGURATION_C2S;
		case CLIENTBOUND -> PayloadTypeRegistryImpl.CONFIGURATION_S2C;
		};
	}

	static PayloadTypeRegistry<RegistryByteBuf> play(NetworkSide side) {
		return switch (side) {
		case SERVERBOUND -> PayloadTypeRegistryImpl.PLAY_C2S;
		case CLIENTBOUND -> PayloadTypeRegistryImpl.PLAY_S2C;
		};
	}
}
