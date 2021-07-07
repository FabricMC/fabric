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

package net.fabricmc.fabric.api.structure.v1;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A callback for newly added structure pools.
 *
 * <p><strong>Word of warning</strong>: Mods may be editing on the structure pool from user configured data packs
 * instead of the builtin Minecraft or mod resources.
 *
 * <p>Example usage:
 * <pre>{@code
 * StructurePoolAddCallback.EVENT.register(structurePool -> {
 * 	if (structurePool.id().toString().equals("minecraft:village/common/butcher_animals")) {
 * 		structurePool.addStructurePoolElement(StructurePoolElement.ofLegacySingle("village/common/animals/pigs_1").apply(StructurePool.Projection.RIGID), 2);
 *    }
 * });}
 * </pre>
 */
public interface StructurePoolAddCallback {
	/**
	 * Called when structure pools are reloaded at data pack reload time.
	 */
	Event<StructurePoolAddCallback> EVENT = EventFactory.createArrayBacked(StructurePoolAddCallback.class,
			listeners -> initialPool -> {
				for (StructurePoolAddCallback listener : listeners) {
					listener.onAdd(initialPool);
				}
			}
	);

	void onAdd(FabricStructurePool initialPool);
}
