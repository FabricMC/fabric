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

package net.fabricmc.fabric.test.screenlayer;

import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screenlayer.v1.ScreenLayer;

public class ScreenLayerTest implements ClientModInitializer {
	public static final Logger LOGGER = Logger.getLogger("ScreenLayerTest");

	KeyBinding keyMapping = new KeyBinding("examplemod.key.newscreen.label",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_N,
			"examplemod.key.category");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Started Screen Layer Test Mod");
		KeyBindingHelper.registerKeyBinding(keyMapping);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyMapping.wasPressed()) {
				ScreenLayer.push(new ExampleScreen(ScreenLayer.getScreenLayerCount()));
			}
		});

		ScreenEvents.BEFORE_INIT.register(((client, screen, scaledWidth, scaledHeight) ->
				ScreenKeyboardEvents.allowKeyPress(screen).register((keyScreen, key, scancode, modifiers) -> {
					if (keyScreen != null) {
						if (keyMapping.getDefaultKey().getCode() == key) {
							ScreenLayer.push(new ExampleScreen(ScreenLayer.getScreenLayerCount() + 1));
						}
					}

					return true;
				})));
	}
}
