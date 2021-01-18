package net.fabricmc.fabric.api.client.event.input;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.mixin.event.input.client.KeyBindingMixin;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

public final class KeybindEvent extends GenericKeyEvent {
	public final int code;
	public final int scancode;
	public final int action;
	public final int mods;
	public final KeyBinding keybind;

	public KeybindEvent(int code, int scancode, int action, int mods, KeyBinding keybind) {
		this.code = code;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
		this.keybind = keybind;
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public int getScancode() {
		return this.scancode;
	}

	@Override
	public int getMods() {
		return this.mods;
	}

	@Override
	public boolean isPressed() {
		return this.action != GLFW.GLFW_RELEASE;
	}

	@Override
	public Key getKey() {
		return ((KeyBindingMixin)this.keybind).fabric_getBoundKey();
	}

	public KeyBinding getKeybind() {
		return this.keybind;
	}
}
