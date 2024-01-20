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

package net.fabricmc.fabric.impl.screenhandler;

import java.util.Objects;

import io.netty.buffer.Unpooled;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;

import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

public final class Networking implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-screen-handler-api-v1/server");

	// [Packet format]
	// typeId: identifier
	// syncId: varInt
	// title: text
	// customData: buf
	public static final Identifier OPEN_ID = new Identifier("fabric-screen-handler-api-v1", "open_screen");

	/**
	 * Opens an extended screen handler by sending a custom packet to the client.
	 *
	 * @param player  the player
	 * @param factory the screen handler factory
	 * @param handler the screen handler instance
	 * @param syncId  the synchronization ID
	 */
	public static void sendOpenPacket(ServerPlayerEntity player, ExtendedScreenHandlerFactory factory, ScreenHandler handler, int syncId) {
		Objects.requireNonNull(player, "player is null");
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(handler, "handler is null");

		Identifier typeId = Registries.SCREEN_HANDLER.getId(handler.getType());

		if (typeId == null) {
			LOGGER.warn("Trying to open unregistered screen handler {}", handler);
			return;
		}

		RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), player.server.getRegistryManager());
		factory.writeScreenOpeningData(player, buf);

		ServerPlayNetworking.send(player, new OpenScreenPayload(typeId, syncId, factory.getDisplayName(), buf));
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.play(NetworkSide.SERVERBOUND).register(OpenScreenPayload.ID, OpenScreenPayload.CODEC);
	}

	public record OpenScreenPayload(Identifier identifier, int syncId, Text title, RegistryByteBuf registryByteBuf) implements CustomPayload {
		public static final PacketCodec<RegistryByteBuf, OpenScreenPayload> CODEC = CustomPayload.codecOf(OpenScreenPayload::write, OpenScreenPayload::new);
		public static final CustomPayload.Id<OpenScreenPayload> ID = new Id<>(OPEN_ID);

		private OpenScreenPayload(RegistryByteBuf buf) {
			this(buf.readIdentifier(), buf.readByte(), buf.readText(), buf);
		}

		private void write(RegistryByteBuf buf) {
			buf.writeIdentifier(this.identifier);
			buf.writeByte(this.syncId);
			registryByteBuf.getBytes(registryByteBuf.readerIndex(), buf);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
