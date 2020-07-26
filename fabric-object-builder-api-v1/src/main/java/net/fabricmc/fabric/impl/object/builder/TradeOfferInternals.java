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

package net.fabricmc.fabric.impl.object.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import net.fabricmc.fabric.mixin.object.builder.TradeOffersAccessor;

public final class TradeOfferInternals {
	/**
	 * A copy of the original trade offers map.
	 */
	public static Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> DEFAULT_VILLAGER_OFFERS;
	public static Int2ObjectMap<TradeOffers.Factory[]> DEFAULT_WANDERING_TRADER_OFFERS;
	private static final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> VILLAGER_TRADE_FACTORIES = new HashMap<>();
	private static final Int2ObjectMap<TradeOffers.Factory[]> WANDERING_TRADER_FACTORIES = new Int2ObjectOpenHashMap<>();
	private TradeOfferInternals() {
	}

	public static void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<TradeOffers.Factory>> factory) {
		final List<TradeOffers.Factory> list = new ArrayList<>();
		factory.accept(list);

		final TradeOffers.Factory[] additionalEntries = list.toArray(new TradeOffers.Factory[0]);
		final Int2ObjectMap<TradeOffers.Factory[]> professionEntry = VILLAGER_TRADE_FACTORIES.computeIfAbsent(profession, p -> new Int2ObjectOpenHashMap<>());

		final TradeOffers.Factory[] currentEntries = professionEntry.computeIfAbsent(level, l -> new TradeOffers.Factory[0]);
		final TradeOffers.Factory[] newEntries = ArrayUtils.addAll(additionalEntries, currentEntries);
		professionEntry.put(level, newEntries);

		// Refresh the trades map
		TradeOfferInternals.refreshOffers();
	}

	public static void registerWanderingTraderOffers(int level, Consumer<List<TradeOffers.Factory>> factory) {
		final List<TradeOffers.Factory> list = new ArrayList<>();
		factory.accept(list);

		final TradeOffers.Factory[] additionalEntries = list.toArray(new TradeOffers.Factory[0]);
		final TradeOffers.Factory[] currentEntries = TradeOfferInternals.DEFAULT_WANDERING_TRADER_OFFERS.computeIfAbsent(level, key -> new TradeOffers.Factory[0]);

		// Merge current and new entries
		final TradeOffers.Factory[] newEntries = ArrayUtils.addAll(additionalEntries, currentEntries);
		TradeOfferInternals.DEFAULT_WANDERING_TRADER_OFFERS.put(level, newEntries);

		// Refresh the trades map
		TradeOfferInternals.refreshOffers();
	}

	public static void refreshOffers() {
		TradeOfferInternals.refreshVillagerOffers();
		TradeOfferInternals.refreshWanderingTraderOffers();
	}

	private static void refreshVillagerOffers() {
		final HashMap<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> trades = new HashMap<>(TradeOfferInternals.DEFAULT_VILLAGER_OFFERS);

		for (Map.Entry<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> tradeFactoryEntry : TradeOfferInternals.VILLAGER_TRADE_FACTORIES.entrySet()) {
			// Create an empty map or get all existing profession entries.
			final Int2ObjectMap<TradeOffers.Factory[]> leveledFactoryMap = trades.computeIfAbsent(tradeFactoryEntry.getKey(), k -> new Int2ObjectOpenHashMap<>());
			// Get the existing entries
			final Int2ObjectMap<TradeOffers.Factory[]> value = tradeFactoryEntry.getValue();

			// Iterate through the existing level entries
			for (int level : value.keySet()) {
				final TradeOffers.Factory[] factories = value.get(level);

				if (factories != null) {
					final Int2ObjectMap<TradeOffers.Factory[]> resultMap = trades.computeIfAbsent(tradeFactoryEntry.getKey(), key -> new Int2ObjectOpenHashMap<>());
					resultMap.put(level, ArrayUtils.addAll(leveledFactoryMap.computeIfAbsent(level, key -> new TradeOffers.Factory[0]), factories));
				}
			}
		}

		// Set the new villager trade map
		TradeOffersAccessor.setVillagerTradeMap(trades);
	}

	private static void refreshWanderingTraderOffers() {
		// Create an empty map that is a clone of the default offers
		final Int2ObjectMap<TradeOffers.Factory[]> trades = new Int2ObjectOpenHashMap<>(TradeOfferInternals.DEFAULT_WANDERING_TRADER_OFFERS);

		for (int level : TradeOfferInternals.WANDERING_TRADER_FACTORIES.keySet()) {
			// Get all registered offers and add them to current entries
			final TradeOffers.Factory[] factories = TradeOfferInternals.WANDERING_TRADER_FACTORIES.get(level);
			trades.put(level, ArrayUtils.addAll(factories, trades.computeIfAbsent(level, key -> new TradeOffers.Factory[0])));
		}

		// Set the new wandering trader trade map
		TradeOffersAccessor.setWanderingTraderTradeMap(trades);
	}

	static {
		// Load the trade offers class so the field is set.
		TradeOffers.PROFESSION_TO_LEVELED_TRADE.getClass();
	}
}
