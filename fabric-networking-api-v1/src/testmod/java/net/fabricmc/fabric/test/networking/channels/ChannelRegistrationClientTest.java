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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

// TODO: WIP
//  This simply adds a screen which can be opened to see what registrations for channels the client has received and seeing what channels the server supports for reception
@Environment(EnvType.CLIENT)
public final class ChannelRegistrationClientTest implements ClientModInitializer {
	static final KeyBinding OPEN_CHANNEL_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding("fabric-networking-api-v1-testmod-channel-test-screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_MENU, "fabric-networking-api-v1-testmod"));
	@Nullable
	private ServerState serverState;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				if (OPEN_CHANNEL_SCREEN.wasPressed()) {
					client.openScreen(new ChannelScreen(this.serverState));
				}
			}
		});

		ClientLoginConnectionEvents.LOGIN_INIT.register((handler, client) -> {
			// Setup the state
			this.serverState = new ServerState();
		});

		ClientLoginConnectionEvents.LOGIN_DISCONNECT.register((handler, client) -> {
			// Kill the current state
			this.serverState = null;
		});

		ClientPlayConnectionEvents.PLAY_DISCONNECT.register((handler, sender, client) -> {
			// Kill the current state
			this.serverState = null;
		});

		ClientPlayChannelEvents.REGISTER.register((handler, sender, client, channels) -> {
			this.serverState.register(channels);
		});

		ClientPlayChannelEvents.UNREGISTER.register((handler, sender, client, channels) -> {
			this.serverState.unregister(channels);
		});
	}

	static class ServerState {
		private final Set<Identifier> supported = new HashSet<>();

		void register(List<Identifier> channels) {
			this.supported.addAll(channels);
		}

		void unregister(List<Identifier> channels) {
			this.supported.removeAll(channels);
		}

		Collection<Identifier> getSupportedChannels() {
			return this.supported;
		}
	}
}
