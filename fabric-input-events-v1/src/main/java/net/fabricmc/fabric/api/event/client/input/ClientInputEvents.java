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

import java.util.Objects;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.client.input.InputCallbacksImpl;

/**
 * Events related to user input.
 */
@Environment(EnvType.CLIENT)
public final class ClientInputEvents {
	// a special fake scancode we invented to indicate a mouse Key (rather than a keyboard Key) event.
	private static final int SPECIAL_MOUSE_KEY_SCANCODE = -2;

	/**
	 * Called when the player presses, releases, or holds a key.
	 */
	public static final Event<KeyChanged> KEY = EventFactory.createArrayBacked(KeyChanged.class, listeners -> (code, scancode, action, modKeys) -> {
		for (KeyChanged listener : listeners) {
			listener.onKeyChanged(code, scancode, action, modKeys);
		}
	});
	/**
	 * Called when the player presses a key.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * The invoker always invokes the {@link #KEYBIND_PRESSED} event,
	 * with a {@code null} keybind argument and the possibly computed key.
	 * @see #KEYBIND_PRESSED
	 */
	public static final Event<KeyState> KEY_PRESSED;
	/**
	 * Called when the player releases a key.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * The invoker always invokes the {@link #KEYBIND_RELEASED} event,
	 * with a {@code null} keybind argument and the possibly computed key.
	 * @see #KEYBIND_RELEASED
	 */
	public static final Event<KeyState> KEY_RELEASED;
	/**
	 * Called when the player holds a key for a while.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * The invoker always invokes the {@link #KEYBIND_REPEATED} event,
	 * with a {@code null} keybind argument and the possibly computed key.
	 * @see #KEYBIND_REPEATED
	 */
	public static final Event<KeyState> KEY_REPEATED;
	/**
	 * Called when the player presses a key that is bound to some keybind.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * If the invoker receives a {@code null} KeyBinding object, it will try
	 * to find the KeyBinding corresponding to the given Key, if necessairy.
	 * If the invoker cannot find the keybind, the listeners are not invoked.
	 * @see #KEY_PRESSED
	 * @see #MOUSE_BUTTON_PRESSED
	 */
	public static final Event<KeybindState> KEYBIND_PRESSED = createKeybindStateEvent();
	/**
	 * Called when the player releases a key that is bound to some keybind.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * If the invoker receives a {@code null} KeyBinding object, it will try
	 * to find the KeyBinding corresponding to the given Key, if necessairy.
	 * If the invoker cannot find the keybind, the listeners are not invoked.
	 * @see #KEY_RELEASED
	 * @see #MOUSE_BUTTON_RELEASED
	 */
	public static final Event<KeybindState> KEYBIND_RELEASED = createKeybindStateEvent();
	/**
	 * Called when the player holds a key that is bound to some keybind for a while.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * If the invoker receives a {@code null} KeyBinding object, it will try
	 * to find the KeyBinding corresponding to the given Key, if necessairy.
	 * If the invoker cannot find the keybind, the listeners are not invoked.
	 * @see #KEY_REPEATED
	 */
	public static final Event<KeybindState> KEYBIND_REPEATED = createKeybindStateEvent();
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
	 * Called when the player presses or releases a button on their mouse.
	 */
	public static final Event<MouseButtonChanged> MOUSE_BUTTON = EventFactory.createArrayBacked(MouseButtonChanged.class, listeners -> (button, action, modKeys) -> {
		for (MouseButtonChanged listener : listeners) {
			listener.onMouseButtonChanged(button, action, modKeys);
		}
	});
	/**
	 * Called when the player presses a button on their mouse.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * The invoker always invokes the {@link #KEYBIND_PRESSED} event,
	 * with a {@code null} keybind argument and the possibly computed key.
	 * @see #KEYBIND_PRESSED
	 */
	public static final Event<MouseButtonState> MOUSE_BUTTON_PRESSED;
	/**
	 * Called when the player releases a button on their mouse.
	 *
	 * @implSpec The listeners to this event will never receive any {@code null} arguments.
	 * @implNote If the invoker receives a {@code null} Key object, it will
	 * compute it from the given code and scancode, if necessairy.
	 * The invoker always invokes the {@link #KEYBIND_RELEASED} event,
	 * with a {@code null} keybind argument and the possibly computed key.
	 * @see #KEYBIND_RELEASED
	 */
	public static final Event<MouseButtonState> MOUSE_BUTTON_RELEASED;
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
	public interface KeyChanged {
		void onKeyChanged(int code, int scancode, int action, int modKeys);
	}

	@FunctionalInterface
	public interface KeyState {
		void onKey(int code, int scancode, int modKeys, Key key);
	}

