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

package net.fabricmc.fabric.impl.client.screen;

import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Factory methods for creating event instances used in {@link FabricScreen}.
 */
public final class ScreenEventFactory {
	public static Event<ScreenEvents.BeforeRender> createBeforeRenderEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeRender.class, callbacks -> (client, matrices, screen, info, mouseX, mouseY, tickDelta) -> {
			if (EventFactory.isProfilingEnabled()) {
				final Profiler profiler = client.getProfiler();
				profiler.push("beforeFabricRenderScreen");

				for (ScreenEvents.BeforeRender callback : callbacks) {
					profiler.push(EventFactory.getHandlerName(callback));
					callback.beforeRender(client, matrices, screen, info, mouseX, mouseY, tickDelta);
					profiler.pop();
				}

				profiler.pop();
			} else {
				for (ScreenEvents.BeforeRender callback : callbacks) {
					callback.beforeRender(client, matrices, screen, info, mouseX, mouseY, tickDelta);
				}
			}
		});
	}

	public static Event<ScreenEvents.AfterRender> createAfterRenderEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterRender.class, callbacks -> (client, matrices, screen, info, mouseX, mouseY, tickDelta) -> {
			if (EventFactory.isProfilingEnabled()) {
				final Profiler profiler = client.getProfiler();
				profiler.push("afterFabricRenderScreen");

				for (ScreenEvents.AfterRender callback : callbacks) {
					profiler.push(EventFactory.getHandlerName(callback));
					callback.afterRender(client, matrices, screen, info, mouseX, mouseY, tickDelta);
					profiler.pop();
				}

				profiler.pop();
			} else {
				for (ScreenEvents.AfterRender callback : callbacks) {
					callback.afterRender(client, matrices, screen, info, mouseX, mouseY, tickDelta);
				}
			}
		});
	}

	public static Event<ScreenEvents.BeforeTick> createBeforeTickEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeTick.class, callbacks -> (client, screen, info) -> {
			if (EventFactory.isProfilingEnabled()) {
				final Profiler profiler = client.getProfiler();
				profiler.push("beforeFabricScreenTick");

				for (ScreenEvents.BeforeTick callback : callbacks) {
					profiler.push(EventFactory.getHandlerName(callback));
					callback.beforeTick(client, screen, info);
					profiler.pop();
				}

				profiler.pop();
			} else {
				for (ScreenEvents.BeforeTick callback : callbacks) {
					callback.beforeTick(client, screen, info);
				}
			}
		});
	}

	public static Event<ScreenEvents.AfterTick> createAfterTickEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterTick.class, callbacks -> (client, screen, info) -> {
			if (EventFactory.isProfilingEnabled()) {
				final Profiler profiler = client.getProfiler();
				profiler.push("afterFabricScreenTick");

				for (ScreenEvents.AfterTick callback : callbacks) {
					profiler.push(EventFactory.getHandlerName(callback));
					callback.afterTick(client, screen, info);
					profiler.pop();
				}

				profiler.pop();
			} else {
				for (ScreenEvents.AfterTick callback : callbacks) {
					callback.afterTick(client, screen, info);
				}
			}
		});
	}

	public static Event<ScreenEvents.AfterResize> createAfterResizeEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterResize.class, callbacks -> (client, screen, info) -> {
			if (EventFactory.isProfilingEnabled()) {
				final Profiler profiler = client.getProfiler();
				profiler.push("afterFabricScreenResize");

				for (ScreenEvents.AfterResize callback : callbacks) {
					profiler.push(EventFactory.getHandlerName(callback));
					callback.onResize(client, screen, info);
					profiler.pop();
				}

				profiler.pop();
			} else {
				for (ScreenEvents.AfterResize callback : callbacks) {
					callback.onResize(client, screen, info);
				}
			}
		});
	}

	public static Event<ScreenEvents.BeforeKeyPressed> createBeforeKeyPressedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeKeyPressed.class, callbacks -> (client, screen, info, key, scancode, modifiers) -> {
			for (ScreenEvents.BeforeKeyPressed callback : callbacks) {
				if (callback.beforeKeyPress(client, screen, info, key, scancode, modifiers)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenEvents.AfterKeyPressed> createAfterKeyPressedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterKeyPressed.class, callbacks -> (client, screen, info, key, scancode, modifiers) -> {
			for (ScreenEvents.AfterKeyPressed callback : callbacks) {
				callback.afterKeyPress(client, screen, info, key, scancode, modifiers);
			}
		});
	}

	public static Event<ScreenEvents.BeforeKeyReleased> createBeforeKeyReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeKeyReleased.class, callbacks -> (client, screen, info, key, scancode, modifiers) -> {
			for (ScreenEvents.BeforeKeyReleased callback : callbacks) {
				if (callback.beforeKeyReleased(client, screen, info, key, scancode, modifiers)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenEvents.AfterKeyReleased> createAfterKeyReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterKeyReleased.class, callbacks -> (client, screen, info, key, scancode, modifiers) -> {
			for (ScreenEvents.AfterKeyReleased callback : callbacks) {
				callback.afterKeyReleased(client, screen, info, key, scancode, modifiers);
			}
		});
	}

	//

	public static Event<ScreenEvents.BeforeMouseClicked> createBeforeMouseClickedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeMouseClicked.class, callbacks -> (client, screen, info, mouseX, mouseY, button) -> {
			for (ScreenEvents.BeforeMouseClicked callback : callbacks) {
				if (callback.beforeMouseClicked(client, screen, info, mouseX, mouseY, button)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenEvents.AfterMouseClicked> createAfterMouseClickedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterMouseClicked.class, callbacks -> (client, screen, info, mouseX, mouseY, button) -> {
			for (ScreenEvents.AfterMouseClicked callback : callbacks) {
				callback.afterMouseClicked(client, screen, info, mouseX, mouseY, button);
			}
		});
	}

	public static Event<ScreenEvents.BeforeMouseReleased> createBeforeMouseReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeMouseReleased.class, callbacks -> (client, screen, info, mouseX, mouseY, button) -> {
			for (ScreenEvents.BeforeMouseReleased callback : callbacks) {
				if (callback.beforeMouseReleased(client, screen, info, mouseX, mouseY, button)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenEvents.AfterMouseReleased> createAfterMouseReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterMouseReleased.class, callbacks -> (client, screen, info, mouseX, mouseY, button) -> {
			for (ScreenEvents.AfterMouseReleased callback : callbacks) {
				callback.afterMouseReleased(client, screen, info, mouseX, mouseY, button);
			}
		});
	}

	private ScreenEventFactory() {
	}
}
