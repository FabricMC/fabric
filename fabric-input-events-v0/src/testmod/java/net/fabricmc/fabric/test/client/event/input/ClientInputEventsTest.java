/*
 * Copyright (c) 2016, 2017, 2018, 2019, 2020 FabricMC
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

package net.fabricmc.fabric.test.client.event.input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.input.ClientInputEvents;

public class ClientInputEventsTest implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("ClientInputEventsTest");

	@Override
	public void onInitialize() {
		ClientInputEvents.KEY_PRESSED.register(key -> {
			LOGGER.info("pressed {}", key.getKey().getTranslationKey());
		});
		ClientInputEvents.KEY_RELEASED.register(key -> {
			LOGGER.info("released {}", key.getKey().getTranslationKey());
		});
		ClientInputEvents.KEY_REPEATED.register(key -> {
			LOGGER.info("repeated {}", key.getKey().getTranslationKey());
		});
		ClientInputEvents.KEYBIND_PRESSED.register(key -> {
			LOGGER.info("pressed {}", key.getKeybind().getTranslationKey());
		});
		ClientInputEvents.KEYBIND_RELEASED.register(key -> {
			LOGGER.info("released {}", key.getKeybind().getTranslationKey());
		});
		ClientInputEvents.KEYBIND_REPEATED.register(key -> {
			LOGGER.info("repeated {}", key.getKeybind().getTranslationKey());
		});
		ClientInputEvents.CHAR_TYPED.register(key -> {
			LOGGER.info("typed U+{}", String.format("%04x", key.codepoint));
		});
		ClientInputEvents.MOUSE_MOVED.register(mouse -> {
			LOGGER.info("moved to {},{} (delta {},{})", mouse.x, mouse.y, mouse.dx, mouse.dy);
		});
		ClientInputEvents.MOUSE_BUTTON_PRESSED.register(mouse -> {
			LOGGER.info("pressed {}", mouse.getKey().getTranslationKey());
		});
		ClientInputEvents.MOUSE_BUTTON_RELEASED.register(mouse -> {
			LOGGER.info("released {}", mouse.getKey().getTranslationKey());
		});
		ClientInputEvents.MOUSE_WHEEL_SCROLLED.register(mouse -> {
			LOGGER.info("scrolled by {},{}", mouse.scrollX, mouse.scrollY);
		});
	}
}
