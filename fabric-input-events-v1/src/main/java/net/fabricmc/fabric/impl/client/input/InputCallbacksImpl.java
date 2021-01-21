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

package net.fabricmc.fabric.impl.client.input;

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.fabric.api.event.client.input.ClientInputEvents;
import net.fabricmc.fabric.mixin.event.input.client.accessor.InputUtilTypeAccessor;
import net.fabricmc.fabric.mixin.event.input.client.accessor.KeyBindingAccessor;

public final class InputCallbacksImpl {
	public static void onKey(long window, int code, int scancode, int action, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);

		ClientInputEvents.KEY.invoker().onKeyChanged(code, scancode, action, modKeys);

		boolean hasListeners = ClientInputEvents.KEY_PRESSED.hasListeners()
							|| ClientInputEvents.KEY_RELEASED.hasListeners()
							|| ClientInputEvents.KEY_REPEATED.hasListeners()
							|| ClientInputEvents.KEYBIND_PRESSED.hasListeners()
							|| ClientInputEvents.KEYBIND_RELEASED.hasListeners()
							|| ClientInputEvents.KEYBIND_REPEATED.hasListeners();

		if (hasListeners) {
			Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingAccessor.getKeyToBindings();
			Key key = InputUtil.fromKeyCode(code, scancode);
			KeyBinding binding = keyToBindings.get(key);

			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEY_PRESSED.invoker().onKey(code, scancode, action, modKeys, key);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEY_RELEASED.invoker().onKey(code, scancode, action, modKeys, key);
				break;
			case GLFW.GLFW_REPEAT:
				ClientInputEvents.KEY_REPEATED.invoker().onKey(code, scancode, action, modKeys, key);
				break;
			}

			if (binding != null) {
				switch (action) {
				case GLFW.GLFW_PRESS:
					ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(code, scancode, action, modKeys, key, binding);
					break;
				case GLFW.GLFW_RELEASE:
					ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(code, scancode, action, modKeys, key, binding);
					break;
				case GLFW.GLFW_REPEAT:
					ClientInputEvents.KEYBIND_REPEATED.invoker().onKeybind(code, scancode, action, modKeys, key, binding);
					break;
				}
			}
		}
	}

	public static void onChar(long window, int codepoint, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);
		ClientInputEvents.CHAR_TYPED.invoker().onChar(codepoint, modKeys);
	}

	private static boolean hasMoved = false;
	private static double lastX = 0.0;
	private static double lastY = 0.0;

	public static void onMouseMoved(long window, double x, double y) {
		FabricMouseImpl.updatePosition(x, y);
		double dx = hasMoved ? x - lastX : 0.0;
		double dy = hasMoved ? y - lastY : 0.0;
		ClientInputEvents.MOUSE_MOVED.invoker().onMouseMoved(x, y, dx, dy);
		lastX = x;
		lastY = y;
		hasMoved = true;
	}

	private static final Int2ObjectMap<Key> buttonToKey = ((InputUtilTypeAccessor) (Object) InputUtil.Type.MOUSE).getMap();
	private static final Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingAccessor.getKeyToBindings();

	public static void onMouseButton(long window, int button, int action, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);
		FabricMouseImpl.updateButton(button, action != GLFW.GLFW_RELEASE);

		ClientInputEvents.MOUSE_BUTTON.invoker().onMouseButtonChanged(button, action, modKeys);

		boolean hasListeners = ClientInputEvents.MOUSE_BUTTON_PRESSED.hasListeners()
							|| ClientInputEvents.MOUSE_BUTTON_RELEASED.hasListeners()
							|| ClientInputEvents.KEYBIND_PRESSED.hasListeners()
							|| ClientInputEvents.KEYBIND_RELEASED.hasListeners();

		if (hasListeners) {
			Key key = buttonToKey.get(button);
			KeyBinding binding = keyToBindings.get(key);

			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.MOUSE_BUTTON_PRESSED.invoker().onMouseButton(button, action, modKeys, key);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.MOUSE_BUTTON_RELEASED.invoker().onMouseButton(button, action, modKeys, key);
				break;
			}

			if (binding != null) {
				switch (action) {
				case GLFW.GLFW_PRESS:
					ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(button, -1, action, modKeys, key, binding);
					break;
				case GLFW.GLFW_RELEASE:
					ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(button, -1, action, modKeys, key, binding);
					break;
				}
			}
		}
	}

	public static void onMouseScrolled(long window, double dx, double dy) {
		ClientInputEvents.MOUSE_WHEEL_SCROLLED.invoker().onMouseScrolled(dx, dy);
	}

	public static void onFilesDropped(long window, int count, long names) {
		String[] paths = new String[count];

		for (int i = 0; i < count; ++i) {
			paths[i] = GLFWDropCallback.getName(names, i);
		}

		ClientInputEvents.FILE_DROPPED.invoker().onFilesDropped(paths);
	}
}
