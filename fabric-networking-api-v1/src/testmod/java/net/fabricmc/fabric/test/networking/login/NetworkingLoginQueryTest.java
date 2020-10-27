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

package net.fabricmc.fabric.test.networking.login;

import java.util.concurrent.FutureTask;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;
import net.fabricmc.fabric.test.networking.play.NetworkingPlayPacketTest;

public final class NetworkingLoginQueryTest implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerConnectionEvents.LOGIN_QUERY_START.register(this::onLoginStart);

		// login delaying example
		ServerNetworking.getLoginReceivers().register(NetworkingPlayPacketTest.TEST_CHANNEL, (handler, server, sender, buf, understood, synchronizer) -> {
			if (understood) {
				FutureTask<?> future = new FutureTask<>(() -> {
					for (int i = 0; i <= 10; i++) {
						Thread.sleep(300);
						NetworkingTestmods.LOGGER.info("Delayed login for number {} 300 milliseconds", i);
					}

					return null;
				});

				// Execute the task on a worker thread as not to block the server thread
				Util.getMainWorkerExecutor().execute(future);
				synchronizer.waitFor(future);
			}
		});
	}

	private void onLoginStart(ServerLoginNetworkHandler networkHandler, MinecraftServer server, PacketSender sender, ServerNetworking.LoginSynchronizer synchronizer) {
		// Send a dummy query when the client starts accepting queries.
		sender.sendPacket(NetworkingPlayPacketTest.TEST_CHANNEL, PacketByteBufs.empty()); // dummy packet
	}
}
