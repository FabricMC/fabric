package net.fabricmc.fabric.impl.client;

import net.fabricmc.fabric.api.client.FabricKeyboard;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class FabricKeyboardImpl implements FabricKeyboard {
	public static FabricKeyboardImpl INSTANCE = new FabricKeyboardImpl();

	private int mods = 0;

	private FabricKeyboardImpl() {
	}

	@Override
	public boolean impl_isKeyPressed(int keycode, int scancode) {
		return InputUtil.isKeyPressed(keycode, scancode);
	}

	@Override
	public boolean impl_isKeyPressed(Key key) {
		return InputUtil.isKeyPressed(key.getCode(), -1);
	}

	@Override
	public int impl_getMods() {
		return this.mods;
	}

	public void updateMods(int mods) {
		this.mods = mods;
	}

}
