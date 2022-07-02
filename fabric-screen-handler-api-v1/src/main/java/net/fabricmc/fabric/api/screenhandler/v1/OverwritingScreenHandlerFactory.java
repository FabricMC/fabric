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

package net.fabricmc.fabric.api.screenhandler.v1;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;

/**
 * A {@link net.minecraft.screen.SimpleNamedScreenHandlerFactory} that
 * "overwrites" the current screen without closing it.
 * In vanilla game, opening a new screen will always send the close screen packet.
 * This, among other things, causes the mouse cursor to move to the center of the screen,
 * which might not be expected in some cases. Opening a screen handler with this factory
 * skips sending the packet and "overwrites" the current screen, preventing the cursor
 * movement.
 */
public final class OverwritingScreenHandlerFactory extends SimpleNamedScreenHandlerFactory {
	public OverwritingScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text name) {
		super(baseFactory, name);
	}

	public OverwritingScreenHandlerFactory(NamedScreenHandlerFactory baseFactory) {
		this(baseFactory, baseFactory.getDisplayName());
	}

	@Override
	public boolean shouldCloseCurrentScreen() {
		return false;
	}
}
