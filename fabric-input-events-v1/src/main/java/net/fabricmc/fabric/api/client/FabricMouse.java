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

import net.fabricmc.fabric.impl.client.FabricMouseImpl;

public interface FabricMouse {
	public static FabricMouse INSTANCE = FabricMouseImpl.INSTANCE;

	public double impl_getX();
	public double impl_getY();
	public int impl_getPressedButtons();
	public boolean impl_isButtonPressed(int button);
	public int impl_getMods();

	public static double getX() {
		return FabricMouse.INSTANCE.impl_getX();
	}
	public static double getY() {
		return FabricMouse.INSTANCE.impl_getY();
	}
	public static int getPressedButtons() {
		return FabricMouse.INSTANCE.impl_getPressedButtons();
	}
	public static boolean isButtonPressed(int button) {
		return FabricMouse.INSTANCE.impl_isButtonPressed(button);
	}
	public static int getMods() {
		return FabricMouse.INSTANCE.impl_getMods();
	}
}
