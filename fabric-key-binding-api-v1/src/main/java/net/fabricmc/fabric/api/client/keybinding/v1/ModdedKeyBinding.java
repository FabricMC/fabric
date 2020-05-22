package net.fabricmc.fabric.api.client.keybinding.v1;

import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingUtil.getBoundKeyOf;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

public abstract class ModdedKeyBinding extends KeyBinding {
	public ModdedKeyBinding(String id, int keyCode, String category) {
		super(id, keyCode, category);
	}

	public ModdedKeyBinding(String id, InputUtil.Type type, int code, String category) {
		super(id, type, code, category);
	}

	/**
	 * Returns the configured KeyCode bound to the KeyBinding from the player's settings.
	 *
	 * @return configured KeyCode
	 */
	public InputUtil.KeyCode getBoundKey() {
		return getBoundKeyOf(this);
	}
}
