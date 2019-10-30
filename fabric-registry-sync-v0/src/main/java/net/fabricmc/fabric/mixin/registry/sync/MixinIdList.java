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

package net.fabricmc.fabric.mixin.registry.sync;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.util.IdList;

import net.fabricmc.fabric.impl.registry.sync.RemovableIdList;

@Mixin(IdList.class)
public class MixinIdList implements RemovableIdList<Object> {
	@Shadow
	private int nextId;
	@Shadow
	private IdentityHashMap<Object, Integer> idMap;
	@Shadow
	private List<Object> list;

	@Override
	public void fabric_clear() {
		nextId = 0;
		idMap.clear();
		list.clear();
	}

	@Unique
	private void fabric_removeInner(Object o) {
		int value = idMap.remove(o);
		list.set(value, null);

		while (nextId > 1 && list.get(nextId - 1) == null) {
			nextId--;
		}
	}

	@Override
	public void fabric_remove(Object o) {
		if (idMap.containsKey(o)) {
			fabric_removeInner(o);
		}
	}

	@Override
	public void fabric_removeId(int i) {
		List<Object> removals = new ArrayList<>();

		for (Object o : idMap.keySet()) {
			int j = idMap.get(o);

			if (i == j) {
				removals.add(o);
			}
		}

		removals.forEach(this::fabric_removeInner);
	}

	@Override
	public void fabric_remapId(int from, int to) {
		fabric_remapIds(Int2IntMaps.singleton(from, to));
	}

	@Override
	public void fabric_remapIds(Int2IntMap map) {
		// remap idMap
		idMap.replaceAll((a, b) -> map.get(b));

		// remap list
		nextId = 0;
		List<Object> oldList = new ArrayList<>(list);
		list.clear();

		for (int k = 0; k < oldList.size(); k++) {
			Object o = oldList.get(k);

			if (o != null) {
				int i = map.getOrDefault(k, k);

				while (list.size() <= i) {
					list.add(null);
				}

				list.set(i, o);

				if (nextId <= i) {
					nextId = i + 1;
				}
			}
		}
	}
}
