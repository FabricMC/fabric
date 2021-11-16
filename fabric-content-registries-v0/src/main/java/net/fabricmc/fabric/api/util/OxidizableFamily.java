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

package net.fabricmc.fabric.api.util;

import static net.minecraft.block.Oxidizable.OxidizationLevel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;

/**
 * Represents a 'family' of blocks that can be affected by Oxidization.
 *
 * <p>Like vanilla's Copper Blocks, these come in four levels of Oxidization;
 * Unaffected, Exposed, Weathered, and Oxidized.
 *
 * <p>Each block also has a 'Waxed' form.
 */
public record OxidizableFamily(
		Map<OxidizationLevel, WaxableBlockPair> blocks) {
	public OxidizableFamily(Map<OxidizationLevel, WaxableBlockPair> blocks) {
		for (OxidizationLevel level : OxidizationLevel.values()) {
			WaxableBlockPair pair = blocks.get(level);
			Objects.requireNonNull(pair, "OxidizableFamily is missing variant for " + level + "!");
			Objects.requireNonNull(pair.unwaxed(), "OxidizableFamily is missing unwaxed variant for " + level + "!");
			Objects.requireNonNull(pair.waxed(), "OxidizableFamily is missing waxed variant for " + level + "!");
		}

		this.blocks = ImmutableMap.copyOf(blocks);
	}

	/**
	 * Get the {@link WaxableBlockPair} in this family for the given {@link OxidizationLevel}.
	 *
	 * @param level the {@link OxidizationLevel}
	 * @return the waxable block pair
	 */
	public WaxableBlockPair waxableBlockPair(OxidizationLevel level) {
		return blocks().get(level);
	}

	/**
	 * Get all {@link WaxableBlockPair}s in this family.
	 *
	 * @return the waxable block pairs
	 */
	public Collection<WaxableBlockPair> waxableBlockPairs() {
		return blocks().values();
	}

	/**
	 * Get the unwaxed variant in this family for the given {@link OxidizationLevel}.
	 *
	 * @param level the {@link OxidizationLevel}
	 * @return the unwaxed variant
	 */
	public Block unwaxed(OxidizationLevel level) {
		return waxableBlockPair(level).unwaxed();
	}

	/**
	 * Builds a map of the unwaxed variants in this family.
	 *
	 * @return the map
	 */
	public Map<OxidizationLevel, Block> unwaxed() {
		return ImmutableMap.<OxidizationLevel, Block>builder()
				.put(OxidizationLevel.UNAFFECTED, unwaxed(OxidizationLevel.UNAFFECTED))
				.put(OxidizationLevel.EXPOSED, unwaxed(OxidizationLevel.EXPOSED))
				.put(OxidizationLevel.WEATHERED, unwaxed(OxidizationLevel.WEATHERED))
				.put(OxidizationLevel.OXIDIZED, unwaxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Get the waxed variant in this family for the given {@link OxidizationLevel}.
	 *
	 * @param level the {@link OxidizationLevel}
	 * @return the waxed variant
	 */
	public Block waxed(OxidizationLevel level) {
		return waxableBlockPair(level).waxed();
	}

	/**
	 * Builds a map of the waxed variants in this family.
	 *
	 * @return the map
	 */
	public Map<OxidizationLevel, Block> waxed() {
		return ImmutableMap.<OxidizationLevel, Block>builder()
				.put(OxidizationLevel.UNAFFECTED, waxed(OxidizationLevel.EXPOSED))
				.put(OxidizationLevel.EXPOSED, waxed(OxidizationLevel.WEATHERED))
				.put(OxidizationLevel.WEATHERED, waxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Builds a map of {@link OxidizationLevel} increases.
	 *
	 * @return the map
	 */
	public BiMap<Block, Block> oxidizationLevelIncreasesMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(unwaxed(OxidizationLevel.UNAFFECTED), unwaxed(OxidizationLevel.EXPOSED))
				.put(unwaxed(OxidizationLevel.EXPOSED), unwaxed(OxidizationLevel.WEATHERED))
				.put(unwaxed(OxidizationLevel.WEATHERED), unwaxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Builds a map of {@link OxidizationLevel} decreases.
	 *
	 * @return the map
	 */
	public BiMap<Block, Block> oxidizationLevelDecreasesMap() {
		return oxidizationLevelIncreasesMap().inverse();
	}

	/**
	 * Builds a map of unwaxed forms to waxed counterparts.
	 *
	 * @return the map
	 */
	public BiMap<Block, Block> unwaxedToWaxedMap() {
		return ImmutableBiMap.<Block, Block>builder()
				.put(unwaxed(OxidizationLevel.UNAFFECTED), waxed(OxidizationLevel.UNAFFECTED))
				.put(unwaxed(OxidizationLevel.EXPOSED), waxed(OxidizationLevel.EXPOSED))
				.put(unwaxed(OxidizationLevel.WEATHERED), waxed(OxidizationLevel.WEATHERED))
				.put(unwaxed(OxidizationLevel.OXIDIZED), waxed(OxidizationLevel.OXIDIZED))
				.build();
	}

	/**
	 * Builds a map of waxed forms to unwaxed counterparts.
	 *
	 * @return the map
	 */
	public BiMap<Block, Block> waxedToUnwaxedMap() {
		return unwaxedToWaxedMap().inverse();
	}

	/**
	 * Allows for the creation of {@link OxidizableFamily}s.
	 */
	public static class Builder {
		private static final Logger LOGGER = LogManager.getLogger();

		private final HashMap<OxidizationLevel, WaxableBlockPair> blocks = new HashMap<>();

		/**
		 * Adds blocks to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param level   the {@link OxidizationLevel} of the blocks to add
		 * @param unwaxed the unwaxed variant
		 * @param waxed   the waxed variant
		 * @return this builder
		 * @see #add(OxidizationLevel, WaxableBlockPair)
		 */
		public Builder add(OxidizationLevel level, Block unwaxed, Block waxed) {
			return add(level, new WaxableBlockPair(unwaxed, waxed));
		}

		/**
		 * Adds blocks to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param level  the {@link OxidizationLevel} of the blocks to add
		 * @param blocks the blocks to add
		 * @return this builder
		 * @see #add(OxidizationLevel, Block, Block)
		 */
		public Builder add(OxidizationLevel level, WaxableBlockPair blocks) {
			if (!(blocks.unwaxed() instanceof Oxidizable)) {
				LOGGER.warn("Block " + blocks.unwaxed() + " is not oxidizable, but added to OxidizableFamily as unwaxed block. This is likely an error!");
			}

			this.blocks.put(level, blocks);
			return this;
		}

		/**
		 * Adds blocks of the 'unaffected' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param unwaxed the unwaxed variant
		 * @param waxed   the waxed variant
		 * @return this builder
		 * @see #unaffected(WaxableBlockPair)
		 */
		public Builder unaffected(Block unwaxed, Block waxed) {
			return unaffected(new WaxableBlockPair(unwaxed, waxed));
		}

		/**
		 * Adds blocks of the 'unaffected' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param blocks the blocks to add
		 * @return this builder
		 * @see #unaffected(Block, Block)
		 */
		public Builder unaffected(WaxableBlockPair blocks) {
			return add(OxidizationLevel.UNAFFECTED, blocks);
		}

		/**
		 * Adds blocks of the 'weathered' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param unwaxed the unwaxed variant
		 * @param waxed   the waxed variant
		 * @return this builder
		 * @see #weathered(WaxableBlockPair)
		 */
		public Builder weathered(Block unwaxed, Block waxed) {
			return weathered(new WaxableBlockPair(unwaxed, waxed));
		}

		/**
		 * Adds blocks of the 'weathered' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param blocks the blocks to add
		 * @return this builder
		 * @see #weathered(Block, Block)
		 */
		public Builder weathered(WaxableBlockPair blocks) {
			return add(OxidizationLevel.WEATHERED, blocks);
		}

		/**
		 * Adds blocks of the 'exposed' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param unwaxed the unwaxed variant
		 * @param waxed   the waxed variant
		 * @return this builder
		 * @see #exposed(WaxableBlockPair)
		 */
		public Builder exposed(Block unwaxed, Block waxed) {
			return exposed(new WaxableBlockPair(unwaxed, waxed));
		}

		/**
		 * Adds blocks of the 'exposed' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param blocks the blocks to add
		 * @return this builder
		 * @see #exposed(Block, Block)
		 */
		public Builder exposed(WaxableBlockPair blocks) {
			return add(OxidizationLevel.EXPOSED, blocks);
		}

		/**
		 * Adds blocks of the 'oxidized' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param unwaxed the unwaxed variant
		 * @param waxed   the waxed variant
		 * @return this builder
		 * @see #oxidized(WaxableBlockPair)
		 */
		public Builder oxidized(Block unwaxed, Block waxed) {
			return oxidized(new WaxableBlockPair(unwaxed, waxed));
		}

		/**
		 * Adds blocks of the 'oxidized' {@link OxidizationLevel} to the {@link OxidizableFamily}.
		 * Will output a warning to the log if the unwaxed variant is not an instance of {@link Oxidizable}
		 *
		 * @param blocks the blocks to add
		 * @return this builder
		 * @see #oxidized(Block, Block)
		 */
		public Builder oxidized(WaxableBlockPair blocks) {
			return add(OxidizationLevel.OXIDIZED, blocks);
		}

		/**
		 * Builds this {@link OxidizableFamily}.
		 *
		 * @return the {@link OxidizableFamily}
		 * @throws NullPointerException if any variants are missing or null
		 */
		public OxidizableFamily build() {
			return new OxidizableFamily(blocks);
		}
	}
}
