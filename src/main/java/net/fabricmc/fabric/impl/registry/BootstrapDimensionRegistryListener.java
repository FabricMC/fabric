/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.registry;

import net.fabricmc.fabric.impl.dimension.DimensionTypeExtensions;
import net.fabricmc.fabric.registry.RegistryListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class BootstrapDimensionRegistryListener implements RegistryListener<DimensionType> {

	@Override
	public void beforeRegistryRegistration(Registry<DimensionType> registry, int id, Identifier identifier, DimensionType object, boolean isNew) {
		DimensionTypeExtensions extensions = (DimensionTypeExtensions) object;
		extensions.fabric_setId(id);
	}
}
