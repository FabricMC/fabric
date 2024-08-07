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

package net.fabricmc.fabric.impl.modprotocol;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

public interface RemoteProtocolStorage {
	static int getProtocol(Object object, Identifier identifier) {
		if (object instanceof RemoteProtocolStorage storage) {
			Object2IntMap<Identifier> map = storage.fabric$getRemoteProtocol();

			if (map != null) {
				return map.getOrDefault(identifier, -1);
			}
		}

		return -1;
	}

	static Object2IntMap<Identifier> getMap(Object object) {
		if (object instanceof RemoteProtocolStorage storage) {
			Object2IntMap<Identifier> map = storage.fabric$getRemoteProtocol();

			if (map != null) {
				return Object2IntMaps.unmodifiable(map);
			}
		}

		return Object2IntMaps.emptyMap();
	}

	@Nullable
	Object2IntMap<Identifier> fabric$getRemoteProtocol();
	void fabric$setRemoteProtocol(Object2IntMap<Identifier> protocol);
}
