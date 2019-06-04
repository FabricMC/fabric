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

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods.
 * <p>
 * *ALL* instantiated FabricKeyBindings should be registered in
 * {@link KeyBindingRegistry#register(FabricKeyBinding)}!
 */
public class FabricKeyBinding extends KeyBinding {
	protected FabricKeyBinding(String name, InputUtil.Type type, int code, String category) {
		super(name, type, code, category);
	}

	public static class Builder {

		private InputUtil.Type type = InputUtil.Type.KEYSYM;

		private String keyName = "key.fabric.unnamed";

		private int code;

		private String category = KeyCategory.MISC;

		private Builder() {

		}

		public Builder name(String keyName) {
			this.keyName = keyName;
			return this;
		}

		public Builder name(Identifier keyName) {
			return name("key." + keyName.toString().replace(':', '.').replace('/', '/'));
		}

		public Builder category(String category) {
			this.category = category.contains("key.categories.") ? category : "key.categories." + category;
			return this;
		}

		public Builder code(int keyCode) {
			this.code = keyCode;
			return this;
		}

		public Builder type(InputUtil.Type type) {
			this.type = type;
			return this;
		}

		public FabricKeyBinding build() {
			return new FabricKeyBinding(keyName, type, code, category);
		}

		public static Builder create() {
			return new Builder();
		}

		@Deprecated
		public static Builder create(Identifier id, InputUtil.Type type, int code, String category) {
			return new Builder()
					.name(id)
					.type(type)
					.code(code)
					.category(category);
		}
	}
}
