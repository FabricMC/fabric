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

package net.fabricmc.fabric.test.networking.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PayloadTypeRegistryTests {
	@BeforeAll
	static void beforeAll() {
		SharedConstants.createGameVersion();
		Bootstrap.initialize();

		PayloadTypeRegistry.playC2S().register(C2SPlayPayload.ID, C2SPlayPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(S2CPlayPayload.ID, S2CPlayPayload.CODEC);

		PayloadTypeRegistry.configurationC2S().register(C2SConfigPayload.ID, C2SConfigPayload.CODEC);
		PayloadTypeRegistry.configurationS2C().register(S2CConfigPayload.ID, S2CConfigPayload.CODEC);
	}

	@Test
	void C2SPlay() {
		RegistryByteBuf buf = new RegistryByteBuf(PacketByteBufs.create(), null);

		var packetToSend = new CustomPayloadC2SPacket(new C2SPlayPayload("Hello"));
		CustomPayloadC2SPacket.CODEC.encode(buf, packetToSend);

		CustomPayloadC2SPacket decodedPacket = CustomPayloadC2SPacket.CODEC.decode(buf);

		if (decodedPacket.payload() instanceof C2SPlayPayload payload) {
			assertEquals("Hello", payload.value());
		} else {
			fail();
		}
	}

	@Test
	void S2CPlay() {
		RegistryByteBuf buf = new RegistryByteBuf(PacketByteBufs.create(), null);

		var packetToSend = new CustomPayloadS2CPacket(new S2CPlayPayload("Hello"));
		CustomPayloadS2CPacket.PLAY_CODEC.encode(buf, packetToSend);

		CustomPayloadS2CPacket decodedPacket = CustomPayloadS2CPacket.PLAY_CODEC.decode(buf);

		if (decodedPacket.payload() instanceof S2CPlayPayload payload) {
			assertEquals("Hello", payload.value());
		} else {
			fail();
		}
	}

	@Test
	void C2SConfig() {
		PacketByteBuf buf = PacketByteBufs.create();

		var packetToSend = new CustomPayloadC2SPacket(new C2SConfigPayload("Hello"));
		CustomPayloadC2SPacket.CODEC.encode(buf, packetToSend);

		CustomPayloadC2SPacket decodedPacket = CustomPayloadC2SPacket.CODEC.decode(buf);

		if (decodedPacket.payload() instanceof C2SConfigPayload payload) {
			assertEquals("Hello", payload.value());
		} else {
			fail();
		}
	}

	@Test
	void S2CConfig() {
		PacketByteBuf buf = PacketByteBufs.create();

		var packetToSend = new CustomPayloadS2CPacket(new S2CConfigPayload("Hello"));
		CustomPayloadS2CPacket.CONFIGURATION_CODEC.encode(buf, packetToSend);

		CustomPayloadS2CPacket decodedPacket = CustomPayloadS2CPacket.CONFIGURATION_CODEC.decode(buf);

		if (decodedPacket.payload() instanceof S2CConfigPayload payload) {
			assertEquals("Hello", payload.value());
		} else {
			fail();
		}
	}

	private record C2SPlayPayload(String value) implements CustomPayload {
		public static final CustomPayload.Id<C2SPlayPayload> ID = new Id<>(Identifier.of("fabric:c2s_play"));
		public static final PacketCodec<RegistryByteBuf, C2SPlayPayload> CODEC = PacketCodecs.STRING.xmap(C2SPlayPayload::new, C2SPlayPayload::value).cast();

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record S2CPlayPayload(String value) implements CustomPayload {
		public static final CustomPayload.Id<S2CPlayPayload> ID = new Id<>(Identifier.of("fabric:s2c_play"));
		public static final PacketCodec<RegistryByteBuf, S2CPlayPayload> CODEC = PacketCodecs.STRING.xmap(S2CPlayPayload::new, S2CPlayPayload::value).cast();

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record C2SConfigPayload(String value) implements CustomPayload {
		public static final CustomPayload.Id<C2SConfigPayload> ID = new Id<>(Identifier.of("fabric:c2s_config"));
		public static final PacketCodec<PacketByteBuf, C2SConfigPayload> CODEC = PacketCodecs.STRING.xmap(C2SConfigPayload::new, C2SConfigPayload::value).cast();

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private record S2CConfigPayload(String value) implements CustomPayload {
		public static final CustomPayload.Id<S2CConfigPayload> ID = new Id<>(Identifier.of("fabric:s2c_config"));
		public static final PacketCodec<PacketByteBuf, S2CConfigPayload> CODEC = PacketCodecs.STRING.xmap(S2CConfigPayload::new, S2CConfigPayload::value).cast();

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
