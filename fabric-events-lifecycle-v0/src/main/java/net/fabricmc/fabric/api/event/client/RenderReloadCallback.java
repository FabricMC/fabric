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

package net.fabricmc.fabric.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Called before chunks are reloaded due to resource pack or video config
 * changes, or when the player types F3+A in the debug screen.<p>
 *     
 * Render chunks and other render-related object instances will be made null
 * or invalid after this event so do not use it to capture state.
 * Instead, use it to invalidate state as a signal to reinitialize lazily.
 */
public interface RenderReloadCallback {
	public static final Event<RenderReloadCallback> EVENT = EventFactory.createArrayBacked(RenderReloadCallback.class,
		(listeners) -> () -> {
			for (RenderReloadCallback event : listeners) {
				event.reload();
			}
		}
	);

	void reload();
}
