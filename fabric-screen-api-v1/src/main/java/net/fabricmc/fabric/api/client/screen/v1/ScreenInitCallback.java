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

package net.fabricmc.fabric.api.client.screen.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public interface ScreenInitCallback {
	Event<ScreenInitCallback> EVENT = EventFactory.createArrayBacked(ScreenInitCallback.class, callbacks -> (client, screen, context, scaledWidth, scaledHeight) -> {
		for (ScreenInitCallback callback : callbacks) {
			callback.onInit(client, screen, context, scaledWidth, scaledHeight);
		}
	});

	void onInit(MinecraftClient client, Screen screen, ScreenContext context, int scaledWidth, int scaledHeight);
}
