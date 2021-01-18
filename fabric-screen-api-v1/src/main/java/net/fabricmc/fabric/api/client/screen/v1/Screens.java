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
import java.util.Objects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.mixin.screen.ScreenAccessor;

/**
 * Utility methods related to screens.
 *
 * @see ScreenEvents
 */
@Environment(EnvType.CLIENT)
public final class Screens {
	/**
	 * Gets all of a screen's button widgets.
	 * The provided list allows for addition and removal of buttons from the screen.
	 * This method should be preferred over adding buttons directly to a screen's {@link Screen#children() child elements}.
	 *
	 * @return a list of all of a screen's buttons
	 */
	public static List<AbstractButtonWidget> getButtons(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getButtons();
	}

	/**
	 * Gets a screen's item renderer.
	 *
	 * @return the screen's item renderer
	 */
	public static ItemRenderer getItemRenderer(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ((ScreenAccessor) screen).getItemRenderer();
	}

	/**
	 * Gets a screen's text renderer.
	 *
	 * @return the screen's text renderer.
	 */
	public static TextRenderer getTextRenderer(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ((ScreenAccessor) screen).getTextRenderer();
	}

	public static MinecraftClient getClient(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ((ScreenAccessor) screen).getClient();
	}

	private Screens() {
	}
}
