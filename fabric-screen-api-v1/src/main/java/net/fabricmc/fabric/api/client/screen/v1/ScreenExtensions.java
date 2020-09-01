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
public interface ScreenExtensions {
	/**
	 * Gets the screen's additional info.
	 *
	 * @param screen the screen
	 * @return the screen's context
	 */
	static ScreenExtensions getExtensions(Screen screen) {
		return (ScreenExtensions) screen;
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
	 *
	 * @return the event
	 */
	Event<ScreenEvents.BeforeTick> getBeforeTickEvent();

	/**
	 * An event that is called after a screen is ticked.
	 *
	 * @return the event
	 */
	Event<ScreenEvents.AfterTick> getAfterTickEvent();

	/**
	 * An event that is called before a screen is rendered.
	 *
	 * @return the event
	 */
	Event<ScreenEvents.BeforeRender> getBeforeRenderEvent();

	/**
	 * An event that is called after a screen is rendered.
	 *
	 * @return the event
	 */
	Event<ScreenEvents.AfterRender> getAfterRenderEvent();

	/**
	 * Gets the containing object for all keyboard related events for this screen.
	 *
	 * @return the keyboard events object
	 */
	KeyboardEvents getKeyboardEvents();

	/**
	 * Gets the containing object for all mouse related events for this screen.
	 *
	 * @return the mouse events object
	 */
	MouseEvents getMouseEvents();

	/**
	 * Gets the backing screen.
	 *
	 * @return the screen
	 */
	Screen getScreen();

	interface KeyboardEvents {
		/**
		 * An event that is called before a key press is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.BeforeKeyPressed> getBeforeKeyPressedEvent();

		/**
		 * An event that is called after a key press is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.AfterKeyPressed> getAfterKeyPressedEvent();

		/**
		 * An event that is called after the release of a key is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.BeforeKeyReleased> getBeforeKeyReleasedEvent();

		/**
		 * An event that is called after the release a key is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.AfterKeyReleased> getAfterKeyReleasedEvent();
	}

	interface MouseEvents {
		/**
		 * An event that is called before a mouse click is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.BeforeMouseClicked> getBeforeMouseClickedEvent();

		/**
		 * An event that is called after a mouse click is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.AfterMouseClicked> getAfterMouseClickedEvent();

		/**
		 * An event that is called after the release of a mouse click is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.BeforeMouseReleased> getBeforeMouseReleasedEvent();

		/**
		 * An event that is called after the release of a mouse click is processed for a screen.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.AfterMouseReleased> getAfterMouseReleasedEvent();

		/**
		 * An event that is called before mouse scrolling is processed for a screen.
		 *
		 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.BeforeMouseScrolled> getBeforeMouseScrolledEvent();

		/**
		 * An event that is called after mouse scrolling is processed for a screen.
		 *
		 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
		 *
		 * @return the event
		 */
		Event<ScreenEvents.AfterMouseScrolled> getAfterMouseScrolledEvent();
	}
}

