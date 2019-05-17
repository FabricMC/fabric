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

package net.fabricmc.fabric.mixin.registry;

import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.minecraft.util.IdList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.IdentityHashMap;
import java.util.List;

@Mixin(IdList.class)
public class MixinIdList implements RemovableIdList<Object> {
	@Shadow
	private int nextId;
	@Shadow
	private IdentityHashMap<Object, Integer> idMap;
	@Shadow
	private List<Object> list;

	@Override
	public void clear() {
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
	public void remove(Object o) {
		if (idMap.containsKey(o)) {
			fabric_removeInner(o);
		}
	}

	@Override
	public void removeId(int i) {
		Object obj = list.get(i);
		if (obj != null) {
			fabric_removeInner(obj);
		}
	}
}
