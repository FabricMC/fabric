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
 * Events related to use of the mouse in a {@link Screen}.
 *
 * @see ScreenEvents
 */
@Environment(EnvType.CLIENT)
public final class ScreenMouseEvents {
	/**
	 * An event that checks if the mouse click should be allowed.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseClick> getAllowMouseClickEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseClickEvent();
	}

	/**
	 * An event that is called before a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
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
	 * An event that checks if the mouse click should be allowed to release in a screen.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseRelease> getAllowMouseReleaseEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseReleaseEvent();
	}

	/**
	 * An event that is called before the release of a mouse click is processed for a screen.
	 *
	 * @return the event
	 */
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
	 * An event that is checks if the mouse should be allowed to scroll in a screen.
	 *
	 * <p>This event tracks amount of vertical and horizontal scroll.
	 *
	 * @return the event
	 */
	public static Event<AllowMouseScroll> getAllowMouseScrollEvent(Screen screen) {
		Objects.requireNonNull(screen, "Screen cannot be null");

		return ScreenExtensions.getExtensions(screen).fabric_getAllowMouseScrollEvent();
	}

	/**
	 * An event that is called after mouse scrolling is processed for a screen.
	 *
	 * <p>This event tracks amount of vertical and horizontal scroll.
	 *
	 * @return the event
	 */
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
		/**
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		boolean allowMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseClick {
		/**
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		void beforeMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseClick {
		/**
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		void afterMouseClick(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AllowMouseRelease {
		/**
		 * Checks if the mouse click should be allowed to release in a screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		boolean allowMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseRelease {
		/**
		 * Called before a mouse click has released in a screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		void beforeMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseRelease {
		/**
		 * Called after a mouse click has released in a screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param button the button number, which can be identified by the constants in {@link org.lwjgl.glfw.GLFW GLFW}.
		 * @see org.lwjgl.glfw.GLFW#GLFW_MOUSE_BUTTON_1
		 */
		void afterMouseRelease(double mouseX, double mouseY, int button);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AllowMouseScroll {
		/**
		 * Checks if the mouse should be allowed to scroll in a screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param horizontalAmount the horizontal scroll amount
		 * @param verticalAmount the vertical scroll amount
		 * @return whether the mouse should be allowed to scroll
		 */
		boolean allowMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeMouseScroll {
		/**
		 * Called before a mouse has scrolled on screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param horizontalAmount the horizontal scroll amount
		 * @param verticalAmount the vertical scroll amount
		 */
		void beforeMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterMouseScroll {
		/**
		 * Called after a mouse has scrolled on screen.
		 *
		 * @param mouseX the x position of the mouse
		 * @param mouseY the y position of the mouse
		 * @param horizontalAmount the horizontal scroll amount
		 * @param verticalAmount the vertical scroll amount
		 */
		void afterMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}
}
