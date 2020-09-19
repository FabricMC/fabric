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

package net.fabricmc.fabric.api.object.builder.v1.villager;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.mixin.object.builder.VillagerTypeAccessor;

/**
 * Utilities related to the creation of {@link VillagerType}s.
 * Not to be confused with a {@link VillagerProfession}, a villager type defines the appearance of a villager.
 *
 * <p>Creation of custom villager types may be done via the use of a simple helper method.
 * Below is an example of how one wouldd create a villager type:
 * <blockquote><pre>
 * // Note: You should register your villager types via the corresponding registry
 * public static final VillagerType MOUNTAIN = VillagerTypeHelper.create(new Identifier("mymod", "mountain"));
 * </pre></blockquote>
 *
 * <p>Creation and registration of a villager type does not guarantee villagers of a specific type will be created in a world.
 * Typically the villager type is bound to a specific group of biomes.
 * Below is an example of allowing creation of mountain villagers in a mountain biome:
 * <blockquote><pre>
 * VillagerTypeHelper.addVillagerTypeToBiome(BiomeKeys.MOUNTAIN, MyVillagerTypes.MOUNTAIN);
 * </pre></blockquote>
 */
public final class VillagerTypeHelper {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Creates a new villager type.
	 *
	 * @param id the id of the villager type
	 * @return a new villager type
	 */
	public static VillagerType create(Identifier id) {
		Objects.requireNonNull(id, "Id of villager type cannot be null");
		return VillagerTypeAccessor.callCreate(id.toString());
	}

	/**
	 * Sets the biome a villager type can spawn in.
	 *
	 * @param biomeKey the registry key of the biome
	 * @param villagerType the villager type
	 */
	public static void addVillagerTypeToBiome(RegistryKey<Biome> biomeKey, VillagerType villagerType) {
		Objects.requireNonNull(biomeKey, "Biome registry key cannot be null");
		Objects.requireNonNull(villagerType, "Villager type cannot be null");

		if (VillagerTypeAccessor.getBiomeTypeToIdMap().put(biomeKey, villagerType) != null) {
			LOGGER.debug("Overriding existing Biome -> VillagerType registration for Biome {}", biomeKey.getValue().toString());
		}
	}

	private VillagerTypeHelper() {
	}
}
