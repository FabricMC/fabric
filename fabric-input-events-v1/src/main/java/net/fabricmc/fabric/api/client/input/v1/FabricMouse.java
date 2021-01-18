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

import net.fabricmc.fabric.impl.client.input.FabricMouseImpl;

public final class FabricMouse {
	private FabricMouse() {
	}

	public static double getX() {
		return FabricMouseImpl.INSTANCE.getX();
	}

	public static double getY() {
		return FabricMouseImpl.INSTANCE.getY();
	}

	public static int getPressedButtons() {
		return FabricMouseImpl.INSTANCE.getPressedButtons();
	}

	public static boolean isButtonPressed(int button) {
		return FabricMouseImpl.INSTANCE.isButtonPressed(button);
	}

	public static int getMods() {
		return FabricMouseImpl.INSTANCE.getMods();
	}
}
