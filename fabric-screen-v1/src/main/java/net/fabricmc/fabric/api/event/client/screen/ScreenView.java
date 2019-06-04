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

package net.fabricmc.fabric.api.event.client.screen;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

/**
 * Abstract view of a screen that allows for adding or removing buttons,
 * maybe more in the future.
 */
public interface ScreenView {

	/**
	 * Utility method to convert a screen into an abstract view of its contents.
	 */
	static ScreenView of(Screen screen) {
		return (ScreenView)screen;
	}

	/**
	 * Gets the screen backed by this view.
	 * @return
	 */
	Screen getScreen();

	/**
	 * Gets all the buttons currently added to the screen.
	 */
	List<AbstractButtonWidget> getButtons();

	/**
	 * Adds a new button to the screen's own button list.
	 *
	 * This is the same as calling `addButton(button)` on the screen
	 * itself and likewise add the button to the screen's elements list.
	 */
	<T extends AbstractButtonWidget> T addButton(T button);

	/**
	 * Adds a new button to the screen's own button list.
	 *
	 * This is the same as calling `addButton(button)` on the screen
	 * itself and likewise add the button to the screen's elements list.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <T extends AbstractButtonWidget> T addButton(int index, T button) {
		getButtons().add(index, button);
		((List)getScreen().children()).add(index, button);
		return button;
	}

	/**
	 * Removes a button from the screen's internal buttons and elements lists.
	 */
	default <T extends AbstractButtonWidget> void removeButton(T button) {
		getScreen().children().remove(button);
		getButtons().remove(button);
	}
}
