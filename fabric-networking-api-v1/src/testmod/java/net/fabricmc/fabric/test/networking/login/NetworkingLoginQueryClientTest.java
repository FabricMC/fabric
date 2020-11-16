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

import java.util.concurrent.CompletableFuture;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.test.networking.play.NetworkingPlayPacketTest;

@Environment(EnvType.CLIENT)
public final class NetworkingLoginQueryClientTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Send a dummy response to the server in return, by registering here we essentially say we understood the server's query
		ClientLoginNetworking.registerGlobalReceiver(NetworkingPlayPacketTest.TEST_CHANNEL, (client, handler, buf, listenerAdder) -> {
			return CompletableFuture.completedFuture(PacketByteBufs.empty());
		});
	}
}
