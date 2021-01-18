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

package net.fabricmc.fabric.api.event.client.input;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

public final class KeyEvent extends GenericKeyEvent {
	public final int code;
	public final int scancode;
	public final int action;
	public final int modKeys;
	public final Key key;

	public KeyEvent(int code, int scancode, int action, int modKeys) {
		this.code = code;
		this.scancode = scancode;
		this.action = action;
		this.modKeys = modKeys;
		this.key = InputUtil.fromKeyCode(code, scancode);
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public int getScancode() {
		return this.scancode;
	}

	@Override
	public int getModKeys() {
		return this.modKeys;
	}

	@Override
	public boolean isPressed() {
		return this.action != GLFW.GLFW_RELEASE;
	}

	@Override
	public Key getKey() {
		return this.key;
	}
}
