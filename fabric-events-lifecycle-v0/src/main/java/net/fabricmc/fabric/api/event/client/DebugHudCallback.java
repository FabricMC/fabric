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

import java.util.List;

public interface DebugHudCallback {

    /** Used to add debug lines to the right side of the screen */
	Event<DebugHudCallback> EVENT_RIGHT = EventFactory.createArrayBacked(DebugHudCallback.class, (listeners) ->
		(list) -> {
			for(DebugHudCallback callback : listeners){
				callback.debugHudText(list);
			}
		}
	);

    /** Used to add debug lines to the left side of the screen */
	Event<DebugHudCallback> EVENT_LEFT = EventFactory.createArrayBacked(DebugHudCallback.class, (listeners) ->
		(list) -> {
			for(DebugHudCallback callback : listeners){
				callback.debugHudText(list);
			}
		}
	);

    /**
     * Called when the debug HUD is rendered. Strings added to {@code lines} will be
     * rendered on the corresponding side of the screen.
     * @param lines the list containing the lines of text displayed on the debug HUD
     */
    void debugHudText(List<String> lines);

}
