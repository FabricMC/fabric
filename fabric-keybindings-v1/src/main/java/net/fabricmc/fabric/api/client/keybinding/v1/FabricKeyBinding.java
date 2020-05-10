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

package net.fabricmc.fabric.api.client.keybinding.v1;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import com.google.common.base.Preconditions;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.keybinding.StickyFabricKeyBinding;
import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods.
 *
 * <p>*ALL* instantiated FabricKeyBindings should be registered in
 * {@link KeyBindingRegistry#register(FabricKeyBinding)}!
 * </p>
 *
 * <pre><code>
 * FabricKeyBinding.Builder builder = FabricKeyBinding.builder();
 * FabricKeyBinding left = builder
 * 			.id(new Identifier("example", "left"))
 * 			.code(Keys.Left)
 * 			.build();
 * FabricKeyBinding right = builder
 * 			.id(new Identifier("example", "right"))
 * 			.code(Keys.Right)
 * 			.build();
 * KeyBindingRegistry.register(left);
 * KeyBindingRegistry.register(right);
 * </code></pre>
 */
public class FabricKeyBinding extends KeyBinding {
	private final Identifier id;

	protected FabricKeyBinding(Identifier id, String translationKey, InputUtil.Type type, int code, String category) {
		super(translationKey, type, code, category);
		this.id = id;
	}

	/**
	 * Original identifier used to register this key.
	 *
	 * <p>May be different from the {@link #getId()}.</p>
	 */
	public Identifier getIdentifier() {
		return id;
	}

	/**
	 * Creates a new builder for constructing custom key bindings.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns the configured KeyCode assigned to the KeyBinding from the player's settings.
	 *
	 * @return configured KeyCode
	 */
	public InputUtil.KeyCode getBoundKey() {
		return getBoundKeyOf(this);
	}

	/**
	 * Returns the configured KeyCode assigned to the KeyBinding from the player's settings.
	 *
	 * @param keyBinding the keybinding
	 * @return configured KeyCode
	 */
	public static InputUtil.KeyCode getBoundKeyOf(KeyBinding keyBinding) {
		return ((KeyCodeAccessor) keyBinding).getKeyCode();
	}

	public static class Builder {
		private static final int UNASSIGNED = InputUtil.UNKNOWN_KEYCODE.getKeyCode();

		private InputUtil.Type type = InputUtil.Type.KEYSYM;
		private Identifier id = null;
		private String translationKey;
		private boolean unassigned = false;
		private boolean automaticallyRegister = false;
		private BooleanSupplier toggleFlagSupplier = null;
		private int code = UNASSIGNED;
		private String category = KeyCategories.MISC;

		private Builder() {
		}

		/**
		 * Sets the key binding id and translation key for this builder.
		 * <br>
		 * Key bindings will be assigned a translation key of the format "key.{namespace}.{path}"
		 *
		 * @param id Unique identifier for the bound key.
		 */
		public Builder id(Identifier id) {
			this.id = Objects.requireNonNull(id, "Keybinding's id can not be null!");
			this.translationKey = String.format("key.%s.%s", id.getNamespace(), id.getPath());
			return this;
		}

		/**
		 * Sets the translation key for the key's category. {@link KeyCategories} for
		 * all possible values, vanilla values.
		 *
		 * @param category The category under which key bindings created by this builder will be grouped.
		 */
		public Builder category(String category) {
			this.category = Objects.requireNonNull(category, "Keybinding's category can not be null!");
			return this;
		}

		/**
		 * Sets the default key to be used for the key binding created using this builder.
		 *
		 * @param keyCode The default key code. Must be a valid key. May not be -1.
		 */
		public Builder code(int keyCode) {
			Preconditions.checkState(keyCode != UNASSIGNED, "UNASSIGNED is not a valid key code.");
			this.code = keyCode;
			this.unassigned = false;
			return this;
		}

		/**
		 * Indicates to this builder that keybindings built through it are intended to be unbound.
		 */
		public Builder unassigned() {
			this.code = UNASSIGNED;
			this.unassigned = true;
			return this;
		}

		/**
		 * Sets this builder to create sticky keybindings that will toggle their state when pressed.
		 */
		public Builder sticky() {
			return sticky(() -> true);
		}

		/**
		 * Sets a sticking action to be used by the constructed key binding which can be used to switch between
		 * a sticky (toggle) and a regular key binding.
		 *
		 * @param toggleFlagSupplier A getter function to determine whether to toggle or not. True for toggling behaviour, false otherwise.
		 */
		public Builder sticky(BooleanSupplier toggleFlagSupplier) {
			this.toggleFlagSupplier = toggleFlagSupplier;
			return this;
		}

		/**
		 * Sets this builder to auto-register any key bindings created using it.
		 *
		 * <p>Mods who intend to register their own key bindings manually may choose not to use this.</p>
		 */
		public Builder automaticallyRegister() {
			this.automaticallyRegister = true;
			return this;
		}

		/**
		 * Sets the key's type. Maybe be one of [{@link InputUtil.Type#KEYSYM} (keyboard), {@link InputUtil.Type#SCANCODE}, {@link InputUtil.Type#MOUSE}]
		 *
		 * @param type The binding type.
		 */
		public Builder type(InputUtil.Type type) {
			this.type = Objects.requireNonNull(type, "Keybinding's type can not be null!");
			return this;
		}

		/**
		 * Returns a key binding with a matching configuration to that of this builder.
		 *
		 * <p>Implementation Note:</p>
		 *
		 * <p>At current this returns a <i>new</i> key binding that modders should
		 * hold onto for their own use, though this may change in the future.</p>
		 */
		public FabricKeyBinding build() {
			Objects.requireNonNull(id, "Keybindings should be created with an identifier.");
			Preconditions.checkState(unassigned || code != UNASSIGNED, "Keybindings need a default keycode.");
			FabricKeyBinding binding;

			if (toggleFlagSupplier == null) {
				binding = new FabricKeyBinding(id, translationKey, type, code, category);
			} else {
				binding = new StickyFabricKeyBinding(id, translationKey, type, code, category, toggleFlagSupplier);
			}

			if (automaticallyRegister) {
				KeyBindingRegistry.INSTANCE.register(binding);
			}

			return binding;
		}
	}
}
