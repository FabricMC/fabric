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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.biomes.v1.BiomeClimate;
import net.fabricmc.fabric.api.biomes.v1.RiverAssociate;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

public final class BiomeLists
{
	private BiomeLists() {}
	
	public static final Map<Biome, BiomeAssociate> HILLS_MAP = new HashMap<>();
	public static final Map<Biome, BiomeAssociate> SHORE_MAP = new HashMap<>();
	public static final Map<Biome, BiomeAssociate> EDGE_MAP = new HashMap<>();
	public static final Map<Biome, RiverAssociate> RIVER_MAP = new HashMap<>();
	
	public static final List<Pair<Integer, BiomeClimate>> INJECTED_BIOME_LIST = new ArrayList<>();
	public static final Map<BiomeClimate, Map<Biome, Integer>> BIOME_WEIGHT_LOOKUP = new HashMap<>();
	
	public static final Set<Biome> CUSTOM_BIOMES = new HashSet<>();
	
	public static final Set<Biome> SPAWN_BIOMES = new HashSet<>();
}
