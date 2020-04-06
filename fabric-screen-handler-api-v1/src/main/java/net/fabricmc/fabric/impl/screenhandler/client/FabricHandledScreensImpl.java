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

package net.fabricmc.fabric.impl.screenhandler.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.client.FabricHandledScreens;

@Environment(EnvType.CLIENT)
public final class FabricHandledScreensImpl implements FabricHandledScreens {
	public static final FabricHandledScreens INSTANCE = new FabricHandledScreensImpl();

	private FabricHandledScreensImpl() {
	}

	@Override
	public <T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> void register(ScreenHandlerType<? extends T> type, Factory<? super T, ? extends U> screenFactory) {
		// Convert our factory to the vanilla provider here as it won't be available to modders.
		HandledScreens.register(type, screenFactory::create);
	}
}
