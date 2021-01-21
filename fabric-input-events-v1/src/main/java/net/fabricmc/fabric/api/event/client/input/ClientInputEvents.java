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

package net.fabricmc.fabric.api.event.client.input;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientInputEvents {
	/**
	 * Called when the player presses a key.
	 */
	public static final Event<KeyState> KEY_PRESSED = EventFactory.createArrayBacked(KeyState.class, listeners -> (code, scancode, action, modKeys, key) -> {
		for (KeyState listener : listeners) {
			listener.onKey(code, scancode, action, modKeys, key);
		}
	});
	/**
	 * Called when the player releases a key.
	 */
	public static final Event<KeyState> KEY_RELEASED = EventFactory.createArrayBacked(KeyState.class, listeners -> (code, scancode, action, modKeys, key) -> {
		for (KeyState listener : listeners) {
			listener.onKey(code, scancode, action, modKeys, key);
		}
	});
	/**
	 * Called when the player holds a key for a while.
	 */
	public static final Event<KeyState> KEY_REPEATED = EventFactory.createArrayBacked(KeyState.class, listeners -> (code, scancode, action, modKeys, key) -> {
		for (KeyState listener : listeners) {
			listener.onKey(code, scancode, action, modKeys, key);
		}
	});
	/**
	 * Called when the player presses a key that is bound to some keybind.
	 */
	public static final Event<KeybindState> KEYBIND_PRESSED = EventFactory.createArrayBacked(KeybindState.class, listeners -> (code, scancode, action, modKeys, key, binding) -> {
		for (KeybindState listener : listeners) {
			listener.onKeybind(code, scancode, action, modKeys, key, binding);
		}
	});
	/**
	 * Called when the player releases a key that is bound to some keybind.
	 */
	public static final Event<KeybindState> KEYBIND_RELEASED = EventFactory.createArrayBacked(KeybindState.class, listeners -> (code, scancode, action, modKeys, key, binding) -> {
		for (KeybindState listener : listeners) {
			listener.onKeybind(code, scancode, action, modKeys, key, binding);
		}
	});
	/**
	 * Called when the player holds a key that is bound to some keybind for a while.
	 */
	public static final Event<KeybindState> KEYBIND_REPEATED = EventFactory.createArrayBacked(KeybindState.class, listeners -> (code, scancode, action, modKeys, key, binding) -> {
		for (KeybindState listener : listeners) {
			listener.onKeybind(code, scancode, action, modKeys, key, binding);
		}
	});
	/**
	 * Called when the player types a character in some way.
	 *
	 * <p>CHAR_TYPED events need not have a corresponding KEY_PRESSED event,
	 * for example when pressing dead keys or using an Emoji menu.
	 */
	public static final Event<CharState> CHAR_TYPED = EventFactory.createArrayBacked(CharState.class, listeners -> (codepoint, modKeys) -> {
		for (CharState listener : listeners) {
			listener.onChar(codepoint, modKeys);
		}
	});
	/**
	 * Called when the player moves their mouse.
	 */
	public static final Event<MouseMove> MOUSE_MOVED = EventFactory.createArrayBacked(MouseMove.class, listeners -> (x, y, dx, dy) -> {
		for (MouseMove listener : listeners) {
			listener.onMouseMoved(x, y, dx, dy);
		}
	});
	/**
	 * Called when the player presses a button on their mouse.
	 */
	public static final Event<MouseButtonState> MOUSE_BUTTON_PRESSED = EventFactory.createArrayBacked(MouseButtonState.class, listeners -> (button, action, modKeys, key) -> {
		for (MouseButtonState listener : listeners) {
			listener.onMouseButton(button, action, modKeys, key);
		}
	});
	/**
	 * Called when the player releases a button on their mouse.
	 */
	public static final Event<MouseButtonState> MOUSE_BUTTON_RELEASED = EventFactory.createArrayBacked(MouseButtonState.class, listeners -> (button, action, modKeys, key) -> {
		for (MouseButtonState listener : listeners) {
			listener.onMouseButton(button, action, modKeys, key);
		}
	});
	/**
	 * Called when the player scrolls their mouse wheel.
	 */
	public static final Event<MouseScroll> MOUSE_WHEEL_SCROLLED = EventFactory.createArrayBacked(MouseScroll.class, listeners -> (dx, dy) -> {
		for (MouseScroll listener : listeners) {
			listener.onMouseScrolled(dx, dy);
		}
	});
	/**
	 * Called when the player drops a file into the Minecraft window.
	 */
	public static final Event<FileDrop> FILE_DROPPED = EventFactory.createArrayBacked(FileDrop.class, listeners -> paths -> {
		for (FileDrop listener : listeners) {
			listener.onFilesDropped(paths);
		}
	});

	@FunctionalInterface
	public interface KeyState {
		void onKey(int code, int scancode, int action, int modKeys, Key key);
	}

	@FunctionalInterface
	public interface KeybindState {
		void onKeybind(int code, int scancode, int action, int modKeys, Key key, KeyBinding binding);
	}

	@FunctionalInterface
	public interface CharState {
		void onChar(int codepoint, int modKeys);
	}

	@FunctionalInterface
	public interface MouseMove {
		void onMouseMoved(double x, double y, double dx, double dy);
	}

	@FunctionalInterface
	public interface MouseButtonState {
		void onMouseButton(int button, int action, int modKeys, Key key);
	}

	@FunctionalInterface
	public interface MouseScroll {
		void onMouseScrolled(double dx, double dy);
	}

	@FunctionalInterface
	public interface FileDrop {
		void onFilesDropped(String[] paths);
	}
}
