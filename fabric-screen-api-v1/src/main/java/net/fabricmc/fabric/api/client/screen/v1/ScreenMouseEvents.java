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
 *
 * @see ScreenEvents
 */
@Environment(EnvType.CLIENT)
public final class ScreenMouseEvents {
	/**
	 * An event that is called before a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseClick> getAllowMouseClickEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseClickEvent();
	}

	public static Event<BeforeMouseClick> getBeforeMouseClickEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseClickEvent();
	}

	/**
	 * An event that is called after a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseClick> getAfterMouseClickEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseClickEvent();
	}

	/**
	 * An event that is called after the release of a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseRelease> getAllowMouseReleaseEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseReleaseEvent();
	}

	public static Event<BeforeMouseRelease> getBeforeMouseReleaseEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseReleaseEvent();
	}

	/**
	 * An event that is called after the release of a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseRelease> getAfterMouseReleaseEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseReleaseEvent();
	}

	/**
	 * An event that is called before mouse scrolling is processed for a screen.
	 *
	 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseScroll> getAllowMouseScrollEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseScrollEvent();
	}

	public static Event<BeforeMouseScroll> getBeforeMouseScrollEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeMouseScrollEvent();
	}

	/**
	 * An event that is called after mouse scrolling is processed for a screen.
	 *
	 * <p>This event tracks amount a mouse was scrolled both vertically and horizontally.
	 *
	 * @return the event
	 */
	public static Event<AfterMouseScroll> getAfterMouseScrollEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterMouseScrollEvent();
	}

	private ScreenMouseEvents() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AllowMouseClick {
		boolean allowMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseClick {
		void beforeMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseClick {
		void afterMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AllowMouseRelease {
		boolean allowMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseRelease {
		void beforeMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseRelease {
		void afterMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AllowMouseScroll {
		boolean allowMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseScroll {
		void beforeMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseScroll {
		void afterMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}
}
