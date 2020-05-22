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

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.fabricmc.fabric.mixin.client.keybinding.KeyCodeAccessor;

/**
 * Interface for registering key bindings.
 *
 * @see FabricKeyBinding
 */
public final class KeyBindingUtil {
	private KeyBindingUtil() {
	}

	/**
	 * Returns the configured KeyCode bound to the KeyBinding from the player's settings.
	 *
	 * @param keyBinding the keybinding
	 * @return configured KeyCode
	 */
	public static InputUtil.KeyCode getBoundKeyOf(KeyBinding keyBinding) {
		return ((KeyCodeAccessor) keyBinding).getKeyCode();
	}
}
