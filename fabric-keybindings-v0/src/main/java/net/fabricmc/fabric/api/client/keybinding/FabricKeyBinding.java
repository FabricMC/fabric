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

	protected FabricKeyBinding(Identifier id, InputUtil.Type type, int code, String category) {
		super(formatKeyName(id), type, code, category);
		this.id = id;
	}

	public static String formatKeyName(Identifier id) {
		return String.format("key.%s.%s", id.getNamespace(), id.getPath());
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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private InputUtil.Type type = InputUtil.Type.KEYSYM;

		private Identifier id = null;

		private boolean unassigned = false;
		private int code = InputUtil.UNKNOWN_KEYCODE.getKeyCode();

		private String category = KeyCategories.MISC;

		private Builder() {
		}

		public Builder id(Identifier keyName) {
			this.id = keyName;
			return this;
		}

		public Builder category(String category) {
			this.category = category;
			return this;
		}

		public Builder code(int keyCode) {
			this.code = keyCode;
			return this;
		}

		public Builder unassigned() {
			return this;
		}

		public Builder type(InputUtil.Type type) {
			this.type = type;
			return this;
		}

		public FabricKeyBinding build() {
			Objects.requireNonNull(id, "Keybindings should be created with an identifier.");

			if (!unassigned && code == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
				throw new IllegalStateException("Keybingings need a default keycode.");
			}

			return new FabricKeyBinding(id, type, code, category);
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
