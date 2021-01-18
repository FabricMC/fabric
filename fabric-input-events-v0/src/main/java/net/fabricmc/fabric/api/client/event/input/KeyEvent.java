package net.fabricmc.fabric.api.client.event.input;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public final class KeyEvent extends GenericKeyEvent {
	public final int code;
	public final int scancode;
	public final int action;
	public final int mods;
	public final Key key;

	public KeyEvent(int code, int scancode, int action, int mods) {
		this.code = code;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
		this.key = InputUtil.fromKeyCode(code, scancode);
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
		return this.key;
	}
}
