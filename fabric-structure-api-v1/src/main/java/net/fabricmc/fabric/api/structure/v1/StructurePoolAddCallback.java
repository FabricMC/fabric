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
 * 			if (structurePool.getId().equals(new Identifier("minecraft:village/desert/houses"))) {
 * 				structurePool.addStructurePoolElement(StructurePoolElement.ofProcessedLegacySingle("fabric:cactus_farm", StructureProcessorLists.FARM_PLAINS).apply(StructurePool.Projection.RIGID));
 *          }
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
