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

import java.util.Objects;

import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;

/**
 * Events related to use of the mouse in a {@link Screen screen}.
 */
@Environment(EnvType.CLIENT)
public final class ScreenMouseEvents {
	/**
	 * An event that is called before a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<BeforeMouseClicked> getBeforeMouseClickedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseClickedEvent();
	}

	/**
	 * An event that is called after a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseClicked> getAfterMouseClickedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseClickedEvent();
	}

	/**
	 * An event that is called after the release of a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<BeforeMouseReleased> getBeforeMouseReleasedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseReleasedEvent();
	}

	/**
	 * An event that is called after the release of a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseReleased> getAfterMouseReleasedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseReleasedEvent();
	}

	/**
	 * An event that is called before mouse scrolling is processed for a screen.
	 *
	 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
	 *
	 * @return the event
	 */
	public static Event<BeforeMouseScrolled> getBeforeMouseScrolledEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseScrolledEvent();
	}

	/**
	 * An event that is called after mouse scrolling is processed for a screen.
	 *
	 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseScrolled> getAfterMouseScrolledEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseScrolledEvent();
	}

	private ScreenMouseEvents() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseClicked {
		boolean beforeMouseClicked(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseClicked {
		void afterMouseClicked(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseReleased {
		boolean beforeMouseReleased(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseReleased {
		void afterMouseReleased(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseScrolled {
		boolean beforeMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseScrolled {
		void afterMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}
}
