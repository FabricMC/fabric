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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.mixin.content.registry.GiveGiftsToHeroTaskAccessor;

/**
 * Registry for controlling the loot tables used by villagers when gifting
 * items to a player with hero of the village.
 */
public class VillagerHeroGiftRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerHeroGiftRegistry.class);

	/**
	 * Registers a hero of the village gifts loot table to a profession.
	 * @param profession the profession to modify
	 * @param lootTable  the loot table to associate with the profession
	 */
	public static void register(VillagerProfession profession, Identifier lootTable) {
		Objects.requireNonNull(profession, "Profession cannot be null!");
		Identifier oldValue = GiveGiftsToHeroTaskAccessor.getGifts().put(profession, lootTable);

		if (oldValue != null) {
			LOGGER.info("Overriding previous loot table of {} progession, was: {}, now: {}", profession.getId(), oldValue, lootTable);
		}
	}
}
