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

public final class MouseEventsImpl implements FabricScreen.MouseEvents {
	private final Event<ScreenEvents.BeforeMouseClicked> beforeMouseClickedEvent = ScreenEventFactory.createBeforeMouseClickedEvent();
	private final Event<ScreenEvents.AfterMouseClicked> afterMouseClickedEvent = ScreenEventFactory.createAfterMouseClickedEvent();
	private final Event<ScreenEvents.BeforeMouseReleased> beforeMouseReleasedEvent = ScreenEventFactory.createBeforeMouseReleasedEvent();
	private final Event<ScreenEvents.AfterMouseReleased> afterMouseReleasedEvent = ScreenEventFactory.createAfterMouseReleasedEvent();
	private final Event<ScreenEvents.BeforeMouseScrolled> beforeMouseScrolledEvent = ScreenEventFactory.createBeforeMouseScrolledEvent();
	private final Event<ScreenEvents.AfterMouseScrolled> afterMouseScrolledEvent = ScreenEventFactory.createAfterMouseScrolledEvent();

	@Override
	public Event<ScreenEvents.BeforeMouseClicked> getBeforeMouseClickedEvent() {
		return this.beforeMouseClickedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterMouseClicked> getAfterMouseClickedEvent() {
		return this.afterMouseClickedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeMouseReleased> getBeforeMouseReleasedEvent() {
		return this.beforeMouseReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterMouseReleased> getAfterMouseReleasedEvent() {
		return this.afterMouseReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeMouseScrolled> getBeforeMouseScrolledEvent() {
		return this.beforeMouseScrolledEvent;
	}

	@Override
	public Event<ScreenEvents.AfterMouseScrolled> getAfterMouseScrolledEvent() {
		return this.afterMouseScrolledEvent;
	}
}
