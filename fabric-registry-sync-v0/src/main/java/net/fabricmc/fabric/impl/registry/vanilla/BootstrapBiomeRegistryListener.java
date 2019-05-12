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

package net.fabricmc.fabric.impl.registry.vanilla;

import net.fabricmc.fabric.impl.registry.ExtendedIdList;
import net.fabricmc.fabric.impl.registry.RegistryListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BootstrapBiomeRegistryListener implements RegistryListener<Biome> {
	@Override
	public void beforeRegistryCleared(Registry<Biome> registry) {
		((ExtendedIdList) Biome.PARENT_BIOME_ID_MAP).clear();
	}

	@Override
	public void beforeRegistryRegistration(Registry<Biome> registry, int id, Identifier identifier, Biome object, boolean isNew) {
		// refer net.minecraft.biome.Biomes
		if (object.hasParent()) {
			Biome.PARENT_BIOME_ID_MAP.set(object, id);
		}
	}
}
