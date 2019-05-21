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

package net.fabricmc.fabric.api.biomes.v1;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

/**
 * Class which contains an instance of a biome, for use specifying the river type. <br/> <br/>
 * 
 * This class functions mostly like a container for biomes, however
 * the instance RiverAssociate.NONE has a special function.
 */
public class RiverAssociate
{
	/**
	 *  RiverAssociate with special function. This specifies no river to generate.
	 */
	public static final RiverAssociate NONE = new RiverAssociate(null);
	
	/**
	 *  Normal, default river biome
	 */
	public static final RiverAssociate WATER = new RiverAssociate(Biomes.RIVER);
	
	/**
	 *  Frozen river biome
	 */
	public static final RiverAssociate FROZEN = new RiverAssociate(Biomes.FROZEN_RIVER);
	
	private final int biome;
	
	public RiverAssociate(Biome biome)
	{
		this.biome = Registry.BIOME.getRawId(biome);
	}
	
	public int getBiome()
	{
		return biome;
	}
}
