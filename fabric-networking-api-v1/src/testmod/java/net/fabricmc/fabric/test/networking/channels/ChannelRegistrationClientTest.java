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

package net.fabricmc.fabric.test.networking.channels;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

// TODO: WIP
//  This simply adds a screen which can be opened to see what registrations for channels the client has received and seeing what channels the server supports for reception
@Environment(EnvType.CLIENT)
public final class ChannelRegistrationClientTest implements ClientModInitializer {
	@Nullable
	private ChannelRegistrationClientTest.ClientSession clientSession;

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.PLAY_INITIALIZED.register((handler, sender, client) -> {
		});

		ClientPlayConnectionEvents.PLAY_DISCONNECTED.register((handler, sender, client) -> {
			// Kill the current state
			this.clientSession = null;
		});

		ClientChannelEvents.REGISTERED.register((handler, sender, client, channels) -> {
		});

		ClientChannelEvents.UNREGISTERED.register((handler, sender, client, channels) -> {
		});
	}

	class ClientSession {

	}

	class ServerState {

	}
}
