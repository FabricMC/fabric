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

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods.
 *
 * <p>*ALL* instantiated FabricKeyBindings should be registered in
 * {@link KeyBindingRegistry#register(FabricKeyBinding)}!</p>
 *
 * @deprecated Please migrate to v1. Please use {@link KeyBindingHelper#registerKeyBinding(KeyBinding)} instead.
 */
@Deprecated
public class FabricKeyBinding extends KeyBinding {
	protected FabricKeyBinding(Identifier id, InputUtil.Type type, int code, String category) {
		super(String.format("key.%s.%s", id.getNamespace(), id.getPath()), type, code, category);
	}

	/**
	 * Returns the configured KeyCode assigned to the KeyBinding from the player's settings.
	 *
	 * @return configured KeyCode
	 */
	@Deprecated
	public InputUtil.Key getBoundKey() {
		return KeyBindingHelper.getBoundKeyOf(this);
	}

	@Deprecated
	public static class Builder {
		protected final FabricKeyBinding binding;

		protected Builder(FabricKeyBinding binding) {
			this.binding = binding;
		}

		public FabricKeyBinding build() {
			return binding;
		}

		public static Builder create(Identifier id, InputUtil.Type type, int code, String category) {
			return new Builder(new FabricKeyBinding(id, type, code, category));
		}
	}
}
