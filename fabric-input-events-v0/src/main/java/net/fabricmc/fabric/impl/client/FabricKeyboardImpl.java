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

package net.fabricmc.fabric.impl.client;

import net.fabricmc.fabric.api.client.FabricKeyboard;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public class FabricKeyboardImpl implements FabricKeyboard {
	public static FabricKeyboardImpl INSTANCE = new FabricKeyboardImpl();

	private int mods = 0;

	private FabricKeyboardImpl() {
	}

	@Override
	public boolean impl_isKeyPressed(int keycode, int scancode) {
		return InputUtil.isKeyPressed(keycode, scancode);
	}

	@Override
	public boolean impl_isKeyPressed(Key key) {
		return InputUtil.isKeyPressed(key.getCode(), -1);
	}

	@Override
	public int impl_getMods() {
		return this.mods;
	}

	public void updateMods(int mods) {
		this.mods = mods;
	}

}
