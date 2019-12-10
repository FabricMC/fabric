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

package net.fabricmc.fabric.api.client.keybinding;

import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import com.google.common.base.Preconditions;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods.
 *
 * <p>*ALL* instantiated FabricKeyBindings should be registered in
 * {@link KeyBindingRegistry#register(FabricKeyBinding)}!
 * </p>
 * <pre><code>
 * FabricKeyBinding.Builder builder = FabricKeyBinding.builder();
 * FabricKeyBinding left = builder
 *			.id(new Identifier("example", "left"))
 *			.code(Keys.Left)
 *			.build();
 * FabricKeyBinding right = builder
 *			.id(new Identifier("example", "right"))
 *			.code(Keys.Right)
 *			.build();
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
	 * May be different from the {@link getId()}.
	 */
	public Identifier getIdentifier() {
		return id;
	}

	/**
	 * Returns the configured KeyCode assigned to the KeyBinding from the player's settings.
	 * @return configured KeyCode
	 */
	public InputUtil.KeyCode getBoundKey() {
		return ((KeyCodeAccessor) this).getKeyCode();
	}

	/**
	 * Creates a new builder for constructing custom key bindings.
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private static final int UNASSIGNED = InputUtil.UNKNOWN_KEYCODE.getKeyCode();

		private InputUtil.Type type = InputUtil.Type.KEYSYM;

		private BooleanSupplier toggleFlagSupplier = null;

		private Identifier id = null;
		private String translationKey;

		private boolean unassigned = false;
		private boolean registered = false;

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
			this.id = id;
			this.translationKey = String.format("key.%s.%s", id.getNamespace(), id.getPath());
			return this;
		}

		/**
		 * Sets the translation key for the key's category. {@see KeyCategories} for
		 * all possible values, vanilla values.
		 *
		 * @param category The category under which key bindings created by this builder will be grouped.
		 */
		public Builder category(String category) {
			this.category = category;
			return this;
		}

		/**
		 * Sets this builder to create sticky beybindings that will toggle their state when pressed.
		 */
		public Builder sticky() {
			return sticky(() -> true);
		}

		/**
		 * Sets a sticking action to be used by the constructed key binding which can be used to switch between
		 * a sticky (toggle) and a regular key binding.
		 *
		 * @param toggleGetter A getter function to determine whether to toggle or not. True for toggling behaviour, false otherwise.
		 */
		public Builder sticky(BooleanSupplier toggleFlagSupplier) {
			this.toggleFlagSupplier = toggleFlagSupplier;
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
		 * Sets this builder to auto-register any key bindings created using it.
		 * <p>
		 * Mods who intend to register their own key bindings manually may choose not to use this.
		 */
		public Builder registered() {
			this.registered = true;
			return this;
		}

		/**
		 * Sets the key's type. Maybe be one of [KEYSYM (keyboard), SCANCODE, MOUSE]
		 *
		 * @param type The binding type.
		 */
		public Builder type(InputUtil.Type type) {
			this.type = type;
			return this;
		}

		/**
		 * Returns a key binding with a matching configuration to that of this builder.
		 * <p>
		 * Implementation Note:
		 * <p>
		 * At current this returns a <i>new</i> key binding that modders should
		 * hold onto for their own use, though this may change in the future.
		 */
		public FabricKeyBinding build() {
			Objects.requireNonNull(id, "Keybindings should be created with an identifier.");
			Preconditions.checkState(unassigned || code != UNASSIGNED, "Keybingings need a default keycode.");

			FabricKeyBinding binding;
			if (toggleFlagSupplier == null) {
				binding = new FabricKeyBinding(id, translationKey, type, code, category);
			} else {
				binding = new StickyFabricKeyBinding(id, translationKey, type, code, category, toggleFlagSupplier);
			}

			if (registered) {
				KeyBindingRegistry.INSTANCE.register(binding);
			}

			return binding;
		}

		/**
		 * @deprecated Prefer using the builder itself.
		 */
		@Deprecated
		public static Builder create(Identifier id, InputUtil.Type type, int code, String category) {
			return builder().id(id).type(type).code(code).category(category);
		}
	}
}
