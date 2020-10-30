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

@Mixin(Biome.Weather.class)
public interface BiomeWeatherAccessor {
	@Accessor
	Biome.Precipitation getPrecipitation();

	@Accessor
	@Mutable
	void setPrecipitation(Biome.Precipitation precipitation);

	@Accessor
	float getTemperature();

	@Accessor
	@Mutable
	void setTemperature(float temperature);

	@Accessor
	Biome.TemperatureModifier getTemperatureModifier();

	@Accessor
	@Mutable
	void setTemperatureModifier(Biome.TemperatureModifier temperatureModifier);

	@Accessor
	float getDownfall();

	@Accessor
	@Mutable
	void setDownfall(float downfall);
}
