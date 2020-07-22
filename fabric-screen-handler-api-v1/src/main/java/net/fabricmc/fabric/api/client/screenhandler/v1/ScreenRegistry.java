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

package net.fabricmc.fabric.api.client.screenhandler.v1;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * An API for registering handled screens that represent screen handlers on the client.
 * Exposes vanilla's private {@link HandledScreens#register HandledScreens.register()} to modders as {@link #register ScreenRegistry.register()}.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * // In a client-side initialization method:
 * ScreenRegistry.register(MyScreenHandlers.OVEN, OvenScreen::new);
 *
 * // Screen class
 * public class OvenScreen extends HandledScreen<OvenScreenHandler> {
 * 	public OvenScreen(OvenScreenHandler handler, PlayerInventory inventory, Text title) {
 * 		super(handler, inventory, title);
 * 	}
 * }
 * }
 * </pre>
 *
 * @see net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry registering screen handlers
 */
@Environment(EnvType.CLIENT)
public final class ScreenRegistry {
	private ScreenRegistry() {
	}

	/**
	 * Registers a new screen factory for a screen handler type.
	 *
	 * @param type          the screen handler type object
	 * @param screenFactory the screen handler factory
	 * @param <H>           the screen handler type
	 * @param <S>           the screen type
	 */
	public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void register(ScreenHandlerType<? extends H> type, Factory<H, S> screenFactory) {
		// Convert our factory to the vanilla provider here as the vanilla interface won't be available to modders.
		HandledScreens.register(type, screenFactory::create);
	}

	/**
	 * A factory for handled screens.
	 *
	 * @param <H> the screen handler type
	 * @param <S> the screen type
	 */
	@FunctionalInterface
	public interface Factory<H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> {
		/**
		 * Creates a new handled screen.
		 *
		 * @param handler   the screen handler
		 * @param inventory the player inventory
		 * @param title     the title of the screen
		 * @return the created screen
		 */
		S create(H handler, PlayerInventory inventory, Text title);
	}
}
