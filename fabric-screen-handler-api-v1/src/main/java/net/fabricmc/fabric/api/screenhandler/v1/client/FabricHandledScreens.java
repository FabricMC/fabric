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

package net.fabricmc.fabric.api.screenhandler.v1.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.screenhandler.client.FabricHandledScreensImpl;

/**
 * An utility for registering screens with screen handlers.
 */
@Environment(EnvType.CLIENT)
public interface FabricHandledScreens {
	FabricHandledScreens INSTANCE = FabricHandledScreensImpl.INSTANCE;

	/**
	 * Registers a new screen factory for a screen handler type.
	 *
	 * @param type          the screen handler type object
	 * @param screenFactory the screen handler factory
	 * @param <T>           the screen handler type
	 * @param <U>           the screen type
	 */
	<T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> void register(ScreenHandlerType<? extends T> type, Factory<? super T, ? extends U> screenFactory);

	/**
	 * A factory for handled screens.
	 *
	 * @param <T> the screen handler type
	 * @param <U> the screen type
	 */
	@FunctionalInterface
	interface Factory<T extends ScreenHandler, U extends Screen & ScreenHandlerProvider<T>> {
		/**
		 * Creates a new handled screen.
		 *
		 * @param handler   the screen handler
		 * @param inventory the player inventory
		 * @param title     the title of the screen
		 * @return the created screen
		 */
		U create(T handler, PlayerInventory inventory, Text title);
	}
}
