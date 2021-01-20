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

import net.fabricmc.fabric.api.client.input.v1.FabricMouse;
import net.fabricmc.fabric.api.event.client.input.ClientInputEvents;
import net.fabricmc.fabric.api.event.client.input.KeyEvent;
import net.fabricmc.fabric.api.event.client.input.KeybindEvent;
import net.fabricmc.fabric.api.event.client.input.MouseButtonEvent;
import net.fabricmc.fabric.api.event.client.input.MouseMoveEvent;
import net.fabricmc.fabric.api.event.client.input.MouseScrollEvent;
import net.fabricmc.fabric.mixin.event.input.client.InputUtilTypeMixin;
import net.fabricmc.fabric.mixin.event.input.client.KeyBindingMixin;
import net.fabricmc.fabric.mixin.event.input.client.accessor.KeyEventAccessor;
import net.fabricmc.fabric.mixin.event.input.client.accessor.KeybindEventAccessor;
import net.fabricmc.fabric.mixin.event.input.client.accessor.MouseButtonEventAccessor;
import net.fabricmc.fabric.mixin.event.input.client.accessor.MouseMoveEventAccessor;
import net.fabricmc.fabric.mixin.event.input.client.accessor.GenericMouseEventAccessor;

public final class InputCallbacksImpl {
	private static final KeyEvent sharedKeyEvent = new KeyEvent(-1, -1, GLFW.GLFW_RELEASE, 0);
	private static final KeybindEvent sharedKeybindEvent = new KeybindEvent(-1, -1, GLFW.GLFW_RELEASE, 0, null, null);
	private static final MouseMoveEvent sharedMouseMoveEvent = new MouseMoveEvent(0.0, 0.0, 0.0, 0.0);
	private static final MouseButtonEvent sharedMouseButtonEvent = new MouseButtonEvent(0, GLFW.GLFW_RELEASE, 0, null);
	private static final MouseScrollEvent sharedMouseScrollEvent = new MouseScrollEvent(0.0, 0.0);

	private static KeyEvent createKeyEvent(int code, int scancode, int action, int modKeys) {
		KeyEventAccessor accessor = (KeyEventAccessor) (Object) sharedKeyEvent;
		accessor.setCode(code);
		accessor.setScancode(scancode);
		accessor.setAction(action);
		accessor.setModKeys(modKeys);
		accessor.setKey(InputUtil.fromKeyCode(code, scancode));
		return sharedKeyEvent;
	}

	private static KeybindEvent createKeybindEvent(int code, int scancode, int action, int modKeys, Key key, KeyBinding keybind) {
		KeybindEventAccessor accessor = (KeybindEventAccessor) (Object) sharedKeybindEvent;
		accessor.setCode(code);
		accessor.setScancode(scancode);
		accessor.setAction(action);
		accessor.setModKeys(modKeys);
		accessor.setKey(key);
		accessor.setKeybind(keybind);
		return sharedKeybindEvent;
	}

	private static MouseMoveEvent createMouseMoveEvent(double x, double y, double dx, double dy) {
		MouseMoveEventAccessor accessor = (MouseMoveEventAccessor) (Object) sharedMouseMoveEvent;
		accessor.setX(x);
		accessor.setY(y);
		accessor.setDeltaX(dx);
		accessor.setDeltaY(dy);
		GenericMouseEventAccessor genericAccessor = (GenericMouseEventAccessor) (Object) sharedMouseMoveEvent;
		genericAccessor.setCursorX(x);
		genericAccessor.setCursorY(y);
		genericAccessor.setCursorDeltaX(dx);
		genericAccessor.setCursorDeltaY(dy);
		genericAccessor.setPressedButtons(FabricMouse.getPressedButtons());
		genericAccessor.setPressedModKeys(FabricMouse.getModKeys());
		return sharedMouseMoveEvent;
	}

	private static MouseButtonEvent createMouseButtonEvent(int button, int action, int modKeys, Key key) {
		MouseButtonEventAccessor accessor = (MouseButtonEventAccessor) (Object) sharedMouseButtonEvent;
		accessor.setButton(button);
		accessor.setAction(action);
		accessor.setModKeys(modKeys);
		accessor.setKey(key);
		GenericMouseEventAccessor genericAccessor = (GenericMouseEventAccessor) (Object) sharedMouseScrollEvent;
		genericAccessor.setPressedButtons(button);
		genericAccessor.setPressedModKeys(modKeys);
		genericAccessor.setCursorX(FabricMouse.getX());
		genericAccessor.setCursorY(FabricMouse.getY());
		return sharedMouseButtonEvent;
	}

