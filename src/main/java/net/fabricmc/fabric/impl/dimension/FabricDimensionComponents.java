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

package net.fabricmc.fabric.impl.dimension;

import net.fabricmc.fabric.api.dimension.EntityTeleporter;
import net.fabricmc.fabric.api.dimension.FabricTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

//INTERNAL ONLY, dont use this modders, use the APIs provided
public class FabricDimensionComponents implements FabricTeleporter {

	public static final FabricDimensionComponents INSTANCE = new FabricDimensionComponents();

	public EntityTeleporter NEXT_TELEPORTER = null;
	private List<DimensionType> moddedDimensionTypes = new ArrayList<>();
	private Map<DimensionType, EntityTeleporter> defaultTeleporters = new HashMap<>();


	public void addModdedDimension(DimensionType type, EntityTeleporter teleporter){
		moddedDimensionTypes.add(type);
		if(teleporter != null){
			defaultTeleporters.put(type, teleporter);
		}
	}

	public List<DimensionType> getModdedDimensionTypes(){
		return Collections.unmodifiableList(moddedDimensionTypes);
	}

	@Override
	public void changeDimension(Entity entity, DimensionType dimension, EntityTeleporter entityTeleporter) {
		if(entity.world.isClient){
			return;
		}
		if(entityTeleporter == null){
			return;
		}
		NEXT_TELEPORTER = entityTeleporter;
		entity.changeDimension(dimension);
	}

	@Override
	public void changeDimension(Entity entity, DimensionType dimensionType) {
		changeDimension(entity, dimensionType, defaultTeleporters.getOrDefault(dimensionType, EntityTeleporter.DEFAULT_TELEPORTER));
	}
}
