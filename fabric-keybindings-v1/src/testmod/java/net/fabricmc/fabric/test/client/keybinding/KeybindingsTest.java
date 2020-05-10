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

import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

public class KeybindingsTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyBindingRegistry registry = KeyBindingRegistry.INSTANCE;
		registry.addCategory("category.this.is.a.test");
		FabricKeyBinding binding = FabricKeyBinding.builder()
				.id(new Identifier("fabric-keybindings-v1-testmod:test_keybinding"))
				.category("category.this.is.a.test")
				.type(InputUtil.Type.KEYSYM)
				.code(80) // P
				.automaticallyRegister() // This is going to register automatically
				.build();

		ClientTickCallback.EVENT.register(client -> {
			while (binding.wasPressed()) {
				client.player.sendMessage(new LiteralText("Key was pressed!"));
			}
		});
	}
}
