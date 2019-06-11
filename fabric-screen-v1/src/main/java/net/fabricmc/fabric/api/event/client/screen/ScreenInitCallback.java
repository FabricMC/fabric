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

package net.fabricmc.fabric.api.event.client.screen;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * This event is emitted on the client to initialize and redraw gui screens
 * whenever opened. Mods consuming this event are allowed to interact with
 * the screen to add their own buttons and elements.
 */
@FunctionalInterface
public interface ScreenInitCallback {
	/**
	 * Event bus for mods to subscribe to this event.
	 *
	 * Usage:
	 *   <pre><code>ScreenInitCallback.EVENT.register((client, screen) -> {...});</pre></code>
	 */
	Event<ScreenInitCallback> EVENT = EventFactory.createArrayBacked(ScreenInitCallback.class, listeners -> (client, screen) -> {
		for (ScreenInitCallback event : listeners) {
			event.onInit(client, screen);
		}
	});

	/**
	 * Callback for when a screen is initialized.
	 *
	 * @param client the minecraft client
	 * @param screen the current screen being displayed
	 */
	void onInit(MinecraftClient client, Screen screen);
}
