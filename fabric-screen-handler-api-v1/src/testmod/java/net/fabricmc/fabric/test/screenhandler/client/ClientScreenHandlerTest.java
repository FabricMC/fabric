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

package net.fabricmc.fabric.test.screenhandler.client;

import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.test.screenhandler.ScreenHandlerTest;
import net.fabricmc.api.ClientModInitializer;

public class ClientScreenHandlerTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ScreenRegistry.register(ScreenHandlerTest.BAG_SCREEN_HANDLER, Generic3x3ContainerScreen::new);
		ScreenRegistry.register(ScreenHandlerTest.POSITIONED_BAG_SCREEN_HANDLER, PositionedScreen::new);
		ScreenRegistry.register(ScreenHandlerTest.BOX_SCREEN_HANDLER, PositionedScreen::new);
	}
}
