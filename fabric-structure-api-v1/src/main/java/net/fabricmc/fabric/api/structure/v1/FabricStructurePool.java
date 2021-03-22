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

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.mixin.structure.StructurePoolAccessor;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a modifiable structure pool that would have several helper methods for modders.
 */
public class FabricStructurePool {
	private final StructurePool pool;

	public FabricStructurePool(StructurePool underlying) {
		this.pool = underlying;
	}

	public void addStructurePoolElement(StructurePoolElement element) {
		addStructurePoolElement(element, 1);
	}

	public void addStructurePoolElement(StructurePoolElement element, int weight) {
		//adds to elementCounts list
		List<Pair<StructurePoolElement, Integer>> list = new ArrayList<>(((StructurePoolAccessor) pool).getElementCounts());
		list.add(Pair.of(element, weight));
		((StructurePoolAccessor) pool).setElementCounts(list);

		//adds to elements list
		for (int i = 0; i < weight; i++) {
			((StructurePoolAccessor) pool).getElements().add(element);
		}
	}

	public StructurePool getStructurePool() {
		return pool;
	}
}
