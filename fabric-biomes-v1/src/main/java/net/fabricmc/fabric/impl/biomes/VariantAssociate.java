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
import java.util.List;

import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public final class VariantAssociate
{
	private final List<Pair<Biome, Integer>> variants = new ArrayList<>();
	
	public void addBiomeWithRarity(Biome b, int rarity)
	{
		variants.add(new Pair<>(b, rarity));
	}
	
	public Biome transformBiome(Biome biome, LayerRandomnessSource rand)
	{
		for(Pair<Biome, Integer> variant : variants)
			if (rand.nextInt(variant.getRight()) == 0)
				return variant.getLeft();
		
		return biome;
	}
}
