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

package net.fabricmc.fabric.impl.client.input;

public final class FabricMouseImpl {
	private static double x = 0.0;
	private static double y = 0.0;
	private static int buttons = 0;

	private FabricMouseImpl() {
	}

	public static double getX() {
		return FabricMouseImpl.x;
	}

	public static double getY() {
		return FabricMouseImpl.y;
	}

	public static int getPressedButtons() {
		return FabricMouseImpl.buttons;
	}

	public static boolean isButtonPressed(int button) {
		return (FabricMouseImpl.buttons & (1 << button)) != 0;
	}

	public static void updatePosition(double x, double y) {
		FabricMouseImpl.x = x;
		FabricMouseImpl.y = y;
	}

	public static void updateButton(int button, boolean pressed) {
		if (pressed) {
			FabricMouseImpl.buttons |= (1 << button);
		} else {
			FabricMouseImpl.buttons &= ~(1 << button);
		}
	}
}
