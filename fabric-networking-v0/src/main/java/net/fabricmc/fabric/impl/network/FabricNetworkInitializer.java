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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.network.login.S2CLoginHandshakeCallback;
import net.fabricmc.fabric.impl.network.login.S2CLoginQueryQueue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class FabricNetworkInitializer implements ModInitializer {
	protected static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		S2CLoginHandshakeCallback.EVENT.register(queue -> {
			CompoundTag request = new CompoundTag();
			request.putInt("version", 1);

			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeCompoundTag(request);

			queue.sendPacket(new Identifier("fabric:hello"), buf, (handler, connection, id, responseBuf) -> {
				CompoundTag response;
				try {
					response = responseBuf.readCompoundTag();
				} catch (Throwable e) {
					response = null;
				}

				if (response != null && response.containsKey("version", NbtType.NUMBER)) {
					LOGGER.debug("Read compound tag - connected to a Fabric client!");
				} else {
					LOGGER.debug("Could not read compound tag - probably not a Fabric client!");
				}
			});
		});
	}
}
