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
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.mixin.biome.VanillaLayeredBiomeSourceAccessor;

/**
 * Provides several convenient biome selectors that can be used with {@link BiomeModifications}.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 * Because of the volatility of world generation in Minecraft 1.16, this API is marked experimental
 * since it is likely to change in future Minecraft versions.
 */
@ApiStatus.Experimental
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
	 * assuming Vanilla's layered biome source is used.
	 *
	 * <p>This selector will also match modded biomes that have been added to the overworld using {@link OverworldBiomes}.
	 */
	public static Predicate<BiomeSelectionContext> foundInOverworld() {
		return context -> {
			RegistryKey<Biome> biomeKey = context.getBiomeKey();
			// BUG: Minecraft is missing BAMBOO_JUNGLE_HILLS/BAMBOO_JUNGLE in the VanillaLayeredBiome's source BIOME list, even
			// though they generate in the overworld.
			return biomeKey == BiomeKeys.BAMBOO_JUNGLE_HILLS
					|| biomeKey == BiomeKeys.BAMBOO_JUNGLE
					|| VanillaLayeredBiomeSourceAccessor.getBIOMES().contains(biomeKey);
		};
	}

	/**
	 * Returns a biome selector that will match all biomes that would normally spawn in the Nether,
	 * assuming Vanilla's default multi noise biome source with the nether preset is used.
	 *
	 * <p>This selector will also match modded biomes that have been added to the nether using {@link NetherBiomes}.
	 */
	public static Predicate<BiomeSelectionContext> foundInTheNether() {
		return context -> NetherBiomes.canGenerateInNether(context.getBiomeKey());
	}

	/**
	 * Returns a biome selector that will match all biomes that would normally spawn in the End,
	 * assuming Vanilla's default End biome source is used.
	 */
	public static Predicate<BiomeSelectionContext> foundInTheEnd() {
		return context -> context.getBiome().getCategory() == Biome.Category.THEEND;
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
}
