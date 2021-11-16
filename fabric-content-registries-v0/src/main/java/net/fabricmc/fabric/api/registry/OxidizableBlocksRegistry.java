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

package net.fabricmc.fabric.api.registry;

import net.minecraft.block.Oxidizable;

import net.fabricmc.fabric.api.util.OxidizableFamily;

public class OxidizableBlocksRegistry {
	/**
	 * Registers multiple {@link OxidizableFamily}s.
	 *
	 * @param families the families to register
	 */
	public static void registerFamilies(OxidizableFamily... families) {
		for (OxidizableFamily family : families) {
			registerFamily(family);
		}
	}

	/**
	 * Registers multiple {@link OxidizableFamily}s.
	 *
	 * @param families the families to register
	 */
	public static void registerFamilies(Iterable<OxidizableFamily> families) {
		for (OxidizableFamily family : families) {
			registerFamily(family);
		}
	}

	/**
	 * Registers an {@link OxidizableFamily}.
	 *
	 * @param family the {@link OxidizableFamily} to register
	 */
	public static void registerFamily(OxidizableFamily family) {
		Oxidizable.OXIDATION_LEVEL_INCREASES.get().putAll(family.oxidizationLevelIncreasesMap());
		Oxidizable.OXIDATION_LEVEL_DECREASES.get().putAll(family.oxidizationLevelDecreasesMap());
		WaxableBlocksRegistry.registerWaxablePairs(family.waxableBlockPairs());
	}
}
