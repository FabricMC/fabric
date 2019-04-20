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
 
package net.fabricmc.fabric.api.registry;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class BiomeRegistry
{
	
	//Mostly to initialise MC biomes before custom biomes
	//If you can think of another way to do this please let me know
	public static final Biome defaultBiome = Biomes.PLAINS;
	
	/**
	* @param biome the biome to be registered
	* @param ID The biome ID. In the form modid:name
	*/
	public static Biome register(Biome biome, String ID)
	{	
		
		Registry.register(Registry.BIOME, ID, biome);
		
		if (biome.hasParent())
		{
			Biome.PARENT_BIOME_ID_MAP.set(biome, Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier(biome.getParent()))));
		}

		return biome;
	}
	
	public static boolean isIdTaken(int id)
	{
		return !(Registry.BIOME.get(id) == null);
	}
}
