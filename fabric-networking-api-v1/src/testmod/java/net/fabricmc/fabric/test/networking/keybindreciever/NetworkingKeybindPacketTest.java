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

import net.minecraft.text.KeybindText;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

// Listens for a packet from the client which is sent to the server when a keybinding is pressed.
// In response the server will send a message containing the keybind text letting the client know it pressed that key.
public final class NetworkingKeybindPacketTest implements ModInitializer {
	public static final Identifier KEYBINDING_PACKET_ID = NetworkingTestmods.id("keybind_press_test");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.register(KEYBINDING_PACKET_ID, (handler, server, sender, buf) -> {
			// TODO: Can we send chat off the server thread?
			server.execute(() -> {
				handler.player.sendMessage(new LiteralText("So you pressed ").append(new KeybindText("fabric-networking-api-v1-testmod-keybind").styled(style -> style.withFormatting(Formatting.BLUE))), false);
			});
		});
	}
}
