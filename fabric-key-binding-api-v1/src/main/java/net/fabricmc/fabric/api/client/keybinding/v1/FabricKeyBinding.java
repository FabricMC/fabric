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
import net.minecraft.util.Identifier;

public class FabricKeyBinding extends KeyBinding {
	private final Identifier identifier;

	/**
	 * @deprecated Do not use! {@link KeyBindingUtil#builder()}
	 */
	@Deprecated
	protected FabricKeyBinding(Identifier identifier, String translationKey, InputUtil.Type type, int code, String category) {
		super(translationKey, type, code, category);

		this.identifier = identifier;
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
