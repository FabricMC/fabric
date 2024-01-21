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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;

public final class Networking implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-screen-handler-api-v1/server");

	// [Packet format]
	// typeId: identifier
	// syncId: varInt
	// title: text
	// customData: buf
	public static final Identifier OPEN_ID = new Identifier("fabric-screen-handler-api-v1", "open_screen");
	public static final Map<Identifier, PacketCodec<RegistryByteBuf, ?>> CODEC_BY_ID = new HashMap<>();

	/**
	 * Opens an extended screen handler by sending a custom packet to the client.
	 *
	 * @param player  the player
	 * @param factory the screen handler factory
	 * @param handler the screen handler instance
	 * @param syncId  the synchronization ID
	 */
	@SuppressWarnings("unchecked")
	public static <D> void sendOpenPacket(ServerPlayerEntity player, ExtendedScreenHandlerFactory<D> factory, ScreenHandler handler, int syncId) {
		Objects.requireNonNull(player, "player is null");
		Objects.requireNonNull(factory, "factory is null");
		Objects.requireNonNull(handler, "handler is null");

		Identifier typeId = Registries.SCREEN_HANDLER.getId(handler.getType());

		if (typeId == null) {
			LOGGER.warn("Trying to open unregistered screen handler {}", handler);
			return;
		}

		PacketCodec<RegistryByteBuf, D> codec = (PacketCodec<RegistryByteBuf, D>) Objects.requireNonNull(CODEC_BY_ID.get(typeId), () -> "Codec for " + typeId + " is not registered!");
		D data = factory.getScreenOpeningData(player);

		ServerPlayNetworking.send(player, new OpenScreenPayload<>(typeId, syncId, factory.getDisplayName(), codec, data));
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.play(NetworkSide.CLIENTBOUND).register(OpenScreenPayload.ID, OpenScreenPayload.CODEC);
		RegistryEntryAddedCallback.event(Registries.SCREEN_HANDLER).register((rawId, id, type) -> {
			if (type instanceof ExtendedScreenHandlerType<?, ?> extended) {
				CODEC_BY_ID.put(id, extended.getPacketCodec());
			}
		});
	}

	public record OpenScreenPayload<D>(Identifier identifier, int syncId, Text title, PacketCodec<RegistryByteBuf, D> innerCodec, D data) implements CustomPayload {
		public static final PacketCodec<RegistryByteBuf, OpenScreenPayload<?>> CODEC = CustomPayload.codecOf(OpenScreenPayload::write, OpenScreenPayload::fromBuf);
		public static final CustomPayload.Id<OpenScreenPayload<?>> ID = new Id<>(OPEN_ID);

		@SuppressWarnings("unchecked")
		private static <D> OpenScreenPayload<D> fromBuf(RegistryByteBuf buf) {
			Identifier id = buf.readIdentifier();
			PacketCodec<RegistryByteBuf, D> codec = (PacketCodec<RegistryByteBuf, D>) CODEC_BY_ID.get(id);
			return new OpenScreenPayload<>(id, buf.readByte(), buf.readText(), codec, codec == null ? null : codec.decode(buf));
		}

		private void write(RegistryByteBuf buf) {
			buf.writeIdentifier(this.identifier);
			buf.writeByte(this.syncId);
			this.innerCodec.encode(buf, this.data);
		}

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
