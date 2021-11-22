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

package net.fabricmc.fabric.impl.structure;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;

import net.fabricmc.fabric.api.structure.v1.FabricStructurePool;
import net.fabricmc.fabric.mixin.structure.StructurePoolAccessor;

public class FabricStructurePoolImpl implements FabricStructurePool {
	private final StructurePool pool;

	public FabricStructurePoolImpl(StructurePool pool) {
		this.pool = pool;
	}

	@Override
	public void addStructurePoolElement(StructurePoolElement element) {
		addStructurePoolElement(element, 1);
	}

	@Override
	public void addStructurePoolElement(StructurePoolElement element, int weight) {
		//adds to elementCounts list; minecraft makes these immutable lists, so we replace them with an array list
		StructurePoolAccessor pool = (StructurePoolAccessor) getUnderlyingPool();

		if (pool.getElementCounts() instanceof ArrayList) {
			pool.getElementCounts().add(Pair.of(element, weight));
		} else {
			List<Pair<StructurePoolElement, Integer>> list = new ArrayList<>(pool.getElementCounts());
			list.add(Pair.of(element, weight));
			pool.setElementCounts(list);
		}

		//adds to elements list
		for (int i = 0; i < weight; i++) {
			pool.getElements().add(element);
		}
	}

	@Override
	public StructurePool getUnderlyingPool() {
		return pool;
	}
}
