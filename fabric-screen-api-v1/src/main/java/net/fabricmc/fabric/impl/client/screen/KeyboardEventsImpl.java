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

import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;

public final class KeyboardEventsImpl implements FabricScreen.KeyboardEvents {
	private final Event<ScreenEvents.BeforeKeyPressed> beforeKeyPressedEvent = ScreenEventFactory.createBeforeKeyPressedEvent();
	private final Event<ScreenEvents.AfterKeyPressed> afterKeyPressedEvent = ScreenEventFactory.createAfterKeyPressedEvent();
	private final Event<ScreenEvents.BeforeKeyReleased> beforeKeyReleasedEvent = ScreenEventFactory.createBeforeKeyReleasedEvent();
	private final Event<ScreenEvents.AfterKeyReleased> afterKeyReleasedEvent = ScreenEventFactory.createAfterKeyReleasedEvent();

	@Override
	public Event<ScreenEvents.BeforeKeyPressed> getBeforeKeyPressedEvent() {
		return this.beforeKeyPressedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterKeyPressed> getAfterKeyPressedEvent() {
		return this.afterKeyPressedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeKeyReleased> getBeforeKeyReleasedEvent() {
		return this.beforeKeyReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterKeyReleased> getAfterKeyReleasedEvent() {
		return this.afterKeyReleasedEvent;
	}
}
