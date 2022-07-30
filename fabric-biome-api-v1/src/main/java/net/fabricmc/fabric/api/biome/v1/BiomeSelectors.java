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

package net.fabricmc.fabric.api.biome.v1;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.dimension.DimensionOptions;

/**
 * Provides several convenient biome selectors that can be used with {@link BiomeModifications}.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public final class BiomeSelectors {
	private BiomeSelectors() {
	}

	/**
	 * Matches all Biomes. Use a more specific selector if possible.
	 */
	public static Predicate<BiomeSelectionContext> all() {
		return context -> true;
	}

	/**
	 * Matches Biomes that have not been originally defined in a datapack, but that are defined in code.
	 */
	public static Predicate<BiomeSelectionContext> builtIn() {
		return context -> BuiltinRegistries.BIOME.containsId(context.getBiomeKey().getValue());
	}

	/**
	 * Returns a biome selector that will match all biomes from the minecraft namespace.
	 */
	public static Predicate<BiomeSelectionContext> vanilla() {
		return context -> {
			// In addition to the namespace, we also check that it doesn't come from a data pack.
			return context.getBiomeKey().getValue().getNamespace().equals("minecraft")
					&& BuiltinRegistries.BIOME.containsId(context.getBiomeKey().getValue());
		};
	}

	/**
	 * Returns a biome selector that will match all biomes that would normally spawn in the Overworld,
	 * assuming Vanilla's default biome source is used.
	 */
	public static Predicate<BiomeSelectionContext> foundInOverworld() {
		return context -> context.canGenerateIn(DimensionOptions.OVERWORLD);
	}

	/**
	 * Returns a biome selector that will match all biomes that would normally spawn in the Nether,
	 * assuming Vanilla's default multi noise biome source with the nether preset is used.
	 *
	 * <p>This selector will also match modded biomes that have been added to the nether using {@link NetherBiomes}.
	 */
	public static Predicate<BiomeSelectionContext> foundInTheNether() {
		return context -> context.canGenerateIn(DimensionOptions.NETHER);
	}

	/**
	 * Returns a biome selector that will match all biomes that would normally spawn in the End,
	 * assuming Vanilla's default End biome source is used.
	 */
	public static Predicate<BiomeSelectionContext> foundInTheEnd() {
		return context -> context.canGenerateIn(DimensionOptions.END);
	}

	/**
	 * Returns a biome selector that will match all biomes in the given tag.
	 *
	 * @see net.fabricmc.fabric.api.tag.TagFactory#BIOME
	 */
	public static Predicate<BiomeSelectionContext> tag(TagKey<Biome> tag) {
		return context -> context.hasTag(tag);
	}

	/**
	 * @see #excludeByKey(Collection)
	 */
	@SafeVarargs
	public static Predicate<BiomeSelectionContext> excludeByKey(RegistryKey<Biome>... keys) {
		return excludeByKey(ImmutableSet.copyOf(keys));
	}

	/**
	 * Returns a selector that will reject any biome whos keys is in the given collection of keys.
	 *
	 * <p>This is useful for allowing a list of biomes to be defined in the config file, where
	 * a certain feature should not spawn.
	 */
	public static Predicate<BiomeSelectionContext> excludeByKey(Collection<RegistryKey<Biome>> keys) {
		return context -> !keys.contains(context.getBiomeKey());
	}

	/**
	 * @see #includeByKey(Collection)
	 */
	@SafeVarargs
	public static Predicate<BiomeSelectionContext> includeByKey(RegistryKey<Biome>... keys) {
		return includeByKey(ImmutableSet.copyOf(keys));
	}

	/**
	 * Returns a selector that will accept only biomes whos keys are in the given collection of keys.
	 *
	 * <p>This is useful for allowing a list of biomes to be defined in the config file, where
	 * a certain feature should spawn exclusively.
	 */
	public static Predicate<BiomeSelectionContext> includeByKey(Collection<RegistryKey<Biome>> keys) {
		return context -> keys.contains(context.getBiomeKey());
	}

	/**
	 * Returns a biome selector that will match biomes in which one of the given entity types can spawn.
	 *
	 * <p>Matches spawns in all {@link SpawnGroup spawn groups}.
	 */
	public static Predicate<BiomeSelectionContext> spawnsOneOf(EntityType<?>... entityTypes) {
		return spawnsOneOf(ImmutableSet.copyOf(entityTypes));
	}

	/**
	 * Returns a biome selector that will match biomes in which one of the given entity types can spawn.
	 *
	 * <p>Matches spawns in all {@link SpawnGroup spawn groups}.
	 */
	public static Predicate<BiomeSelectionContext> spawnsOneOf(Set<EntityType<?>> entityTypes) {
		return context -> {
			SpawnSettings spawnSettings = context.getBiome().getSpawnSettings();

			for (SpawnGroup spawnGroup : SpawnGroup.values()) {
				for (SpawnSettings.SpawnEntry spawnEntry : spawnSettings.getSpawnEntries(spawnGroup).getEntries()) {
					if (entityTypes.contains(spawnEntry.type)) {
						return true;
					}
				}
			}

			return false;
		};
	}
}
