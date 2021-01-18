package net.fabricmc.fabric.api.client.event.input;

import net.minecraft.client.util.InputUtil.Key;

public abstract class GenericKeyEvent {
	public abstract int getCode();
	public abstract int getScancode();
	public abstract int getMods();
	public abstract boolean isPressed();
	public abstract Key getKey();
}
