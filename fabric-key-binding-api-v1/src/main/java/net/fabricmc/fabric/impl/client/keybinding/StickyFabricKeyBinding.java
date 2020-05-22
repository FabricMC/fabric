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

package net.fabricmc.fabric.impl.client.keybinding;

import java.util.function.BooleanSupplier;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.keybinding.v1.FabricKeyBinding;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods that is sticky.
 *
 * <p>*ALL* built FabricKeyBindings are automatically registered!</p>
 *
 * <pre><code>
 * FabricKeyBinding left = FabricKeyBinding.builder()
 * 			.id(new Identifier("example", "left"))
 * 			.key(InputUtil.Type.KEYSYM, Keys.Left)
 * 			.build();
 * FabricKeyBinding right = FabricKeyBinding.builder()
 * 			.id(new Identifier("example", "right"))
 * 			.key(InputUtil.Type.KEYSYM, Keys.Right)
 * 			.build();
 * </code></pre>
 */
public final class StickyFabricKeyBinding extends FabricKeyBinding {
	private final BooleanSupplier toggled;

	@SuppressWarnings("deprecation")
	public StickyFabricKeyBinding(Identifier identifier, String translationKey, Type type, int code, String category, BooleanSupplier toggled) {
		super(identifier, translationKey, type, code, category);
		this.toggled = toggled;
	}

	@Override
	public void setPressed(boolean pressed) {
		if (toggled.getAsBoolean()) {
			if (pressed) {
				super.setPressed(!isPressed());
			}
		} else {
			super.setPressed(pressed);
		}
	}
}
