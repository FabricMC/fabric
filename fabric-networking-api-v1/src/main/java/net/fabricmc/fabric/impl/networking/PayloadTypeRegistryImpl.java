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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadTypeRegistryImpl<B extends PacketByteBuf> implements PayloadTypeRegistry<B> {
	public static final PayloadTypeRegistryImpl<PacketByteBuf> CONFIGURATION_C2S = new PayloadTypeRegistryImpl<>(NetworkPhase.CONFIGURATION, NetworkSide.SERVERBOUND);
	public static final PayloadTypeRegistryImpl<PacketByteBuf> CONFIGURATION_S2C = new PayloadTypeRegistryImpl<>(NetworkPhase.CONFIGURATION, NetworkSide.CLIENTBOUND);
	public static final PayloadTypeRegistryImpl<RegistryByteBuf> PLAY_C2S = new PayloadTypeRegistryImpl<>(NetworkPhase.PLAY, NetworkSide.SERVERBOUND);
	public static final PayloadTypeRegistryImpl<RegistryByteBuf> PLAY_S2C = new PayloadTypeRegistryImpl<>(NetworkPhase.PLAY, NetworkSide.CLIENTBOUND);

	private final Map<Identifier, CustomPayload.Type<B, ? extends CustomPayload>> packetTypes = new HashMap<>();
	private final NetworkPhase state;
	private final NetworkSide side;

	private PayloadTypeRegistryImpl(NetworkPhase state, NetworkSide side) {
		this.state = state;
		this.side = side;
	}

	@Override
	public <T extends CustomPayload> CustomPayload.Type<? super B, T> register(CustomPayload.Id<T> id, PacketCodec<? super B, T> codec) {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");

		final CustomPayload.Type<B, T> payloadType = new CustomPayload.Type<>(id, codec.cast());

		if (packetTypes.containsKey(id.id())) {
			throw new IllegalArgumentException("Packet type " + id + " is already registered!");
		}

		packetTypes.put(id.id(), payloadType);
		return payloadType;
	}

	@Nullable
	public CustomPayload.Type<B, ? extends CustomPayload> get(Identifier id) {
		return packetTypes.get(id);
	}

	@Nullable
	public <T extends CustomPayload> CustomPayload.Type<B, T> get(CustomPayload.Id<T> id) {
		//noinspection unchecked
		return (CustomPayload.Type<B, T>) packetTypes.get(id.id());
	}

	public NetworkPhase getPhase() {
		return state;
	}

	public NetworkSide getSide() {
		return side;
	}
}
