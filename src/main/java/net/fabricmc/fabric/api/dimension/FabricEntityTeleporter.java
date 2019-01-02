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

package net.fabricmc.fabric.api.dimension;

import net.fabricmc.fabric.impl.dimension.FabricDimensionComponents;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

//TODO find better names for these classes and methods
public interface FabricEntityTeleporter {

	FabricEntityTeleporter INSTANCE = FabricDimensionComponents.INSTANCE;

	/**
	 *
	 * Call this to change the entitys dimension
	 *
	 * @param entity the entity to move to a diffrent dimension
	 * @param dimension the target dimension
	 * @param entityTeleporter an instance of EntityTeleporter
	 */
	void changeDimension(Entity entity, DimensionType dimension, EntityTeleporter entityTeleporter);

}