	private static MouseScrollEvent createMouseScrollEvent(double dx, double dy) {
		GenericMouseEventAccessor genericAccessor = (GenericMouseEventAccessor) (Object) sharedMouseScrollEvent;
		genericAccessor.setScrollX(dx);
		genericAccessor.setScrollY(dy);
		genericAccessor.setCursorX(FabricMouse.getX());
		genericAccessor.setCursorY(FabricMouse.getY());
		genericAccessor.setPressedButtons(FabricMouse.getPressedButtons());
		genericAccessor.setPressedModKeys(FabricMouse.getModKeys());
		return sharedMouseScrollEvent;
	}

	public static void onKey(long window, int code, int scancode, int action, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);
		FabricMouseImpl.update();
		KeyEvent keyEvent = createKeyEvent(code, scancode, action, modKeys);
		switch (action) {
		case GLFW.GLFW_PRESS:
			ClientInputEvents.KEY_PRESSED.invoker().onKey(keyEvent);
			break;
		case GLFW.GLFW_RELEASE:
			ClientInputEvents.KEY_RELEASED.invoker().onKey(keyEvent);
			break;
		case GLFW.GLFW_REPEAT:
			ClientInputEvents.KEY_REPEATED.invoker().onKey(keyEvent);
			break;
		}

		Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.getKeyToBindings();
		Key key = keyEvent.getKey();
		KeyBinding binding = keyToBindings.get(key);

		if (binding != null) {
			KeybindEvent keybind = createKeybindEvent(code, scancode, action, modKeys, key, binding);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
				break;
			case GLFW.GLFW_REPEAT:
				ClientInputEvents.KEYBIND_REPEATED.invoker().onKeybind(keybind);
				break;
			}
		}
	}

	public static void onChar(long window, int codepoint, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);
		FabricMouseImpl.update();
		ClientInputEvents.CHAR_TYPED.invoker().onChar(codepoint, modKeys);
	}

	private static boolean hasMoved = false;
	private static double lastX = 0.0;
	private static double lastY = 0.0;

	public static void onMouseMoved(long window, double x, double y) {
		FabricMouseImpl.update();
		double dx = hasMoved ? x - lastX : 0.0;
		double dy = hasMoved ? y - lastY : 0.0;
		MouseMoveEvent mouse = createMouseMoveEvent(x, y, dx, dy);
		ClientInputEvents.MOUSE_MOVED.invoker().onMouseMoved(mouse);
		lastX = x;
		lastY = y;
		hasMoved = true;
	}

	private static final Int2ObjectMap<Key> buttonToKey = ((InputUtilTypeMixin) (Object) InputUtil.Type.MOUSE).getMap();
	private static final Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.getKeyToBindings();

	public static void onMouseButton(long window, int button, int action, int modKeys) {
		FabricKeyboardImpl.updateModKeys(modKeys);
		FabricMouseImpl.update();
		Key key = buttonToKey.get(button);
		KeyBinding binding = keyToBindings.get(key);
		MouseButtonEvent mouse = createMouseButtonEvent(button, action, modKeys, key);

		switch (action) {
		case GLFW.GLFW_PRESS:
			ClientInputEvents.MOUSE_BUTTON_PRESSED.invoker().onMouseButton(mouse);
			break;
		case GLFW.GLFW_RELEASE:
			ClientInputEvents.MOUSE_BUTTON_RELEASED.invoker().onMouseButton(mouse);
			break;
		}

		if (binding != null) {
			KeybindEvent keybind = createKeybindEvent(button, -1, action, modKeys, key, binding);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
				break;
			}
		}
	}

	public static void onMouseScrolled(long window, double dx, double dy) {
		MouseScrollEvent mouse = createMouseScrollEvent(dx, dy);
		ClientInputEvents.MOUSE_WHEEL_SCROLLED.invoker().onMouseScrolled(mouse);
	}

	public static void onFilesDropped(long window, int count, long names) {
		String[] paths = new String[count];

		for (int i = 0; i < count; ++i) {
			paths[i] = GLFWDropCallback.getName(names, i);
		}

		ClientInputEvents.FILE_DROPPED.invoker().onFilesDropped(paths);
	}
}
