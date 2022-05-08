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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.api.util.ImmutableCollectionUtils;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;
import net.fabricmc.fabric.mixin.content.registry.FarmerWorkTaskAccessor;
import net.fabricmc.fabric.mixin.content.registry.GiveGiftsToHeroTaskAccessor;

/**
 * Registries for modifying villager interactions that
 * villagers have with the world.
 * The registries are:
 *
 * @see VillagerPlantableRegistry for registering plants that farmers can plant
 */
public class VillagerInteractionRegistries {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerInteractionRegistries.class);

	/**
	 * Registers an item to be collectable (picked up from item entity)
	 * by any profession villagers.
	 *
	 * @param item the item to register
	 */
	public static void registerCollectable(ItemConvertible item) {
		Objects.requireNonNull(item, "Item cannot be null!");
		getCollectableRegistry().add(item.asItem());
	}

	/**
	 * Registers an item to be use in a composter by farmer villagers.
	 * @param item the item to register
	 */
	public static void registerCompostable(ItemConvertible item) {
		Objects.requireNonNull(item, "Item cannot be null!");
		getCompostableRegistry().add(item.asItem());
	}

	/**
	 * Registers an item to be edible by villagers.
	 * @param item      the item to register
	 * @param foodValue the amount of breeding power the item has (1 = normal food item, 4 = bread)
	 */
	public static void registerFood(ItemConvertible item, int foodValue) {
		Objects.requireNonNull(item, "Item cannot be null!");
		Objects.requireNonNull(item, "Food value cannot be null!");
		Integer oldValue = getFoodRegistry().put(item.asItem(), foodValue);

		if (oldValue != null) {
			LOGGER.info("Overriding previous food value of {}, was: {}, now: {}", item.asItem().toString(), oldValue, foodValue);
		}
	}

	/**
	 * Registers a hero of the village gifts loot table to a profession.
	 * @param profession the profession to modify
	 * @param lootTable  the loot table to associate with the profession
	 */
	public static void registerGiftLootTable(VillagerProfession profession, Identifier lootTable) {
		Objects.requireNonNull(profession, "Profession cannot be null!");
		Identifier oldValue = GiveGiftsToHeroTaskAccessor.getGifts().put(profession, lootTable);

		if (oldValue != null) {
			LOGGER.info("Overriding previous loot table of {} progession, was: {}, now: {}", profession.getId(), oldValue, lootTable);
		}
	}

	private static Set<Item> getCollectableRegistry() {
		return ImmutableCollectionUtils.getAsMutableSet(VillagerEntityAccessor::getGatherableItems, VillagerEntityAccessor::setGatherableItems);
	}

	private static List<Item> getCompostableRegistry() {
		return ImmutableCollectionUtils.getAsMutableList(FarmerWorkTaskAccessor::getCompostables, FarmerWorkTaskAccessor::setCompostables);
	}

	private static Map<Item, Integer> getFoodRegistry() {
		return ImmutableCollectionUtils.getAsMutableMap(() -> VillagerEntity.ITEM_FOOD_VALUES, VillagerEntityAccessor::setItemFoodValues);
	}
}
