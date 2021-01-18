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

package net.fabricmc.fabric.api.client.input.v1;

import net.minecraft.client.util.InputUtil.Key;

import net.fabricmc.fabric.impl.client.input.FabricKeyboardImpl;

public final class FabricKeyboard {
	private FabricKeyboard() {
	}

	public static boolean isKeyPressed(int keycode, int scancode) {
		return FabricKeyboardImpl.INSTANCE.isKeyPressed(keycode, scancode);
	}

	public static boolean isKeyPressed(Key key) {
		return FabricKeyboardImpl.INSTANCE.isKeyPressed(key);
	}

	public static int getMods() {
		return FabricKeyboardImpl.INSTANCE.getMods();
	}
}
