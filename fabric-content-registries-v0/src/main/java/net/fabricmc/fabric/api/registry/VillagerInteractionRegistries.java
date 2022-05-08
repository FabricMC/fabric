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
import java.util.Set;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.api.util.ImmutableCollectionRedirect;
import net.fabricmc.fabric.api.util.ImmutableMapRedirect;
import net.fabricmc.fabric.impl.content.registry.VillagerPlantableRegistryImpl;
import net.fabricmc.fabric.mixin.content.registry.FarmerWorkTaskAccessor;
import net.fabricmc.fabric.mixin.content.registry.GiveGiftsToHeroTaskAccessor;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;

public class VillagerInteractionRegistries {
	/**
	 * Registry for controlling items that all villagers can
	 * pick up from the ground.
	 */
	public static final ImmutableCollectionRedirect<Item, Set<Item>> COLLECTABLE_REGISTRY = new ImmutableCollectionRedirect<>(VillagerEntityAccessor::getGatherableItems, VillagerEntityAccessor::setGatherableItems);

	/**
	 * Registry for controlling items that farmer villagers can use in a composter.
	 * Items also have to be registered as a compostable item.
	 *
	 * @see CompostingChanceRegistry
	 */
	public static final ImmutableCollectionRedirect<Item, List<Item>> COMPOSTABLE_REGISTRY = new ImmutableCollectionRedirect<>(FarmerWorkTaskAccessor::getCompostables, FarmerWorkTaskAccessor::setCompostables);

	/**
	 * Registry for controlling which items villagers see as valid food items,
	 * and how much breeding power they have.
	 */
	public static final ImmutableMapRedirect<Item, Integer> FOOD_REGISTRY = new ImmutableMapRedirect<>(() -> VillagerEntity.ITEM_FOOD_VALUES, VillagerEntityAccessor::setItemFoodValues);

	/**
	 * Registry for controlling the loot tables used by villagers when gifting
	 * items to a player with hero of the village.
	 */
	public static final ImmutableMapRedirect<VillagerProfession, Identifier> HERO_GIFT_REGISTRY = new ImmutableMapRedirect<>(GiveGiftsToHeroTaskAccessor::getGifts, villagerProfessionIdentifierMap -> { });

	/**
	 * Registry of items that farmer villagers can plant on farmland.
	 */
	public static final VillagerPlantableRegistryImpl PLANTABLE_REGISTRY = new VillagerPlantableRegistryImpl();
}
