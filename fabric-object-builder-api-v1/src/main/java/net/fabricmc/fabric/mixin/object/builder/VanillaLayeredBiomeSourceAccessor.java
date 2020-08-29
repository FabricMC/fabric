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

<<<<<<< HEAD:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/EndRegion.java
package net.fabricmc.fabric.impl.biome;

/**
 * Represents the different regions of the biomes of the end.
 */
public enum EndRegion {
	/**
	 * Corresponds to the central end island and the surrounding empty space.
	 */
	MAIN_ISLAND,
	/**
	 * Corresponds to the End Highlands biome.
	 */
	HIGHLANDS,
	/**
	 * Corresponds to the End Midlands biome.
	 */
	MIDLANDS,
	/**
	 * Corresponds to the End Barrens biome.
	 */
	BARRENS,
	/**
	 * Corresponds to the Small End Islands biome.
	 */
	SMALL_ISLANDS
=======
package net.fabricmc.fabric.mixin.biome;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;

/**
 * This accessor gives us access to the hardcoded list of overworld biomes.
 */
@Mixin(VanillaLayeredBiomeSource.class)
public interface VanillaLayeredBiomeSourceAccessor {
	@Accessor
	static List<RegistryKey<Biome>> getBIOMES() {
		throw new AssertionError("mixin");
	}

	@Accessor
	static void setBIOMES(List<RegistryKey<Biome>> biomes) {
		throw new AssertionError("mixin");
	}
>>>>>>> 7a4deef8... Ported 1.16.1 biomes-api-v1 to 1.16.2.:fabric-object-builder-api-v1/src/main/java/net/fabricmc/fabric/mixin/object/builder/VanillaLayeredBiomeSourceAccessor.java
}
