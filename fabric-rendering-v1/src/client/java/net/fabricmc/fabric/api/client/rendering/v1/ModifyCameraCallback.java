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

package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.client.render.Camera;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired every time the world is rendered right after vanilla finishes updating the camera for its own
 * cases, such as rotations and translations for sleeping or for a third person view. This callback can be used for
 * adjusting the camera further to a modder's needs. The camera is updated often and therefore resets previous
 * transformations from the last render call.
 */
public interface ModifyCameraCallback {
	Event<ModifyCameraCallback> EVENT = EventFactory.createArrayBacked(ModifyCameraCallback.class,
			(listeners) -> (camera, area, focusedEntity, thirdPerson, inverseView, tickDelta) -> {
				for (ModifyCameraCallback listener : listeners) {
					listener.modifyCamera(camera, area, focusedEntity, thirdPerson, inverseView, tickDelta);
				}
			});

	/**
	 * @param camera This camera
	 * @param area World view from the world renderer
	 * @param focusedEntity The entity the camera view is from
	 * @param thirdPerson Whether the camera is in third-person
	 * @param inverseView If thirdPerson is true, this determines whether the camera is front-facing
	 * @param tickDelta Progress from last tick to next tick
	 */
	void modifyCamera(Camera camera, BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta);
}
