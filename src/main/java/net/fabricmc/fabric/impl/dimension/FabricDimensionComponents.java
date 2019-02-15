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
import net.minecraft.entity.Entity;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

//INTERNAL ONLY, dont use this modders, use the APIs provided
public class FabricDimensionComponents {

	public static final FabricDimensionComponents INSTANCE = new FabricDimensionComponents();

	private List<DimensionType> moddedDimensionTypes = new ArrayList<>();
	private Map<DimensionType, EntityTeleporter> dimensionTeleporters = new HashMap<>();


	public void addModdedDimension(DimensionType type, EntityTeleporter teleporter){
		moddedDimensionTypes.add(type);
		if(teleporter != null){
			dimensionTeleporters.put(type, teleporter);
		}
	}

	public List<DimensionType> getModdedDimensionTypes(){
		return Collections.unmodifiableList(moddedDimensionTypes);
	}

	public EntityTeleporter getTeleporter(Entity entity, DimensionType dimensionType){
		//TODO have an event or something to allow devs to handle any teleportation?
		//Check the dimension teleporters to see if it can be handled, this would have been set when creating the dim
		if(dimensionTeleporters.containsKey(dimensionType)){
			return dimensionTeleporters.get(dimensionType);
		}
		//If its a modded dim, we want to force the default teleporter otherwise bad things can happen
		if(getModdedDimensionTypes().contains(dimensionType)){
			return EntityTeleporter.DEFAULT_TELEPORTER;
		}
		return null;
	}
}
