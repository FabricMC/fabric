/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.keybinding;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class KeyBindingModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyBindingRegistry.INSTANCE.addCategory("fabric.test");
		KeyBindingRegistry.INSTANCE.register(
			FabricKeyBinding.Builder.create(
				new Identifier("fabric:test"),
				InputUtil.Type.KEYSYM,
				37,
				"fabric.test"
			).build()
		);
	}
}
