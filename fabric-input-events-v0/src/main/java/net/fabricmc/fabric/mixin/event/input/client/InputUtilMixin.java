package net.fabricmc.fabric.mixin.event.input.client;

import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.fabric.api.client.event.input.CharEvent;
import net.fabricmc.fabric.api.client.event.input.ClientInputEvents;
import net.fabricmc.fabric.api.client.event.input.KeyEvent;
import net.fabricmc.fabric.api.client.event.input.KeybindEvent;
import net.fabricmc.fabric.api.client.event.input.MouseButtonEvent;
import net.fabricmc.fabric.api.client.event.input.MouseMoveEvent;
import net.fabricmc.fabric.api.client.event.input.MouseScrollEvent;
import net.fabricmc.fabric.impl.client.FabricKeyboardImpl;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

@Mixin(InputUtil.class)
public class InputUtilMixin {
	@ModifyVariable(method="setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V", at=@At("HEAD"), index=2)
	private static GLFWKeyCallbackI fabric_changeKeyCb(GLFWKeyCallbackI keyCb) {
		return (long window, int code, int scancode, int action, int mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			KeyEvent key = new KeyEvent(code, scancode, action, mods);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.KEY_PRESSED.invoker().onKey(key);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.KEY_RELEASED.invoker().onKey(key);
				break;
			case GLFW.GLFW_REPEAT:
				ClientInputEvents.KEY_REPEATED.invoker().onKey(key);
				break;
			}
			Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.fabric_getKeyToBindings();
			KeyBinding binding = keyToBindings.get(key.getKey());
			if (binding != null) {
				KeybindEvent keybind = new KeybindEvent(code, scancode, action, mods, binding);
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
			keyCb.invoke(window, code, scancode, action, mods);
		};
	}
	@ModifyVariable(method="setKeyboardCallbacks(JLorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharModsCallbackI;)V", at=@At("HEAD"), index=3)
	private static GLFWCharModsCallbackI fabric_changeCharModsCb(GLFWCharModsCallbackI charModsCb) {
		return (long window, int codepoint, int mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			ClientInputEvents.CHAR_TYPED.invoker().onChar(new CharEvent(codepoint, mods));
			charModsCb.invoke(window, codepoint, mods);
		};
	}

	private static boolean fabric_hasMoved = false;
	private static double fabric_lastX = 0.0;
	private static double fabric_lastY = 0.0;

	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=2)
	private static GLFWCursorPosCallbackI fabric_changeCursorPosCb(GLFWCursorPosCallbackI cursorPosCb) {
		return (long window, double x, double y) -> {
			double dx = fabric_hasMoved ? x - fabric_lastX : 0.0;
			double dy = fabric_hasMoved ? y - fabric_lastY : 0.0;
			ClientInputEvents.MOUSE_MOVED.invoker().onMouseMoved(new MouseMoveEvent(x, y, dx, dy));
			cursorPosCb.invoke(window, x, y);
			fabric_lastX = x;
			fabric_lastY = y;
			fabric_hasMoved = true;
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=3)
	private static GLFWMouseButtonCallbackI fabric_changeMouseButtonCb(GLFWMouseButtonCallbackI mouseButtonCb) {
		return (long window, int button, int action, int mods) -> {
			FabricKeyboardImpl.INSTANCE.updateMods(mods);
			MouseButtonEvent mouse = new MouseButtonEvent(button, action, mods);
			switch (action) {
			case GLFW.GLFW_PRESS:
				ClientInputEvents.MOUSE_BUTTON_PRESSED.invoker().onMouseButton(mouse);
				break;
			case GLFW.GLFW_RELEASE:
				ClientInputEvents.MOUSE_BUTTON_RELEASED.invoker().onMouseButton(mouse);
				break;
			}
			Map<InputUtil.Key, KeyBinding> keyToBindings = KeyBindingMixin.fabric_getKeyToBindings();
			KeyBinding binding = keyToBindings.get(mouse.getKey());
			if (binding != null) {
				KeybindEvent keybind = new KeybindEvent(button, -1, action, mods, binding);
				switch (action) {
				case GLFW.GLFW_PRESS:
					ClientInputEvents.KEYBIND_PRESSED.invoker().onKeybind(keybind);
					break;
				case GLFW.GLFW_RELEASE:
					ClientInputEvents.KEYBIND_RELEASED.invoker().onKeybind(keybind);
					break;
				}
			}
			mouseButtonCb.invoke(window, button, action, mods);
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=4)
	private static GLFWScrollCallbackI fabric_changeScrollCb(GLFWScrollCallbackI scrollCb) {
		return (long window, double dx, double dy) -> {
			MouseScrollEvent mouse = new MouseScrollEvent(dx, dy);
			ClientInputEvents.MOUSE_WHEEL_SCROLLED.invoker().onMouseScrolled(mouse);
			scrollCb.invoke(window, dx, dy);
		};
	}
	@ModifyVariable(method="setMouseCallbacks(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V", at=@At("HEAD"), index=5)
	private static GLFWDropCallbackI fabric_changeDropCb(GLFWDropCallbackI dropCb) {
		return (long window, int count, long names) -> {
			String[] paths = new String[count];
            for (int i = 0; i < count; ++i) {
                paths[i] = GLFWDropCallback.getName(names, i);
			}
			ClientInputEvents.FILE_DROPPED.invoker().onFilesDropped(paths);
			dropCb.invoke(window, count, names);
		};
	}
}
