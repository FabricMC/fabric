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

package net.fabricmc.fabric.api.client.screen.v1;

import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Provides access to additional context a screen can hold.
 */
@Environment(EnvType.CLIENT)
public interface ScreenContext {
	/**
	 * Gets the screen's context.
	 *
	 * @param screen the screen
	 * @return the screen's context
	 */
	static ScreenContext from(Screen screen) {
		return (ScreenContext) screen;
	}

	/**
	 * Gets all the screen's button widgets.
	 *
	 * @return a list of all the buttons
	 */
	List<AbstractButtonWidget> getButtons();

	/**
	 * Gets the screen's item renderer.
	 *
	 * @return a item renderer
	 */
	ItemRenderer getItemRenderer();

	/**
	 * Gets the screen's text renderer.
	 *
	 * @return a text renderer.
	 */
	TextRenderer getTextRenderer();

	/**
	 * Gets the screen which owns this context.
	 *
	 * @return the screen
	 */
	Screen getScreen();
}

