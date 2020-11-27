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

package net.fabricmc.fabric.mixin.biome.modification;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

@Mixin(Biome.class)
public interface BiomeAccessor {
	@Accessor("weather")
	Biome.Weather fabric_getWeather();

	@Accessor("generationSettings")
	GenerationSettings fabric_getGenerationSettings();

	@Accessor("spawnSettings")
	@Mutable
	SpawnSettings fabric_getSpawnSettings();

	@Accessor("depth")
	@Mutable
	void fabric_setDepth(float depth);

	@Accessor("scale")
	@Mutable
	void fabric_setScale(float scale);

	@Accessor("category")
	@Mutable
	void fabric_setCategory(Biome.Category category);
}
