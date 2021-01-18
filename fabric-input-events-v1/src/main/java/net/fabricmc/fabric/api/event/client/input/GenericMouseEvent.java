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

public abstract class GenericMouseEvent {
	public final double cursorX;
	public final double cursorY;
	public final double cursorDeltaX;
	public final double cursorDeltaY;
	public final int pressedButtons;
	public final int pressedModKeys;
	public final double scrollX;
	public final double scrollY;

	public GenericMouseEvent(double x, double y, double dx, double dy, int buttons, int modKeys, double scrollX, double scrollY) {
		this.cursorX = x;
		this.cursorY = y;
		this.cursorDeltaX = dx;
		this.cursorDeltaY = dy;
		this.pressedButtons = buttons;
		this.pressedModKeys = modKeys;
		this.scrollX = scrollX;
		this.scrollY = scrollY;
	}

	public GenericMouseEvent(double x, double y, double dx, double dy, int buttons, int modKeys) {
		this(x, y, dx, dy, buttons, modKeys, 0.0, 0.0);
	}
}
