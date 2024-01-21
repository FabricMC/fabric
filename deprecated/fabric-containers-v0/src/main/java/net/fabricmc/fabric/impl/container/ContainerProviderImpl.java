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

package net.fabricmc.fabric.impl.container;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.netty.buffer.Unpooled;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.impl.networking.CommonVersionPayload;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.container.ServerPlayerEntityAccessor;

public class ContainerProviderImpl implements ContainerProviderRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContainerProviderImpl.class);

	private static final Map<Identifier, ContainerFactory<ScreenHandler>> FACTORIES = new HashMap<>();

	public static void init() {
		PayloadTypeRegistry.play(NetworkSide.SERVERBOUND).register(ContainerOpenPayload.ID, ContainerOpenPayload.CODEC);
	}

	@Override
	public void registerFactory(Identifier identifier, ContainerFactory<ScreenHandler> factory) {
		if (FACTORIES.containsKey(identifier)) {
			throw new RuntimeException("A factory has already been registered as " + identifier.toString());
		}

		FACTORIES.put(identifier, factory);
	}

	@Override
	public void openContainer(Identifier identifier, PlayerEntity player, Consumer<RegistryByteBuf> writer) {
		if (!(player instanceof ServerPlayerEntity)) {
			LOGGER.warn("Please only use ContainerProviderRegistry.openContainer() with server-sided player entities!");
			return;
		}

		openContainer(identifier, (ServerPlayerEntity) player, writer);
	}

	private boolean emittedNoSyncHookWarning = false;

	@Override
	public void openContainer(Identifier identifier, ServerPlayerEntity player, Consumer<RegistryByteBuf> writer) {
		int syncId;

		if (player instanceof ServerPlayerEntitySyncHook) {
			ServerPlayerEntitySyncHook serverPlayerEntitySyncHook = (ServerPlayerEntitySyncHook) player;
			syncId = serverPlayerEntitySyncHook.fabric_incrementSyncId();
		} else if (player instanceof ServerPlayerEntityAccessor) {
			if (!emittedNoSyncHookWarning) {
				LOGGER.warn("ServerPlayerEntitySyncHook could not be applied - fabric-containers is using a hack!");
				emittedNoSyncHookWarning = true;
			}

			syncId = (((ServerPlayerEntityAccessor) player).getScreenHandlerSyncId() + 1) % 100;
			((ServerPlayerEntityAccessor) player).setScreenHandlerSyncId(syncId);
		} else {
			throw new RuntimeException("Neither ServerPlayerEntitySyncHook nor Accessor present! This should not happen!");
		}

		RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), player.server.getRegistryManager());
		writer.accept(buf);
		PacketByteBuf clonedBuf = new PacketByteBuf(buf.duplicate());
		player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(new ContainerOpenPayload(identifier, syncId, buf)));

		ScreenHandler screenHandler = createContainer(syncId, identifier, player, clonedBuf);

		if (screenHandler == null) {
			return;
		}

		player.currentScreenHandler = screenHandler;
		((ServerPlayerEntityAccessor) player).callOnScreenHandlerOpened(screenHandler);
	}

	public <C extends ScreenHandler> C createContainer(int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buf) {
		ContainerFactory<ScreenHandler> factory = FACTORIES.get(identifier);

		if (factory == null) {
			LOGGER.error("No container factory found for {}!", identifier.toString());
			return null;
		}

		//noinspection unchecked
		return (C) factory.create(syncId, identifier, player, buf);
	}

	public record ContainerOpenPayload(Identifier identifier, int syncId, RegistryByteBuf registryByteBuf) implements CustomPayload {
		public static final PacketCodec<RegistryByteBuf, ContainerOpenPayload> CODEC = CustomPayload.codecOf(ContainerOpenPayload::write, ContainerOpenPayload::new);
		public static final CustomPayload.Id<ContainerOpenPayload> ID = CustomPayload.id("fabric:container/open");

		private ContainerOpenPayload(RegistryByteBuf buf) {
			this(buf.readIdentifier(), buf.readByte(), buf);
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
