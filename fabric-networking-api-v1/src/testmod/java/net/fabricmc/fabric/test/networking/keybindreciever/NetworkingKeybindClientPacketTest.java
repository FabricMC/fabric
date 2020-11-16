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

package net.fabricmc.fabric.test.networking.keybindreciever;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

// Sends a packet to the server when a keybinding was pressed
// The server in response will send a chat message to the client.
@Environment(EnvType.CLIENT)
public class NetworkingKeybindClientPacketTest implements ClientModInitializer {
	public static final KeyBinding TEST_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("fabric-networking-api-v1-testmod-keybind", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "fabric-networking-api-v1-testmod"));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Player must be in game to send packets, i.e. client.player != null
			if (client.getNetworkHandler() != null) {
				if (TEST_BINDING.wasPressed()) {
					// Send an empty payload, server just needs to be told when packet is sent
					ClientPlayNetworking.send(NetworkingKeybindPacketTest.KEYBINDING_PACKET_ID, PacketByteBufs.empty());
				}
			}
		});
	}
}
