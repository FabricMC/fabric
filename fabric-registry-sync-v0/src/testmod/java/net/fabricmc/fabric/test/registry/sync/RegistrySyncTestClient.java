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

package net.fabricmc.fabric.test.registry.sync;

import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.RegistryPacketHandler;

@Environment(EnvType.CLIENT)
public class RegistrySyncTestClient implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(RegistrySyncTest.PACKET_CHECK_DIRECT, (client, handler, buf, responseSender) ->
				RegistrySyncTest.DIRECT_PACKET_HANDLER.receivePacket(buf));

		ClientPlayNetworking.registerGlobalReceiver(RegistrySyncTest.PACKET_CHECK_NBT, (client, handler, buf, responseSender) ->
				RegistrySyncTest.NBT_PACKET_HANDLER.receivePacket(buf));

		ClientPlayNetworking.registerGlobalReceiver(RegistrySyncTest.PACKET_CHECK_COMPARE, (client, handler, buf, responseSender) -> {
			logBufferSize(RegistrySyncTest.NBT_PACKET_HANDLER);
			logBufferSize(RegistrySyncTest.DIRECT_PACKET_HANDLER);

			Map<Identifier, Object2IntMap<Identifier>> directPacketMap = RegistrySyncTest.DIRECT_PACKET_HANDLER.getSyncedRegistryMap();
			Map<Identifier, Object2IntMap<Identifier>> nbtPacketMap = RegistrySyncTest.NBT_PACKET_HANDLER.getSyncedRegistryMap();

			Preconditions.checkArgument(Objects.requireNonNull(nbtPacketMap).equals(directPacketMap), "nbt packet and direct packet are not equal!");
		});
	}

	private void logBufferSize(RegistryPacketHandler handler) {
		String handlerName = handler.getClass().getSuperclass().getSimpleName();
		LOGGER.info("{} total packet: {}", handlerName, handler.getTotalPacketReceived());
		LOGGER.info("{} raw size: {}", handlerName, handler.getRawBufSize());
		LOGGER.info("{} deflated size: {}", handlerName, handler.getDeflatedBufSize());
	}
}
