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
 * Events related to use of the keyboard in a {@link Screen screen}.
 *
 * @see ScreenEvents
 */
@Environment(EnvType.CLIENT)
public final class ScreenKeyboardEvents {
	/**
	 * An event that is called before a key press is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<BeforeKeyPressed> getBeforeKeyPressedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeKeyPressedEvent();
	}

	/**
	 * An event that is called after a key press is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<ScreenKeyboardEvents.AfterKeyPressed> getAfterKeyPressedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterKeyPressedEvent();
	}

	/**
	 * An event that is called after the release of a key is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<ScreenKeyboardEvents.BeforeKeyReleased> getBeforeKeyReleasedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeKeyReleasedEvent();
	}

	/**
	 * An event that is called after the release a key is processed for a screen.
	 *
	 * @return the event
	 */
	public static Event<ScreenKeyboardEvents.AfterKeyReleased> getAfterKeyReleasedEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterKeyReleasedEvent();
	}

	private ScreenKeyboardEvents() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeKeyPressed {
		boolean beforeKeyPress(int key, int scancode, int modifiers);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterKeyPressed {
		void afterKeyPress(int key, int scancode, int modifiers);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeKeyReleased {
		boolean beforeKeyReleased(int key, int scancode, int modifiers);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterKeyReleased {
		void afterKeyReleased(int key, int scancode, int modifiers);
	}
}
