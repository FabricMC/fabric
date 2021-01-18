package net.fabricmc.fabric.api.client;

import net.fabricmc.fabric.impl.client.FabricKeyboardImpl;
import net.minecraft.client.util.InputUtil.Key;

public interface FabricKeyboard {
	public static FabricKeyboard INSTANCE = FabricKeyboardImpl.INSTANCE;

	public boolean impl_isKeyPressed(int keycode, int scancode);
	public boolean impl_isKeyPressed(Key key);
	public int impl_getMods();

	public static boolean isKeyPressed(int keycode, int scancode) {
		return FabricKeyboard.INSTANCE.impl_isKeyPressed(keycode, scancode);
	}
	public static boolean isKeyPressed(Key key) {
		return FabricKeyboard.INSTANCE.impl_isKeyPressed(key);
	}
	public static int getMods() {
		return FabricKeyboard.INSTANCE.impl_getMods();
	}
}
