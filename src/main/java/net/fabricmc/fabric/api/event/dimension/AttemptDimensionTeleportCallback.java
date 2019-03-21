/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.event.dimension;

import net.fabricmc.fabric.api.dimension.EntityPlacer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

public interface AttemptDimensionTeleportCallback {

    Event<AttemptDimensionTeleportCallback> EVENT = EventFactory.createArrayBacked(AttemptDimensionTeleportCallback.class, listeners -> (entity, from, to) -> {
        EventResult currentResult = EventResult.DEFAULT;
        for(AttemptDimensionTeleportCallback callback : listeners) {
            EventResult result = callback.placeEntity(entity, from, to);
            if(result == EventResult.CANCEL_TELEPORT) {
                return EventResult.CANCEL_TELEPORT;
            }
            else if(result != EventResult.DEFAULT) {
                currentResult = result;
            }
        }
        return currentResult;
    });

    /**
     * fired to allow mods to intercept the teleportation of an entity to another dimension
     * this hook can take priority over default behaviour, or even cancel the teleportation
     *
     * @return an {@link EventResult} to determine further processing of the teleport
     */
    EventResult placeEntity(Entity entity, DimensionType from, DimensionType to);

    enum EventResult {
        /**
         * if you don't want to have the default {@link EntityPlacer} handle entity placement and portal creation
         */
        SKIP_FURTHER_PROCESSING,
        /**
         * if you want to prevent the entity from changing dimensions and stop all further processing<br/>
         * (this also means that no {@link EntityPlacer} will be invoked)
         */
        CANCEL_TELEPORT,
        /**
         * if you want the default {@link EntityPlacer} to handle placement of the entity
         */
        DEFAULT
    }
}
