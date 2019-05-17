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

package net.fabricmc.fabric.impl.registry.trackers.vanilla;

import net.fabricmc.fabric.impl.registry.RemovableIdList;
import net.fabricmc.fabric.impl.registry.ListenableRegistry;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPostRegisterCallback;
import net.fabricmc.fabric.impl.registry.callbacks.RegistryPreClearCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public final class BiomeParentTracker implements RegistryPreClearCallback<Biome>, RegistryPostRegisterCallback<Biome> {
	private final Registry<Biome> registry;

	private BiomeParentTracker(Registry<Biome> registry) {
		this.registry = registry;
	}

	public static void register(Registry<Biome> registry) {
		BiomeParentTracker tracker = new BiomeParentTracker(registry);
		((ListenableRegistry<Biome>) registry).getPreClearEvent().register(tracker);
		((ListenableRegistry<Biome>) registry).getPostRegisterEvent().register(tracker);
	}

	@Override
	public void onPostRegister(int rawId, Identifier id, Biome object) {
		if (object.hasParent()) {
			Biome.PARENT_BIOME_ID_MAP.set(object, registry.getRawId(registry.get(new Identifier(object.getParent()))));
		}
	}

	@Override
	public void onPreClear() {
		((RemovableIdList) Biome.PARENT_BIOME_ID_MAP).clear();
	}
}
