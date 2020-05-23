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

package net.fabricmc.fabric.test.client.keybinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingUtil;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class KeybindingsTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyBinding binding1 = KeyBindingUtil.builder()
				.identifier(new Identifier("fabric-key-binding-api-v1-testmod:test_keybinding_1"))
				.category("category.first.test")
				.key(InputUtil.Type.KEYSYM, 80) // P
				.buildAndRegister();
		KeyBinding binding2 = KeyBindingUtil.builder()
				.identifier(new Identifier("fabric-key-binding-api-v1-testmod:test_keybinding_2"))
				.category("category.second.test")
				.key(InputUtil.Type.KEYSYM, 85) // U
				.buildAndRegister();
		KeyBinding stickyBinding = KeyBindingUtil.builder()
				.identifier(new Identifier("fabric-key-binding-api-v1-testmod:test_keybinding_sticky"))
				.category("category.first.test")
				.key(InputUtil.Type.KEYSYM, 82) // R
				.sticky()
				.buildAndRegister();

		ClientTickCallback.EVENT.register(client -> {
			while (binding1.wasPressed()) {
				client.player.sendMessage(new LiteralText("Key 1 was pressed!"));
			}

			while (binding2.wasPressed()) {
				client.player.sendMessage(new LiteralText("Key 2 was pressed!"));
			}

			if (stickyBinding.isPressed()) {
				client.player.sendMessage(new LiteralText("Sticky Key was pressed!"));
			}
		});
	}
}
