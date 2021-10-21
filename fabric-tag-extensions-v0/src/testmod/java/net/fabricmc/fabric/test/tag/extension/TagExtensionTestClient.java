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

package net.fabricmc.fabric.test.tag.extension;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class TagExtensionTestClient implements ClientModInitializer {
	static final KeyBinding KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding("tag_test", GLFW.GLFW_KEY_EQUAL, "tag_test"));

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (KEY_BINDING.isPressed()) {
				TagExtensionTest.FACTORY_TEST.values().forEach(biome -> {
					Identifier id = client.getNetworkHandler().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
					client.player.sendMessage(new LiteralText(id.toString()), false);
				});
			}
		});
	}
}
