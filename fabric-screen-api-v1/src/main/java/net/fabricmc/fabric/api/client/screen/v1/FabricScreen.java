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
import net.fabricmc.fabric.api.event.Event;

/**
 * Provides access to additional context a screen can hold.
 */
@Environment(EnvType.CLIENT)
public interface FabricScreen {
	/**
	 * Gets the screen's additional info.
	 *
	 * @param screen the screen
	 * @return the screen's context
	 */
	static FabricScreen getInfo(Screen screen) {
		return (FabricScreen) screen;
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
	 * An event that is called before a screen is ticked.
	 */
	Event<ScreenEvents.BeforeTick> getBeforeTickEvent();

	/**
	 * An event that is called after a screen is ticked.
	 */
	Event<ScreenEvents.AfterTick> getAfterTickEvent();

	/**
	 * An event that is called before a screen is rendered.
	 */
	Event<ScreenEvents.BeforeRender> getBeforeRenderEvent();

	/**
	 * An event that is called after a screen is rendered.
	 */
	Event<ScreenEvents.AfterRender> getAfterRenderEvent();

	KeyboardEvents getKeyboardEvents();

	MouseEvents getMouseEvents();

	/**
	 * Gets the backing screen.
	 *
	 * @return the screen
	 */
	Screen getScreen();

	interface KeyboardEvents {
		Event<ScreenEvents.BeforeKeyPressed> getBeforeKeyPressedEvent();

		Event<ScreenEvents.AfterKeyPressed> getAfterKeyPressedEvent();

		Event<ScreenEvents.BeforeKeyReleased> getBeforeKeyReleasedEvent();

		Event<ScreenEvents.AfterKeyReleased> getAfterKeyReleasedEvent();
	}

	interface MouseEvents {
		Event<ScreenEvents.BeforeMouseClicked> getBeforeMouseClickedEvent();

		Event<ScreenEvents.AfterMouseClicked> getAfterMouseClickedEvent();

		Event<ScreenEvents.BeforeMouseReleased> getBeforeMouseReleasedEvent();

		Event<ScreenEvents.AfterMouseReleased> getAfterMouseReleasedEvent();
	}
}

