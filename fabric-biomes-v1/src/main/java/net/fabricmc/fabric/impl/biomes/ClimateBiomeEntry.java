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

package net.fabricmc.fabric.impl.biomes;

import net.fabricmc.fabric.api.biomes.v1.Climate;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class ClimateBiomeEntry
{
	private Biome biome;
	private Climate climate;
	
	public ClimateBiomeEntry(final Biome biome, final Climate climate)
	{
		this.biome = biome;
		this.climate = climate;
	}
	
	public Biome getBiome()
	{
		return biome;
	}
	
	public Climate getClimate()
	{
		return climate;
	}
	
	public int getRawId()
	{
		return Registry.BIOME.getRawId(biome);
	}
}
