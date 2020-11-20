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

package net.fabricmc.fabric.impl.client.screen;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;

@Environment(EnvType.CLIENT)
public interface ScreenExtensions {
	static ScreenExtensions getExtensions(Screen screen) {
		return (ScreenExtensions) screen;
	}

	List<AbstractButtonWidget> fabric_getButtons();

	Event<ScreenEvents.Remove> fabric_getRemoveEvent();

	Event<ScreenEvents.BeforeTick> fabric_getBeforeTickEvent();

	Event<ScreenEvents.AfterTick> fabric_getAfterTickEvent();

	Event<ScreenEvents.BeforeRender> fabric_getBeforeRenderEvent();

	Event<ScreenEvents.AfterRender> fabric_getAfterRenderEvent();

	// Keyboard

	Event<ScreenKeyboardEvents.BeforeKeyPressed> fabric_getBeforeKeyPressedEvent();

	Event<ScreenKeyboardEvents.AfterKeyPressed> fabric_getAfterKeyPressedEvent();

	Event<ScreenKeyboardEvents.BeforeKeyReleased> fabric_getBeforeKeyReleasedEvent();

	Event<ScreenKeyboardEvents.AfterKeyReleased> fabric_getAfterKeyReleasedEvent();

	// Mouse

	Event<ScreenMouseEvents.BeforeMouseClicked> fabric_getBeforeMouseClickedEvent();

	Event<ScreenMouseEvents.AfterMouseClicked> fabric_getAfterMouseClickedEvent();

	Event<ScreenMouseEvents.BeforeMouseReleased> fabric_getBeforeMouseReleasedEvent();

	Event<ScreenMouseEvents.AfterMouseReleased> fabric_getAfterMouseReleasedEvent();

	Event<ScreenMouseEvents.BeforeMouseScrolled> fabric_getBeforeMouseScrolledEvent();

	Event<ScreenMouseEvents.AfterMouseScrolled> fabric_getAfterMouseScrolledEvent();
}

