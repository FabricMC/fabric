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

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;

/**
 * Represents a modifiable structure pool that has several helper methods for modders.
 */
public interface FabricStructurePool {
	/**
	 * Adds a new {@link StructurePoolElement} to the {@link StructurePool}.
	 * See the alternative {@link #addStructurePoolElement(StructurePoolElement, int)} for details.
	 *
	 * @param element the element to add
	 */
	void addStructurePoolElement(StructurePoolElement element);

	/**
	 * Adds a new {@link StructurePoolElement} to the {@link StructurePool}.
	 * Its weight determines the amount of times an element is added to a list used for sampling during structure generation.
	 *
	 * @param element the element to add
	 * @param weight  the weight of the element
	 */
	void addStructurePoolElement(StructurePoolElement element, int weight);

	/**
	 * Gets the underlying structure pool.
	 */
	StructurePool getUnderlyingPool();

	/**
	 * Gets the identifier for the pool.
	 */
	default Identifier getId() {
		return getUnderlyingPool().getId();
	}
}
