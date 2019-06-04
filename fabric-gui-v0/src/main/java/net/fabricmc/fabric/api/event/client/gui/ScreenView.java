/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.fabricmc.fabric.api.event.client.gui;

import java.util.List;

import org.spongepowered.asm.mixin.Shadow;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

public interface ScreenView {

	Screen getScreen();

	/**
	 * Gets all the buttons currently added to the screen.
	 */
	List<AbstractButtonWidget> getButtons();

	/**
	 * Adds a new button to the screen's own button list.
	 * This is the same as calling `addButton(button)` on the screen
	 * itself and likewise add the button to the screen's elements list.
	 */
	<T extends AbstractButtonWidget> T addButton(T button);

	/**
	 * Adds a new button to the screen's own button list.
	 * This is the same as calling `addButton(button)` on the screen
	 * itself and likewise add the button to the screen's elements list.
	 */
	default <T extends AbstractButtonWidget> T addButton(T button, int tabOrdinal) {
		tabOrdinal = Math.min(Math.max(0, tabOrdinal), getButtons().size());

		getButtons().add(ordinal, button);
		return getScreen().children().add(ordinal, button);
	}

	default void removeButton(T button) {
		getScreen().children().remove(button);
		getButtons().remove(button);
	}
}
