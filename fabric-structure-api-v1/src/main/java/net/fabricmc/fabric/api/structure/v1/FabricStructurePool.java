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

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;

import net.fabricmc.fabric.mixin.structure.StructurePoolAccessor;

/**
 * Represents a modifiable structure pool that has several helper methods for modders.
 */
public class FabricStructurePool {
	private final StructurePool underlying;

	public FabricStructurePool(StructurePool underlying) {
		this.underlying = underlying;
	}

	/**
	 * Adds a new {@link StructurePoolElement} to the {@link StructurePool}.
	 * See the alternative {@link #addStructurePoolElement(StructurePoolElement, int)} for details.
	 *
	 * @param element The element you want to add.
	 */
	public void addStructurePoolElement(StructurePoolElement element) {
		addStructurePoolElement(element, 1);
	}

	/**
	 * Adds a new {@link StructurePoolElement} to the {@link StructurePool}.
	 *
	 * @param element The element you want to add.
	 * @param weight  Minecraft handles weight by adding it that amount of times into the StructurePool#elements.
	 */
	public void addStructurePoolElement(StructurePoolElement element, int weight) {
		//adds to elementCounts list; minecraft makes these immutable lists so we replace them with an array list
		if (((StructurePoolAccessor) underlying).getElementCounts() instanceof ArrayList) {
			((StructurePoolAccessor) underlying).getElementCounts().add(Pair.of(element, weight));
		} else {
			List<Pair<StructurePoolElement, Integer>> list = new ArrayList<>(((StructurePoolAccessor) underlying).getElementCounts());
			list.add(Pair.of(element, weight));
			((StructurePoolAccessor) underlying).setElementCounts(list);
		}


		//adds to elements list
		for (int i = 0; i < weight; i++) {
			((StructurePoolAccessor) underlying).getElements().add(element);
		}
	}

	/**
	 * Allows you to get the {@link StructurePool} itself.
	 *
	 * @return The underlying {@link StructurePool}.
	 */
	public StructurePool getUnderlying() {
		return underlying;
	}
}
