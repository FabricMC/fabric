package net.fabricmc.fabric.api.client.keybinding.v1;

import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingUtil.getBoundKeyOf;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class FabricKeyBinding extends KeyBinding {
	private final Identifier identifier;

	/**
	 * @deprecated Internal, Do not use!
	 */
	@Deprecated
	protected FabricKeyBinding(Identifier identifier, String translationKey, InputUtil.Type type, int code, String category) {
		super(translationKey, type, code, category);

		this.identifier = identifier;
	}

	/**
	 * Returns the configured KeyCode bound to the KeyBinding from the player's settings.
	 *
	 * @return configured KeyCode
	 */
	public InputUtil.KeyCode getBoundKey() {
		return getBoundKeyOf(this);
	}

	/**
	 * Original identifier used to register this key.
	 *
	 * <p>Should be different from the {@link #getId()}.</p>
	 */
	public final Identifier getIdentifier() {
		return this.identifier;
	}
}
