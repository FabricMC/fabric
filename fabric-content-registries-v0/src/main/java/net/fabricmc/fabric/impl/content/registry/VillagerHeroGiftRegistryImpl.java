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

package net.fabricmc.fabric.impl.content.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.api.registry.VillagerHeroGiftRegistry;
import net.fabricmc.fabric.mixin.content.registry.GiveGiftsToHeroTaskAccessor;

public class VillagerHeroGiftRegistryImpl implements VillagerHeroGiftRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerHeroGiftRegistry.class);

	@Override
	public void add(VillagerProfession profession, Identifier lootTable) {
		Identifier previousValue = GiveGiftsToHeroTaskAccessor.getGifts().put(profession, lootTable);

		if (previousValue != null) {
			LOGGER.info("Overriding previously defined gift loot table for {} profession, was: {}, now: {}", profession.getId(), profession, lootTable);
		}
	}

	@Override
	public Identifier remove(VillagerProfession profession) {
		return GiveGiftsToHeroTaskAccessor.getGifts().remove(profession);
	}

	@Override
	public Identifier get(VillagerProfession profession) {
		return GiveGiftsToHeroTaskAccessor.getGifts().get(profession);
	}
}
