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
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ScreenRenderCallback {
	Event<ScreenRenderCallback> EVENT = EventFactory.createArrayBacked(ScreenRenderCallback.class, callbacks -> (client, screen, context, mouseX, mouseY, tickDelta) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("fabricRenderScreen");

			for (ScreenRenderCallback callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onRender(client, screen, context, mouseX, mouseY, tickDelta);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ScreenRenderCallback callback : callbacks) {
				callback.onRender(client, screen, context, mouseX, mouseY, tickDelta);
			}
		}
	});

	void onRender(MinecraftClient client, Screen screen, ScreenContext context, int mouseX, int mouseY, float tickDelta);
}
