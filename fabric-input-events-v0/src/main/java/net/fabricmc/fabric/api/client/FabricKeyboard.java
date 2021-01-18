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

package net.fabricmc.fabric.api.client;

import net.fabricmc.fabric.impl.client.FabricKeyboardImpl;
import net.minecraft.client.util.InputUtil.Key;

public interface FabricKeyboard {
	public static FabricKeyboard INSTANCE = FabricKeyboardImpl.INSTANCE;

	public boolean impl_isKeyPressed(int keycode, int scancode);
	public boolean impl_isKeyPressed(Key key);
	public int impl_getMods();

	public static boolean isKeyPressed(int keycode, int scancode) {
		return FabricKeyboard.INSTANCE.impl_isKeyPressed(keycode, scancode);
	}
	public static boolean isKeyPressed(Key key) {
		return FabricKeyboard.INSTANCE.impl_isKeyPressed(key);
	}
	public static int getMods() {
		return FabricKeyboard.INSTANCE.impl_getMods();
	}
}
