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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Factory methods for creating event instances used in {@link ScreenExtensions}.
 */
@Environment(EnvType.CLIENT)
public final class ScreenEventFactory {
	public static Event<ScreenEvents.Remove> createRemoveEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.Remove.class, callbacks -> () -> {
			for (ScreenEvents.Remove callback : callbacks) {
				callback.onRemove();
			}
		});
	}

	public static Event<ScreenEvents.BeforeRender> createBeforeRenderEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeRender.class, callbacks -> (matrices, mouseX, mouseY, tickDelta) -> {
			for (ScreenEvents.BeforeRender callback : callbacks) {
				callback.beforeRender(matrices, mouseX, mouseY, tickDelta);
			}
		});
	}

	public static Event<ScreenEvents.AfterRender> createAfterRenderEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterRender.class, callbacks -> (matrices, mouseX, mouseY, tickDelta) -> {
			for (ScreenEvents.AfterRender callback : callbacks) {
				callback.afterRender(matrices, mouseX, mouseY, tickDelta);
			}
		});
	}

	public static Event<ScreenEvents.BeforeTick> createBeforeTickEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.BeforeTick.class, callbacks -> () -> {
			for (ScreenEvents.BeforeTick callback : callbacks) {
				callback.beforeTick();
			}
		});
	}

	public static Event<ScreenEvents.AfterTick> createAfterTickEvent() {
		return EventFactory.createArrayBacked(ScreenEvents.AfterTick.class, callbacks -> () -> {
			for (ScreenEvents.AfterTick callback : callbacks) {
				callback.afterTick();
			}
		});
	}

	public static Event<ScreenKeyboardEvents.BeforeKeyPressed> createBeforeKeyPressedEvent() {
		return EventFactory.createArrayBacked(ScreenKeyboardEvents.BeforeKeyPressed.class, callbacks -> (key, scancode, modifiers) -> {
			for (ScreenKeyboardEvents.BeforeKeyPressed callback : callbacks) {
				if (callback.beforeKeyPress(key, scancode, modifiers)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenKeyboardEvents.AfterKeyPressed> createAfterKeyPressedEvent() {
		return EventFactory.createArrayBacked(ScreenKeyboardEvents.AfterKeyPressed.class, callbacks -> (key, scancode, modifiers) -> {
			for (ScreenKeyboardEvents.AfterKeyPressed callback : callbacks) {
				callback.afterKeyPress(key, scancode, modifiers);
			}
		});
	}

	public static Event<ScreenKeyboardEvents.BeforeKeyReleased> createBeforeKeyReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenKeyboardEvents.BeforeKeyReleased.class, callbacks -> (key, scancode, modifiers) -> {
			for (ScreenKeyboardEvents.BeforeKeyReleased callback : callbacks) {
				if (callback.beforeKeyReleased(key, scancode, modifiers)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenKeyboardEvents.AfterKeyReleased> createAfterKeyReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenKeyboardEvents.AfterKeyReleased.class, callbacks -> (key, scancode, modifiers) -> {
			for (ScreenKeyboardEvents.AfterKeyReleased callback : callbacks) {
				callback.afterKeyReleased(key, scancode, modifiers);
			}
		});
	}

	//

	public static Event<ScreenMouseEvents.BeforeMouseClicked> createBeforeMouseClickedEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.BeforeMouseClicked.class, callbacks -> (mouseX, mouseY, button) -> {
			for (ScreenMouseEvents.BeforeMouseClicked callback : callbacks) {
				if (callback.beforeMouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenMouseEvents.AfterMouseClicked> createAfterMouseClickedEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.AfterMouseClicked.class, callbacks -> (mouseX, mouseY, button) -> {
			for (ScreenMouseEvents.AfterMouseClicked callback : callbacks) {
				callback.afterMouseClicked(mouseX, mouseY, button);
			}
		});
	}

	public static Event<ScreenMouseEvents.BeforeMouseReleased> createBeforeMouseReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.BeforeMouseReleased.class, callbacks -> (mouseX, mouseY, button) -> {
			for (ScreenMouseEvents.BeforeMouseReleased callback : callbacks) {
				if (callback.beforeMouseReleased(mouseX, mouseY, button)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenMouseEvents.AfterMouseReleased> createAfterMouseReleasedEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.AfterMouseReleased.class, callbacks -> (mouseX, mouseY, button) -> {
			for (ScreenMouseEvents.AfterMouseReleased callback : callbacks) {
				callback.afterMouseReleased(mouseX, mouseY, button);
			}
		});
	}

	public static Event<ScreenMouseEvents.BeforeMouseScrolled> createBeforeMouseScrolledEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.BeforeMouseScrolled.class, callbacks -> (mouseX, mouseY, horizontalAmount, verticalAmount) -> {
			for (ScreenMouseEvents.BeforeMouseScrolled callback : callbacks) {
				if (callback.beforeMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
					return true;
				}
			}

			return false;
		});
	}

	public static Event<ScreenMouseEvents.AfterMouseScrolled> createAfterMouseScrolledEvent() {
		return EventFactory.createArrayBacked(ScreenMouseEvents.AfterMouseScrolled.class, callbacks -> (mouseX, mouseY, horizontalAmount, verticalAmount) -> {
			for (ScreenMouseEvents.AfterMouseScrolled callback : callbacks) {
				callback.afterMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
			}
		});
	}

	private ScreenEventFactory() {
	}
}