	@FunctionalInterface
	public interface KeybindState {
		void onKeybind(int code, int scancode, int modKeys, Key key, KeyBinding binding);
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
	public interface MouseButtonChanged {
		void onMouseButtonChanged(int button, int action, int modKeys);
	}

	@FunctionalInterface
	public interface MouseButtonState {
		void onMouseButton(int button, int modKeys, Key key);
	}

	@FunctionalInterface
	public interface MouseScroll {
		void onMouseScrolled(double dx, double dy);
	}

	@FunctionalInterface
	public interface FileDrop {
		void onFilesDropped(String[] paths);
	}

	/**
	 * Utility method to save duplicate code for optimized keybind state event creation.
	 *
	 * @return the created keybind state event
	 */
	private static Event<KeybindState> createKeybindStateEvent() {
		return EventFactory.createArrayBacked(KeybindState.class,
				listeners -> (code, scancode, modKeys, nullableKey, nullableBinding) -> {
					Key key = nullableKey;
					KeyBinding binding = nullableBinding;

					boolean isMouseKey = false;

					if (scancode == SPECIAL_MOUSE_KEY_SCANCODE) {
						scancode = GLFW.GLFW_KEY_UNKNOWN;
						isMouseKey = true;
					}

					for (KeybindState listener : listeners) {
						if (key == null) {
							// compute the key on demand
							key = isMouseKey
								? InputCallbacksImpl.buttonToKey(code)
								: InputUtil.fromKeyCode(code, scancode);

							if (key == null) {
								// will probably never happen, just to be safe
								break;
							}
						}

						if (binding == null) {
							// try to find a binding for the key, on demand
							binding = InputCallbacksImpl.keyToBinding(key);

							if (binding == null) {
								// didn't find a binding; don't invoke the handlers
								break;
							}
						}

						listener.onKeybind(code, scancode, modKeys, key, binding);
					}
				});
	}

	/**
	 * Utility method to create mouse button state events that create objects only on demand and
	 * calls the keybind state events.
	 *
	 * @param keybindStateEvent the keybind state event to call
	 * @return the created mouse button state event
	 * @throws NullPointerException if the keybind state event is {@code null}
	 */
	private static Event<MouseButtonState> createMouseButtonStateEvent(Event<KeybindState> keybindStateEvent) {
		Objects.requireNonNull(keybindStateEvent); // safeguard against bad initializer code
		return EventFactory.createArrayBacked(MouseButtonState.class,
				listeners -> (button, modKeys, nullKey) -> {
					Key key = null;

					for (MouseButtonState listener : listeners) {
						if (key == null) {
							// compute they (mouse) key on demand
							key = InputCallbacksImpl.buttonToKey(button);

							if (key == null) {
								// will probably never happen, just to be safe
								break;
							}
						}

						listener.onMouseButton(button, modKeys, key);
					}

					// invoke the corresponding KeyBinding event with our possibly computed key
					keybindStateEvent.invoker().onKeybind(button, GLFW.GLFW_KEY_UNKNOWN, modKeys, key, null);
				});
	}

	/**
	 * Utility method to create key state events that create objects only on demand and
	 * calls the keybind state events.
	 *
	 * @param keybindStateEvent the keybind state event to call
	 * @return the created key state event
	 * @throws NullPointerException if the keybind state event is {@code null}
	 */
	private static Event<KeyState> createKeyStateEvent(Event<KeybindState> keybindStateEvent) {
		Objects.requireNonNull(keybindStateEvent); // safeguard against bad initializer code
		return EventFactory.createArrayBacked(KeyState.class,
				listeners -> (code, scancode, modKeys, nullKey) -> {
					Key key = null;

					for (KeyState listener : listeners) {
						if (key == null) {
							// compute the (keyboard) key on demand
							key = InputUtil.fromKeyCode(code, scancode);

							if (key == null) {
								// will probably never happen, just to be safe
								break;
							}
						}

						listener.onKey(code, scancode, modKeys, key);
					}

					// invoke the corresponding KeyBinding event with our possibly computed key
					keybindStateEvent.invoker().onKeybind(code, scancode, modKeys, key, null);
				});
	}

	static {
		// these must be called only after the KEYBIND static fields are initialized!
		// (code order is the order they appear in the java source file)
		// otherwise nulls are passed into these factory methods!
		KEY_PRESSED = createKeyStateEvent(KEYBIND_PRESSED);
		KEY_RELEASED = createKeyStateEvent(KEYBIND_RELEASED);
		KEY_REPEATED = createKeyStateEvent(KEYBIND_REPEATED);
		MOUSE_BUTTON_PRESSED = createMouseButtonStateEvent(KEYBIND_PRESSED);
		MOUSE_BUTTON_RELEASED = createMouseButtonStateEvent(KEYBIND_RELEASED);
	}
}
